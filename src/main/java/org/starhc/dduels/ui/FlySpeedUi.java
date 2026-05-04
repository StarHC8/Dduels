package org.starhc.dduels.ui;

import fr.mrmicky.fastinv.FastInv;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.starhc.dduels.Dduels;
import org.starhc.dduels.models.DuelSession;
import org.starhc.dduels.utils.Item;

public class FlySpeedUi extends FastInv {

    private final Dduels plugin;

    public FlySpeedUi(Dduels plugin) {
        super(27, plugin.getConfigHandler().getMessageFromConfig("ui-names.fly-speed"));
        this.plugin = plugin;

        setItem(10, Item.create(Material.LEATHER_BOOTS, 1, "§aI"), event -> {
            Player player = (Player) event.getWhoClicked();
            player.setFlySpeed(0.05f);
            player.closeInventory();
        });

        setItem(12, Item.create(Material.IRON_BOOTS, 1, "§eII"), event -> {
            Player player = (Player) event.getWhoClicked();
            player.setFlySpeed(0.1f);
            player.closeInventory();
        });

        setItem(14, Item.create(Material.GOLDEN_BOOTS, 1, "§cIII"), event -> {
            Player player = (Player) event.getWhoClicked();
            player.setFlySpeed(0.2f);
            player.closeInventory();
        });

        setItem(16, Item.create(Material.DIAMOND_BOOTS, 1, "§4IV"), event -> {
            Player player = (Player) event.getWhoClicked();
            player.setFlySpeed(0.4f);
            player.closeInventory();
        });


    }
}
