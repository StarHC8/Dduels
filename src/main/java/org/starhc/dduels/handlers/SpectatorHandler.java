package org.starhc.dduels.handlers;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.starhc.dduels.Dduels;
import org.starhc.dduels.utils.Item;

import java.util.List;


public class SpectatorHandler {
    private final Dduels plugin;

    public SpectatorHandler(Dduels plugin) {
        this.plugin = plugin;
    }

    public void applySpectatorEffect(Player player, List<Player> duelPlayers) {
        giveSpectatorItems(player);
        player.setGameMode(GameMode.ADVENTURE);

        player.setAllowFlight(true);
        player.setFlying(true);

        player.setCollidable(false);
        player.setInvulnerable(true);
        player.setCanPickupItems(false);
        player.setSilent(true);

        player.setHealth(20);
        player.setFoodLevel(20);
        player.setSaturation(20);

        for (PotionEffect effect : player.getActivePotionEffects()) {
            player.removePotionEffect(effect.getType());
        }

        for (Player duelPlayer : duelPlayers) {
            if (duelPlayer.getUniqueId().equals(player.getUniqueId())) continue;
            duelPlayer.hidePlayer(plugin, player);
        }

    }

    public void removeSpectatorEffect(Player player, List<Player> duelPlayers) {
        for (Player duelPlayer : duelPlayers) {
            if (duelPlayer.getUniqueId().equals(player.getUniqueId())) continue;
            duelPlayer.showPlayer(plugin, player);
        }
    }

    public void giveSpectatorItems(Player player) {
        player.getInventory().clear();

        player.getInventory().setItem(0, Item.create(Material.DIAMOND_BOOTS, 1, plugin.getConfigHandler().getMessageFromConfig("items-names.fly-speed-item")));
        player.getInventory().setItem(4, Item.create(Material.COMPASS, 1, plugin.getConfigHandler().getMessageFromConfig("items-names.spect-navigator-item")));
        player.getInventory().setItem(8, Item.create(Material.RED_DYE, 1, plugin.getConfigHandler().getMessageFromConfig("items-names.stop-spectating-item")));

    }

}
