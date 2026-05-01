package org.starhc.dduels.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.starhc.dduels.Dduels;
import org.starhc.dduels.models.Duel;

public class SpectatorListener implements Listener {
    private Dduels plugin;
    public SpectatorListener(Dduels plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onHit(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player player && event.getDamager() instanceof Player damager) {
            Duel duel = plugin.getDuelHandler().getSpectatingDuel(damager);
            if (duel != null) {
                event.setCancelled(true);
            }

        }
    }




}
