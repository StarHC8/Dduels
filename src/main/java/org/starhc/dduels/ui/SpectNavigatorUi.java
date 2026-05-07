package org.starhc.dduels.ui;

import fr.mrmicky.fastinv.InventoryScheme;
import fr.mrmicky.fastinv.PaginatedFastInv;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.starhc.dduels.Dduels;
import org.starhc.dduels.models.Duel;
import org.starhc.dduels.models.DuelSession;
import org.starhc.dduels.models.Kit;
import org.starhc.dduels.utils.Item;

import java.util.List;

public class SpectNavigatorUi extends PaginatedFastInv {

    private static final InventoryScheme SCHEME = new InventoryScheme()
            .mask("         ")
            .mask(" 1111111 ")
            .mask(" 1111111 ")
            .mask(" 1111111 ")
            .bindPagination('1');

    public SpectNavigatorUi(Dduels plugin, Duel duel) {
        super(45, plugin.getConfigHandler().getMessageFromConfig("ui-names.spect-navigator"));

        previousPageItem(39, p -> Item.create(Material.ARROW, 1, "Page " + p + "/" + lastPage()));
        nextPageItem(41, p -> Item.create(Material.ARROW, 1, "Page " + p + "/" + lastPage()));

        for (Player player : duel.getAlivePlayers()) {
            addContent(Item.createPlayerHead(player.getName(), 1, "§f" + player.getName()), event -> {
                Player clicker = (Player) event.getWhoClicked();
                clicker.teleport(player.getLocation());
            });

        }

    }
}
