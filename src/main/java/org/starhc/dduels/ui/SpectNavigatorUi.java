package org.starhc.dduels.ui;

import fr.mrmicky.fastinv.InventoryScheme;
import fr.mrmicky.fastinv.PaginatedFastInv;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.starhc.dduels.Dduels;
import org.starhc.dduels.models.Duel;
import org.starhc.dduels.utils.Item;

import java.util.UUID;

public class SpectNavigatorUi extends PaginatedFastInv {

    private static final int SLOT_PREVIOUS_PAGE = 39;
    private static final int SLOT_NEXT_PAGE = 41;

    private static final InventoryScheme SCHEME = new InventoryScheme()
            .mask("         ")
            .mask(" 1111111 ")
            .mask(" 1111111 ")
            .mask(" 1111111 ")
            .bindPagination('1');

    public SpectNavigatorUi(Dduels plugin, Duel duel) {
        super(45, PlainTextComponentSerializer.plainText().serialize(plugin.getConfigHandler().getMessageFromConfig("ui-names.spect-navigator")));

        previousPageItem(SLOT_PREVIOUS_PAGE, p -> Item.create(Material.ARROW, 1, Component.text("Page " + p + "/" + lastPage())));
        nextPageItem(SLOT_NEXT_PAGE, p -> Item.create(Material.ARROW, 1, Component.text("Page " + p + "/" + lastPage())));

        for (UUID playerUuid : duel.getAlivePlayers()) {
            Player player = Bukkit.getPlayer(playerUuid);
            addContent(Item.createPlayerHead(player.getName(), 1, Component.text(player.getName())), event -> {
                Player clicker = (Player) event.getWhoClicked();
                clicker.teleport(player.getLocation());
            });

        }

    }
}
