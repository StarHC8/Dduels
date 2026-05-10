package org.starhc.dduels.handlers;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.starhc.dduels.Dduels;

public class LobbyHandler {

    private final Dduels plugin;

    public LobbyHandler(Dduels plugin) {
        this.plugin = plugin;
    }

    public void sendToLobby(Player player) {
        player.sendMessage(plugin.getConfigHandler().getMessageFromConfig("going-lobby"));
        player.teleportAsync(Bukkit.getWorlds().getFirst().getSpawnLocation()).thenRun(() -> {

            player.setGameMode(GameMode.ADVENTURE);
            player.setAllowFlight(true);
            player.setCollidable(true);
            player.setInvulnerable(false);
            player.setCanPickupItems(true);
            player.setSilent(false);

            player.setHealth(20);
            player.setFoodLevel(20);
            player.setSaturation(20);
            player.setFlySpeed(0.1f);

            for (PotionEffect effect : player.getActivePotionEffects()) {
                player.removePotionEffect(effect.getType());
            }

            player.addPotionEffect(new PotionEffect(PotionEffectType.SATURATION, 200000, 1));
            player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 200000, 1));

            plugin.getTeamHandler().resetPlayersNames(player);

            player.getInventory().clear();
        });



    }
}
