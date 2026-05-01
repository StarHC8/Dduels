package org.starhc.dduels.models;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.starhc.dduels.Dduels;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Duel {

    private Dduels plugin;
    private List<Player> players;
    private MapTemplate mapTemplate;
    private Kit kit;
    private World world;

    private List<Player> alivePlayers;
    private Map<Player, Spawn> playersSpawns = new HashMap<>();

    public Duel(Dduels plugin, List<Player> players, MapTemplate mapTemplate, Kit kit) {

        this.plugin = plugin;
        this.players = players;
        this.mapTemplate = mapTemplate;
        this.kit = kit;

        this.alivePlayers = new ArrayList<>(List.copyOf(players));

    }

    public List<Player> getPlayers() {
        return players;
    }

    public MapTemplate getMapTemplate() {
        return mapTemplate;
    }

    public Kit getKit() {
        return kit;
    }

    public void init()  {
        for (Player player : players) {
            player.sendMessage(plugin.getConfigHandler().getMessageFromConfig("loading-duel"));
        }

        this.world = plugin.getWorldsHandler().createWorldFromTemplate(mapTemplate.getTemplateName());
        Bukkit.getScheduler().runTaskLater(plugin, this::start, 20 * 3L);

    }

    public void start()  {


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

    public void stop(Player winner) {
        for (Player player : players) {
            if (player == winner) {
                player.sendMessage(plugin.getConfigHandler().getMessageFromConfig("duel-win"));
            } else {
                player.sendMessage(plugin.getConfigHandler().getMessageFromConfig("duel-lose"));
            }

            player.sendMessage(plugin.getConfigHandler().getMessageFromConfig("going-lobby"));

        }

        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            for (Player player : players) {
                prepareInventoryLobby(player);
                teleportPlayerLobby(player);
                player.setGameMode(GameMode.ADVENTURE);
                player.setAllowFlight(true);
                player.setHealth(20);
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

        killed.getInventory().clear();
        killed.setGameMode(GameMode.SPECTATOR);

        checkForDuelEnd();

    }

    public void leave(Player leaver) {

        if (alivePlayers.contains(leaver)) {
            alivePlayers.remove(leaver);
        }

        players.remove(leaver);

        for (Player player : players) {
            player.sendMessage(plugin.getConfigHandler().getMessageFromConfig("player-left-duel").replace("[player]", leaver.getName()));
        }

        checkForDuelEnd();

    }

    public void checkForDuelEnd() {
        if (alivePlayers.size() == 1) {
            stop(alivePlayers.getFirst());
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
                spawn.getZ());

        player.teleport(playerSpawn);

    }

    public void prepareInventoryLobby(Player player) {
        player.getInventory().clear();

    }

    public void teleportPlayerLobby(Player player) {
        World spawnWorld = Bukkit.getWorlds().getFirst();
        Location playerSpawn = spawnWorld.getSpawnLocation();
        player.teleport(playerSpawn);

    }


}
