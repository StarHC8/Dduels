package org.starhc.dduels.models;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.starhc.dduels.Dduels;
import org.starhc.dduels.enums.DuelType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Duel {
    private Dduels plugin;
    private DuelSession session;
    private List<Player> players;
    private MapTemplate mapTemplate;
    private Kit kit;
    private World world;

    private List<Player> alivePlayers;
    private List<Player> spectators = new ArrayList<>();
    private List<Player> deads = new ArrayList<>();
    private Map<Player, Spawn> playersSpawns = new HashMap<>();
    private DuelType duelType;
    private boolean isActive = false;

    private List<Player> teamA;
    private List<Player> teamB;

    public Duel(Dduels plugin, DuelSession session) {
        this.plugin = plugin;
        this.session = session;

        this.players = new ArrayList<>(session.getAllPlayers());
        this.mapTemplate = session.getSelectedMapTemplate().get();
        this.kit = session.getSelectedKit().get();
        this.duelType = session.getDuelType();

        this.alivePlayers = new ArrayList<>(List.copyOf(players));
    }

    public List<Player> getPlayers() {
        return players;
    }

    public List<Player> getAlivePlayers() {
        return alivePlayers;
    }

    public List<Player> getDeads() {
        return deads;
    }

    public List<Player> getSpectators() {
        return spectators;
    }

    public List<Player> getTeamA() {
        return teamA;
    }

    public List<Player> getTeamB() {
        return teamB;
    }

    public List<Player> getPlayerTeam(Player player) {
        if (teamA.contains(player)) {
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
        for (Player player : players) {
            player.sendMessage(plugin.getConfigHandler().getMessageFromConfig("loading-duel"));
        }

        plugin.getWorldsHandler().createWorldFromTemplate(mapTemplate.getTemplateName()).thenAccept(createdWorld -> {
            if (createdWorld == null) {
                for (Player player : players) {
                    player.sendMessage(plugin.getConfigHandler().getMessageFromConfig("system-errors.world-error"));
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

        for (Player player : players) {

            sendDuelInfo(player);
            prepareInventoryDuel(player);
            teleportPlayerDuel(player);
            player.setGameMode(GameMode.SURVIVAL);
            player.setAllowFlight(false);
            player.setHealth(20);

        }

    }

    public void manageWinners(List<Player> winners) {
        Component winner;
        if (duelType.equals(DuelType.SPLIT)) {
            winner = plugin.getConfigHandler().getMessageFromConfig("results.winning-team").append(Component.text(getPlayerTeam(alivePlayers.getFirst()).stream()
                    .map(Player::getName)
                    .collect(Collectors.joining(", "))));
        } else {
            winner = plugin.getConfigHandler().getMessageFromConfig("results.winner").append(Component.text(alivePlayers.getFirst().getName()));
        }

        for (Player player : players) {
            if (winners.contains(player)) {
                plugin.getStatsHandler().addWin(player);
            } else if (deads.contains(player) && !winners.contains(player)) {
                plugin.getStatsHandler().addLoss(player);
            }

            player.sendMessage(" ");
            player.sendMessage(plugin.getConfigHandler().getMessageFromConfig("results.end"));
            player.sendMessage(winner);
            player.sendMessage(" ");
            player.sendMessage(plugin.getConfigHandler().getMessageFromConfig("going-lobby"));

        }
    }

    public void stop() {
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            Location playerSpawn = Bukkit.getWorlds().getFirst().getSpawnLocation();

            for (Player player : players) {
                prepareInventoryLobby(player);
                player.teleportAsync(playerSpawn).thenRun(() -> {
                    if (spectators.contains(player)) {
                        plugin.getSpectatorHandler().removeSpectatorEffect(player);
                    }

                    player.setGameMode(GameMode.ADVENTURE);
                    player.setAllowFlight(true);
                    player.setHealth(20);
                    plugin.getTeamHandler().resetPlayersNames(player);
                });
            }

            plugin.getWorldsHandler().deleteWorld(world);
            plugin.getDuelHandler().deleteDuel(this);
        }, 20 * 5L);
    }

    public void kill(Player killed, Player killer) {
        if (killed.equals(killer)) {
            death(killed);
            return;
        }

        alivePlayers.remove(killed);

        for (Player player : players) {
            player.sendMessage(plugin.getConfigHandler().getMessageFromConfig(
                    "player-killed-player",
                    Placeholder.component("killer", getPlayerDisplayName(killer)),
                    Placeholder.component("killed", getPlayerDisplayName(killed))
            ));
        }

        deads.add(killed);
        startSpectating(killed, killer);

        checkForDuelEnd();

    }

    public void death(Player dead) {
        alivePlayers.remove(dead);

        for (Player player : players) {
            player.sendMessage(plugin.getConfigHandler().getMessageFromConfig("player-died", Placeholder.component("player", getPlayerDisplayName(dead))));
        }

        deads.add(dead);

        startSpectating(dead, alivePlayers.getFirst());

        checkForDuelEnd();

    }

    public void leave(Player leaver) {
        alivePlayers.remove(leaver);

        for (Player player : players) {
            player.sendMessage(plugin.getConfigHandler().getMessageFromConfig("player-left-duel", Placeholder.component("player", getPlayerDisplayName(leaver))));
        }

        deads.add(leaver);

        if (leaver.isOnline()) {
            startSpectating(leaver, alivePlayers.getFirst());
        } else {
            alivePlayers.remove(leaver);
            players.remove(leaver);
        }

        checkForDuelEnd();

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
        Spawn spawn = playersSpawns.get(player);

        Location playerSpawn = new Location(
                world,
                spawn.getX(),
                spawn.getY(),
                spawn.getZ(),
                spawn.getYaw(),
                0);

        player.teleport(playerSpawn);

    }

    public void prepareInventoryLobby(Player player) {
        player.getInventory().clear();

    }

    public void startSpectating(Player spectator, Player toSpectate) {
        if (!players.contains(spectator)) {
            players.add(spectator);
        }
        spectators.add(spectator);

        spectator.teleportAsync(toSpectate.getLocation()).thenRun(() -> {
            plugin.getSpectatorHandler().applySpectatorEffect(spectator);
        });

        spectator.sendMessage(plugin.getConfigHandler().getMessageFromConfig("start-spectating", Placeholder.component("player", getPlayerDisplayName(toSpectate))));
    }

    public void stopSpectating(Player spectator) {
        if (!deads.contains(spectator)) {
            players.remove(spectator);
        }

        spectators.remove(spectator);

        Location playerSpawn = Bukkit.getWorlds().getFirst().getSpawnLocation();
        spectator.teleportAsync(playerSpawn).thenRun(() -> {
            plugin.getSpectatorHandler().removeSpectatorEffect(spectator);
            spectator.setAllowFlight(true);
            spectator.setFlying(true);

        });

        plugin.getTeamHandler().resetPlayersNames(spectator);
        prepareInventoryLobby(spectator);

        spectator.sendMessage(plugin.getConfigHandler().getMessageFromConfig("going-lobby"));
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
            return Component.text(player.getName(), (getPlayerTeam(player).equals(teamA) ? NamedTextColor.RED : NamedTextColor.BLUE));

        }
    }


}
