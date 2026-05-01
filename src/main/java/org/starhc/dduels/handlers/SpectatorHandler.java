package org.starhc.dduels.handlers;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.starhc.dduels.Dduels;


public class SpectatorHandler {
    private final Dduels plugin;
    public SpectatorHandler(Dduels plugin) {
        this.plugin = plugin;
    }

    public void applySpectatorEffect(Player player) {

        player.setGameMode(GameMode.ADVENTURE);

        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            if (player.isOnline()) {
                player.setAllowFlight(true);
                player.setFlying(true);
            }
        }, 1L);

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

}
