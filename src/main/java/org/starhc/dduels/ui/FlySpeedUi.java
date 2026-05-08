package org.starhc.dduels.ui;

import fr.mrmicky.fastinv.FastInv;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.starhc.dduels.Dduels;
import org.starhc.dduels.models.DuelSession;
import org.starhc.dduels.utils.Item;

import java.awt.*;

public class FlySpeedUi extends FastInv {

    private final int SLOT_I = 10;
    private final int SLOT_II = 12;
    private final int SLOT_III = 14;
    private final int SLOT_VI = 16;

    private final Dduels plugin;

    public FlySpeedUi(Dduels plugin) {
        super(27, PlainTextComponentSerializer.plainText().serialize(plugin.getConfigHandler().getMessageFromConfig("ui-names.fly-speed")));
        this.plugin = plugin;

        setItem(SLOT_I, Item.create(Material.LEATHER_BOOTS, 1, Component.text("I", NamedTextColor.GREEN)), event -> {
            Player player = (Player) event.getWhoClicked();
            player.setFlySpeed(0.05f);
            player.closeInventory();
        });

        setItem(SLOT_II, Item.create(Material.IRON_BOOTS, 1, Component.text("II", NamedTextColor.YELLOW)), event -> {
            Player player = (Player) event.getWhoClicked();
            player.setFlySpeed(0.1f);
            player.closeInventory();
        });

        setItem(SLOT_III, Item.create(Material.GOLDEN_BOOTS, 1, Component.text("III", NamedTextColor.RED)), event -> {
            Player player = (Player) event.getWhoClicked();
            player.setFlySpeed(0.2f);
            player.closeInventory();
        });

        setItem(SLOT_VI, Item.create(Material.DIAMOND_BOOTS, 1, Component.text("IV", NamedTextColor.DARK_RED)), event -> {
            Player player = (Player) event.getWhoClicked();
            player.setFlySpeed(0.4f);
            player.closeInventory();
        });


    }
}
