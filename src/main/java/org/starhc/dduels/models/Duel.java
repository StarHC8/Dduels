package org.starhc.dduels.models;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.starhc.dduels.Dduels;
import org.starhc.dduels.enums.DuelType;

import java.util.*;
import java.util.stream.Collectors;

public class Duel {
    private Dduels plugin;
    private DuelSession session;
    private List<UUID> players;
    private MapTemplate mapTemplate;
    private Kit kit;
    private World world;

    private List<UUID> alivePlayers;
    private List<UUID> spectators = new ArrayList<>();
    private List<UUID> deads = new ArrayList<>();
    private Map<UUID, Spawn> playersSpawns = new HashMap<>();
    private DuelType duelType;
    private boolean isActive = false;

    private List<UUID> teamA;
    private List<UUID> teamB;

    public Duel(Dduels plugin, DuelSession session) {
        this.plugin = plugin;
        this.session = session;

        this.players = new ArrayList<>(session.getAllPlayers());
        this.mapTemplate = session.getSelectedMapTemplate().get();
        this.kit = session.getSelectedKit().get();
        this.duelType = session.getDuelType();

        this.alivePlayers = new ArrayList<>(List.copyOf(players));
    }

    public List<UUID> getPlayers() {
        return players;
    }

    public List<UUID> getAlivePlayers() {
        return alivePlayers;
    }

    public List<UUID> getDeads() {
        return deads;
    }

    public List<UUID> getSpectators() {
        return spectators;
    }

    public List<UUID> getTeamA() {
        return teamA;
    }

    public List<UUID> getTeamB() {
        return teamB;
    }

    public List<UUID> getPlayerTeam(UUID playerUUID) {
        if (teamA.contains(playerUUID)) {
            return teamA;
        } else {
            return teamB;
        }
    }

    public MapTemplate getMapTemplate() {
        return mapTemplate;
    }

    public Kit getKit() {
        return kit;
    }

    public DuelType getDuelType() {
        return duelType;
    }

    public boolean isActive() {
        return isActive;
    }

    public void init() {
        for (UUID uuid : players) {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null) {
                player.sendMessage(plugin.getConfigHandler().getMessageFromConfig("loading-duel"));
            }
        }

        plugin.getWorldsHandler().createWorldFromTemplate(mapTemplate.getTemplateName()).thenAccept(createdWorld -> {
            if (createdWorld == null) {
                for (UUID uuid : players) {
                    Player player = Bukkit.getPlayer(uuid);
                    if (player != null) {
                        player.sendMessage(plugin.getConfigHandler().getMessageFromConfig("system-errors.world-error"));
                    }
                }
                plugin.getDuelHandler().deleteDuel(this);
                return;
            }
            this.world = createdWorld;
            Bukkit.getScheduler().runTaskLater(plugin, this::start, 20 * 3L);
        });


    }

    public void start() {
        isActive = true;
        Map<Integer, Spawn> spawns = mapTemplate.getSpawns();

        if (duelType.equals(DuelType.SPLIT)) {
            teamA = new ArrayList<>(session.getTeamA());
            teamB = new ArrayList<>(session.getTeamB());
            plugin.getTeamHandler().applyDuelTeamName(players, teamA, teamB);

            for (int i = 0; i < teamA.size(); i++) {
                playersSpawns.put(teamA.get(i), spawns.get(1));
            }

            for (int i = 0; i < teamB.size(); i++) {
                playersSpawns.put(teamB.get(i), spawns.get(2));
            }

        } else {
            for (int i = 0; i < players.size(); i++) {
                playersSpawns.put(players.get(i), spawns.get((i % spawns.size()) + 1));
            }
        }

        for (UUID uuid : players) {
            Player player = Bukkit.getPlayer(uuid);
            if (player == null) continue;

            sendDuelInfo(player);
            prepareInventoryDuel(player);
            teleportPlayerDuel(player);

            player.setHealth(20);
            player.setFoodLevel(20);
            player.setSaturation(20);
            player.setGameMode(GameMode.SURVIVAL);
            player.setAllowFlight(false);
            player.setCollidable(true);
            player.setInvulnerable(false);
            player.setCanPickupItems(true);
            player.setSilent(false);

        }

    }

    public void manageWinners(List<UUID> winners) {
        Component winner;
        if (duelType.equals(DuelType.SPLIT)) {
            winner = plugin.getConfigHandler().getMessageFromConfig("results.winning-team").append(Component.text(getPlayerTeam(alivePlayers.getFirst()).stream()
                    .map(uuid -> {
                        Player p = Bukkit.getPlayer(uuid);
                        return p != null ? p.getName() : "Unknown";
                    })
                    .collect(Collectors.joining(", "))));
        } else {
            Player winnerPlayer = Bukkit.getPlayer(alivePlayers.getFirst());
            winner = plugin.getConfigHandler().getMessageFromConfig("results.winner").append(Component.text(winnerPlayer != null ? winnerPlayer.getName() : "Unknown"));
        }

        for (UUID uuid : players) {
            Player player = Bukkit.getPlayer(uuid);
            if (player == null) continue;

            if (winners.contains(uuid)) {
                plugin.getStatsHandler().addWin(player);
            } else if (deads.contains(uuid) && !winners.contains(uuid)) {
                plugin.getStatsHandler().addLoss(player);
            }

            player.sendMessage(" ");
            player.sendMessage(plugin.getConfigHandler().getMessageFromConfig("results.end"));
            player.sendMessage(winner);
            player.sendMessage(" ");

        }
    }

    public void stop() {
        Bukkit.getScheduler().runTaskLater(plugin, () -> {

            for (UUID uuid : new ArrayList<>(players)) {
                Player player = Bukkit.getPlayer(uuid);
                if (player == null) continue;

                if (!alivePlayers.contains(uuid) && !spectators.contains(uuid)) continue;

                if (spectators.contains(uuid)) {
                    stopSpectating(player);
                } else {
                    plugin.getLobbyHandler().sendToLobby(player);
                }
            }

            plugin.getWorldsHandler().deleteWorld(world);
            plugin.getDuelHandler().deleteDuel(this);
        }, 20 * 5L);
    }

    private void removeAlivePlayer(Player player) {
        alivePlayers.remove(player.getUniqueId());
        deads.add(player.getUniqueId());
        checkForDuelEnd();
    }

    public void kill(Player killed, Player killer) {
        if (killed.equals(killer)) {
            death(killed);
            return;
        }

        for (UUID uuid : players) {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null) {
                player.sendMessage(plugin.getConfigHandler().getMessageFromConfig(
                        "player-killed-player",
                        Placeholder.component("killer", getPlayerDisplayName(killer)),
                        Placeholder.component("killed", getPlayerDisplayName(killed))
                ));
            }
        }

        startSpectating(killed, killer);
        removeAlivePlayer(killed);
    }

    public void death(Player dead) {
        for (UUID uuid : players) {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null) {
                player.sendMessage(plugin.getConfigHandler().getMessageFromConfig("player-died", Placeholder.component("player", getPlayerDisplayName(dead))));
            }
        }

        Player firstAlive = Bukkit.getPlayer(alivePlayers.getFirst());
        if (firstAlive != null) {
            startSpectating(dead, firstAlive);
        }

        removeAlivePlayer(dead);
    }

    public void leave(Player leaver) {
        for (UUID uuid : players) {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null) {
                player.sendMessage(plugin.getConfigHandler().getMessageFromConfig("player-left-duel", Placeholder.component("player", getPlayerDisplayName(leaver))));
            }
        }

        if (leaver.isOnline()) {
            Player firstAlive = Bukkit.getPlayer(alivePlayers.getFirst());
            if (firstAlive != null) {
                startSpectating(leaver, firstAlive);
            }
        } else {
            players.remove(leaver.getUniqueId());
        }

        removeAlivePlayer(leaver);

    }

    public void checkForDuelEnd() {
        if (duelType.equals(DuelType.SPLIT)) {
            if (teamA.stream().filter(alivePlayers::contains).toList().isEmpty() || teamB.stream().filter(alivePlayers::contains).toList().isEmpty()) {
                manageWinners(getPlayerTeam(alivePlayers.getFirst()));
                stop();
                isActive = false;
            }

        } else {
            if (alivePlayers.size() == 1) {
                manageWinners(List.of(alivePlayers.getFirst()));
                stop();
                isActive = false;
            }
        }


    }

    public void prepareInventoryDuel(Player player) {
        player.getInventory().clear();

        player.getInventory().setContents(kit.getContents());
        player.getInventory().setArmorContents(kit.getArmor());
        player.getInventory().setItemInOffHand(kit.getOffHand());

    }

    public void teleportPlayerDuel(Player player) {
        Spawn spawn = playersSpawns.get(player.getUniqueId());
        if (spawn == null) return;

        Location playerSpawn = new Location(
                world,
                spawn.getX(),
                spawn.getY(),
                spawn.getZ(),
                spawn.getYaw(),
                0);

        player.teleport(playerSpawn);

    }


    public void startSpectating(Player spectator, Player toSpectate) {
        if (!players.contains(spectator.getUniqueId())) {
            players.add(spectator.getUniqueId());
        }
        spectators.add(spectator.getUniqueId());

        spectator.teleportAsync(toSpectate.getLocation()).thenRun(() -> {
            List<Player> playerObjects = players.stream()
                    .map(Bukkit::getPlayer)
                    .filter(Objects::nonNull)
                    .toList();
            plugin.getSpectatorHandler().applySpectatorEffect(spectator, playerObjects);
        });

        spectator.sendMessage(plugin.getConfigHandler().getMessageFromConfig("start-spectating", Placeholder.component("player", getPlayerDisplayName(toSpectate))));
    }

    public void stopSpectating(Player spectator) {
        if (!deads.contains(spectator.getUniqueId())) {
            players.remove(spectator.getUniqueId());
        }

        spectators.remove(spectator.getUniqueId());

        List<Player> playerObjects = players.stream()
                .map(Bukkit::getPlayer)
                .filter(Objects::nonNull)
                .toList();
        plugin.getSpectatorHandler().removeSpectatorEffect(spectator, playerObjects);

        plugin.getLobbyHandler().sendToLobby(spectator);

    }

    public void sendDuelInfo(Player player) {
        String duelTypeName = (duelType.equals(DuelType.FFA)) ? "Ffa" : "Split";
        String mapName = session.getSelectedMapTemplate().get().getTemplateDisplayName();
        String kitName = "[" + session.getSelectedKit().get().getSlot() + "]";

        player.sendMessage(" ");
        player.sendMessage(plugin.getConfigHandler().getMessageFromConfig("duel.start"));
        player.sendMessage(plugin.getConfigHandler().getMessageFromConfig("duel.type", Placeholder.component("type", Component.text(duelTypeName))));

        player.sendMessage(plugin.getConfigHandler().getMessageFromConfig("duel.map", Placeholder.component("map", Component.text(mapName))));

        player.sendMessage(plugin.getConfigHandler().getMessageFromConfig("duel.kit", Placeholder.component("kit", Component.text(kitName))));

        player.sendMessage(" ");

    }

    public Component getPlayerDisplayName(Player player) {
        if (duelType.equals(DuelType.FFA)) {
            return Component.text(player.getName());
        } else {
            return Component.text(player.getName(), (getPlayerTeam(player.getUniqueId()).equals(teamA) ? NamedTextColor.RED : NamedTextColor.BLUE));

        }
    }


}
