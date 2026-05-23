package org.starhc.dduels.ui;

import fr.mrmicky.fastinv.InventoryScheme;
import fr.mrmicky.fastinv.PaginatedFastInv;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.starhc.dduels.Dduels;
import org.starhc.dduels.models.DuelSession;
import org.starhc.dduels.models.Kit;
import org.starhc.dduels.utils.Item;

import java.util.List;

public class KitSelectorUi extends PaginatedFastInv {

    private static final int SLOT_PREVIOUS_PAGE = 39;
    private static final int SLOT_NEXT_PAGE = 41;

    private static final InventoryScheme SCHEME = new InventoryScheme()
            .mask("         ")
            .mask(" 1111111 ")
            .mask(" 1111111 ")
            .mask(" 1111111 ")
            .bindPagination('1');

    public KitSelectorUi(Dduels plugin, DuelSession session) {
        super(45, PlainTextComponentSerializer.plainText().serialize(plugin.getConfigHandler().getMessageFromConfig("ui-names.kit-selector")));

        Player sender = Bukkit.getPlayer(session.getSender());
        if (sender == null) return;

        previousPageItem(SLOT_PREVIOUS_PAGE, p -> Item.create(Material.ARROW, 1, Component.text("Page " + p + "/" + lastPage())));
        nextPageItem(SLOT_NEXT_PAGE, p -> Item.create(Material.ARROW, 1, Component.text("Page " + p + "/" + lastPage())));

        List<Kit> kits = plugin.getKitHandler().getKits(session.getSender());

        for (Kit kit : kits) {

            boolean isSelected = false;
            if (kit.equals(session.getSelectedKit())) {
                isSelected = true;
            }

            addContent(Item.create(Material.IRON_CHESTPLATE, 1, MiniMessage.miniMessage().deserialize((isSelected ? "<aqua><underlined>" : "") + "[" + kit.getSlot() + "]"),
                    Component.text("LEFT -> Select", NamedTextColor.WHITE),
                    Component.text("SHIFT+LEFT -> Edit", NamedTextColor.WHITE),
                    Component.text("DROP -> Delete", NamedTextColor.WHITE)), event -> {

                if (event.getClick() == ClickType.LEFT) {
                    session.setSelectedKit(kit);
                    new DuelUi(plugin, session).open(sender);

                } else if (event.getClick() == ClickType.SHIFT_LEFT) {
                    new KitCreatorUi(plugin, session, kit).open(sender);

                } else if (event.getClick() == ClickType.DROP) {
                    plugin.getKitHandler().deleteKit(session.getSender(), kit.getSlot()).thenRun(() -> {
                        Bukkit.getScheduler().runTask(plugin, () -> {
                            sender.sendMessage(plugin.getConfigHandler().getMessageFromConfig("kit-deleted", Placeholder.component("kit", Component.text(String.valueOf(kit.getSlot())))));
                            new KitSelectorUi(plugin, session).open(sender);
                        });
                    });
                }
            });
        }

        addContent(Item.create(Material.MAP, 1, plugin.getConfigHandler().getMessageFromConfig("items-names.new-kit")), e -> {

            int lastKitIndex = 0;
            for (Kit kit : kits) {
                lastKitIndex = Math.max(kit.getSlot(), lastKitIndex);
            }

            Kit newKit = new Kit(session.getSender(), lastKitIndex + 1, new ItemStack[36], new ItemStack[4], null);
            new KitCreatorUi(plugin, session, newKit).open(sender);
        });

        SCHEME.apply(this);
    }
}
