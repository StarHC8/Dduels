package org.starhc.dduels.ui;

import fr.mrmicky.fastinv.InventoryScheme;
import fr.mrmicky.fastinv.PaginatedFastInv;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.starhc.dduels.Dduels;
import org.starhc.dduels.models.DuelSession;
import org.starhc.dduels.models.Kit;
import org.starhc.dduels.utils.Item;

import java.util.List;

public class KitSelectorUi extends PaginatedFastInv {

    private static final InventoryScheme SCHEME = new InventoryScheme()
            .mask("         ")
            .mask(" 1111111 ")
            .mask(" 1111111 ")
            .mask(" 1111111 ")
            .bindPagination('1');

    public KitSelectorUi(Dduels plugin, DuelSession session) {
        super(45, plugin.getConfigHandler().getMessageFromConfig("ui-names.kit-selector"));

        previousPageItem(39, p -> Item.create(Material.ARROW, 1, "Page " + p + "/" + lastPage()));
        nextPageItem(41, p -> Item.create(Material.ARROW, 1, "Page " + p + "/" + lastPage()));

        List<Kit> kits = plugin.getKitHandler().getKits(session.getSender().getUniqueId());

        for (Kit kit : kits) {

            boolean isSelected = false;
            if (kit.equals(session.getSelectedKit())) {
                isSelected = true;
            }

            String prefix = isSelected ? "§b§n" : "§r";

            addContent(Item.create(Material.IRON_CHESTPLATE, 1, prefix + "[" + kit.getSlot() + "]",
                    "§7LEFT -> Select",
                    "§7SHIFT+LEFT -> Edit",
                    "§7DROP -> Delete"), event -> {

                if (event.getClick() == ClickType.LEFT) {
                    session.setSelectedKit(kit);
                    new DuelUi(plugin, session).open(session.getSender());

                } else if (event.getClick() == ClickType.SHIFT_LEFT) {
                    new KitCreatorUi(plugin, session, kit).open(session.getSender());

                } else if (event.getClick() == ClickType.DROP) {
                    plugin.getKitHandler().deleteKit(session.getSender().getUniqueId(), kit.getSlot()).thenRun(() -> {
                        Bukkit.getScheduler().runTask(plugin, () -> {
                            session.getSender().sendMessage(plugin.getConfigHandler().getMessageFromConfig("kit-deleted")
                                    .replace("[kit]", String.valueOf(kit.getSlot())));
                            new KitSelectorUi(plugin, session).open(session.getSender());
                        });
                    });
                }
            });
        }

        addContent(Item.create(Material.MAP, 1, plugin.getConfigHandler().getMessageFromConfig("items-names.new-kit")), e -> {

            int lastKitIndex = 1;
            for (Kit kit : kits) {
                lastKitIndex = Math.max(kit.getSlot(), lastKitIndex);
            }

            Kit newKit = new Kit(session.getSender().getUniqueId(), lastKitIndex + 1, new ItemStack[36], new ItemStack[4], null);
            new KitCreatorUi(plugin, session, newKit).open(session.getSender());
        });

        SCHEME.apply(this);
    }
}
