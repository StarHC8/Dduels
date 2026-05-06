package org.starhc.dduels.models;

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

    public void init() {
        for (Player player : players) {
            player.sendMessage(plugin.getConfigHandler().getMessageFromConfig("loading-duel"));
        }

        world = plugin.getWorldsHandler().createWorldFromTemplate(mapTemplate.getTemplateName());
        Bukkit.getScheduler().runTaskLater(plugin, this::start, 20 * 3L);

    }

    public void start() {
        if (duelType.equals(DuelType.SPLIT)) {
            teamA = new ArrayList<>(session.getTeamA());
            teamB = new ArrayList<>(session.getTeamB());
            plugin.getTeamHandler().applyDuelTeamName(players, teamA, teamB);
        }

        if (playersSpawns.isEmpty()) {
            Map<Integer, Spawn> spawns = mapTemplate.getSpawns();
            for (int i = 0; i < players.size(); i++) {
                playersSpawns.put(players.get(i), spawns.get((i % spawns.size()) + 1));
            }
            if (spawns.isEmpty()) {
                for (Player player : players) {
                    player.sendMessage(plugin.getConfigHandler().getMessageFromConfig("system-errors.no-map-spawns"));
                }
                plugin.getWorldsHandler().deleteWorld(world);
                plugin.getDuelHandler().deleteDuel(this);
                return;
            }
        }

        for (Player player : players) {
            prepareInventoryDuel(player);
            teleportPlayerDuel(player);
            player.setGameMode(GameMode.SURVIVAL);
            player.setAllowFlight(false);
            player.setHealth(20);

        }

    }

    public void manageWinners(List<Player> winners) {
        for (Player player : players) {
            if (winners.contains(player)) {
                player.sendMessage(plugin.getConfigHandler().getMessageFromConfig("duel-win"));
                plugin.getStatsHandler().addWin(player);
            } else if (deads.contains(player) && !winners.contains(player)) {
                player.sendMessage(plugin.getConfigHandler().getMessageFromConfig("duel-lose"));
                plugin.getStatsHandler().addLoss(player);
            }
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
        alivePlayers.remove(killed);

        for (Player player : players) {
            player.sendMessage(plugin.getConfigHandler().getMessageFromConfig("player-killed-player")
                    .replace("[killer]", killer.getName())
                    .replace("[killed]", killed.getName()));
        }

        deads.add(killed);
        startSpectating(killed, killer);

        checkForDuelEnd();

    }

    public void death(Player dead) {
        alivePlayers.remove(dead);

        for (Player player : players) {
            player.sendMessage(plugin.getConfigHandler().getMessageFromConfig("player-died").replace("[player]", dead.getName()));
        }

        deads.add(dead);

        startSpectating(dead, alivePlayers.getFirst());

        checkForDuelEnd();

    }

    public void leave(Player leaver) {
        alivePlayers.remove(leaver);

        for (Player player : players) {
            player.sendMessage(plugin.getConfigHandler().getMessageFromConfig("player-left-duel").replace("[player]", leaver.getName()));
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
            }

        } else {
            if (alivePlayers.size() == 1) {
                manageWinners(List.of(alivePlayers.getFirst()));
                stop();
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

        spectator.sendMessage(plugin.getConfigHandler().getMessageFromConfig("start-spectating").replace("[player]", toSpectate.getName()));
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


}
