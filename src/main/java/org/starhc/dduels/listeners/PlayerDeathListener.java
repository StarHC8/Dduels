package org.starhc.dduels.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.starhc.dduels.Dduels;
import org.starhc.dduels.models.Duel;

public class PlayerDeathListener implements Listener {
    private final Dduels plugin;

    public PlayerDeathListener(Dduels plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getPlayer();

        Duel duel = plugin.getDuelHandler().getDuel(player);
        if (duel == null) {
            return;
        }

        event.setCancelled(true);

        if (player.getKiller() instanceof Player killer) {
            duel.kill(player, killer);
        } else {
            duel.death(player);
        }
    }
}
