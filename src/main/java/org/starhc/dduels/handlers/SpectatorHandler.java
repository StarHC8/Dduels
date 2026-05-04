package org.starhc.dduels.handlers;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.starhc.dduels.Dduels;
import org.starhc.dduels.utils.Item;


public class SpectatorHandler {
    private final Dduels plugin;

    public SpectatorHandler(Dduels plugin) {
        this.plugin = plugin;
    }

    public void applySpectatorEffect(Player player) {
        giveSpectatorItems(player);
        player.setGameMode(GameMode.ADVENTURE);

        if (player.isOnline()) {
            player.setAllowFlight(true);
            player.setFlying(true);
        }

        player.setCollidable(false);
        player.setInvulnerable(true);
        player.setCanPickupItems(false);
        player.setSilent(true);

        player.setHealth(player.getMaxHealth());
        player.setFoodLevel(20);
        player.setSaturation(20);
        player.removePotionEffect(PotionEffectType.INVISIBILITY);


        for (Player online : Bukkit.getOnlinePlayers()) {
            if (online.getUniqueId().equals(player.getUniqueId())) continue;
            online.hidePlayer(plugin, player);
        }

    }

    public void removeSpectatorEffect(Player player) {
        player.setCollidable(true);
        player.setInvulnerable(false);
        player.setCanPickupItems(true);
        player.setSilent(false);

        player.setHealth(player.getMaxHealth());
        player.setFoodLevel(20);
        player.setSaturation(20);

        player.removePotionEffect(PotionEffectType.INVISIBILITY);

        for (Player online : Bukkit.getOnlinePlayers()) {
            if (online.getUniqueId().equals(player.getUniqueId())) continue;
            online.showPlayer(plugin, player);
        }
    }

    public void giveSpectatorItems(Player player) {
        player.getInventory().clear();

        player.getInventory().setItem(0, Item.create(Material.DIAMOND_BOOTS, 1, plugin.getConfigHandler().getMessageFromConfig("items-names.fly-speed-item")));
        player.getInventory().setItem(4, Item.create(Material.COMPASS, 1, plugin.getConfigHandler().getMessageFromConfig("items-names.spect-navigator-item")));
        player.getInventory().setItem(8, Item.create(Material.RED_DYE, 1, plugin.getConfigHandler().getMessageFromConfig("items-names.stop-spectating-item")));

    }

}
