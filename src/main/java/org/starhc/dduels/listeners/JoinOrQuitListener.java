package org.starhc.dduels.listeners;

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
    public void onQuit(PlayerQuitEvent event) {
        event.quitMessage(null);
        Player player = event.getPlayer();
        Duel duel = plugin.getDuelHandler().getDuel(player);

        if (duel != null) {
            duel.leave(player);
        }

    }

}
