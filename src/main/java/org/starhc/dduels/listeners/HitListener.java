package org.starhc.dduels.listeners;

import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.starhc.dduels.Dduels;
import org.starhc.dduels.enums.DuelType;
import org.starhc.dduels.models.Duel;

public class HitListener implements Listener {
    private Dduels plugin;

    public HitListener(Dduels plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onHit(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player player) {
            if (event.getDamager() instanceof Player damager) {
                Duel spectatingDuel = plugin.getDuelHandler().getSpectatingDuel(damager);
                if (spectatingDuel != null) {
                    event.setCancelled(true);
                    return;
                }

                Duel playingDuel = plugin.getDuelHandler().getDuel(damager);
                if (playingDuel.getDuelType().equals(DuelType.SPLIT)) {
                    if (plugin.getTeamHandler().areInSameTeam(player, damager)) {
                        event.setCancelled(true);
                        return;
                    }
                }

            } else if (event.getDamager() instanceof Projectile projectile && projectile.getShooter() instanceof Player damager) {
                Duel playingDuel = plugin.getDuelHandler().getDuel(damager);
                if (playingDuel.getDuelType().equals(DuelType.SPLIT)) {
                    if (plugin.getTeamHandler().areInSameTeam(player, damager)) {
                        event.setCancelled(true);
                        return;
                    }
                }
            }
        }
    }
}
