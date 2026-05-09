package org.starhc.dduels.listeners;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.starhc.dduels.Dduels;
import org.starhc.dduels.models.Duel;

public class JoinOrQuitListener implements Listener {
    private Dduels plugin;

    public JoinOrQuitListener(Dduels plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        event.joinMessage(null);
        Player player = event.getPlayer();

        World spawnWorld = Bukkit.getWorlds().getFirst();
        Location playerSpawn = spawnWorld.getSpawnLocation();
        player.teleport(playerSpawn);

        plugin.getSpectatorHandler().removeSpectatorEffect(player);

        player.getInventory().clear();
        player.setGameMode(GameMode.ADVENTURE);
        player.setAllowFlight(true);
        player.setHealth(20);

        if (!plugin.getStatsHandler().isPlayerInDatabase(player.getUniqueId())) {
            plugin.getStatsHandler().addPlayer(player);
        }

        plugin.getRequestHandler().addPlayerToRequestsList(player);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        event.quitMessage(null);
        Player player = event.getPlayer();
        Duel duel = plugin.getDuelHandler().getDuel(player);

        if (duel != null) {
            duel.leave(player);
        }

        plugin.getRequestHandler().removePlayerFromRequestsList(player);

    }

}
