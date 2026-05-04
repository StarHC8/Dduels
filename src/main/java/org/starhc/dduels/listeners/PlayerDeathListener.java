package org.starhc.dduels.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.starhc.dduels.Dduels;
import org.starhc.dduels.models.Duel;

public class PlayerDeathEvent implements Listener {
    private final Dduels plugin;
    public PlayerDeathEvent(Dduels plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerDeath(EntityDeathEvent event) {
        if (event.getEntity() instanceof Player player) {
            Player killer = player.getKiller();
            Duel duel = plugin.getDuelHandler().getDuel(player);
            if (duel != null) {
                event.setCancelled(true);
                duel.kill(player, killer);
            }
        }
    }
}
