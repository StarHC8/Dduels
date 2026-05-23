package org.starhc.dduels.listeners;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.starhc.dduels.Dduels;
import org.starhc.dduels.models.Duel;
import org.starhc.dduels.ui.FlySpeedUi;
import org.starhc.dduels.ui.SpectNavigatorUi;

public class SpectatorListener implements Listener {
    private Dduels plugin;

    public SpectatorListener(Dduels plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Action action = event.getAction();

        if (action.equals(Action.RIGHT_CLICK_AIR) || action.equals(Action.RIGHT_CLICK_BLOCK)) {
            Duel duel = plugin.getDuelHandler().getSpectatingDuel(player);
            if (duel == null) {
                return;
            }

            event.setCancelled(true);
            ItemStack itemInHand = player.getInventory().getItemInMainHand();

            if (itemInHand.getType().equals(Material.DIAMOND_BOOTS)) {
                new FlySpeedUi(plugin).open(player);
                return;
            }

            if (itemInHand.getType().equals(Material.COMPASS)) {
                new SpectNavigatorUi(plugin, duel).open(player);
                return;
            }

            if (itemInHand.getType().equals(Material.RED_DYE)) {
                player.performCommand("spect");
                return;
            }
        }

    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent event) {
        Player player = event.getPlayer();

        Duel duel = plugin.getDuelHandler().getSpectatingDuel(player);
        if (duel != null) {
            event.setCancelled(true);
        }
    }


}
