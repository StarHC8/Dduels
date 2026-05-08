package org.starhc.dduels.ui;

import fr.mrmicky.fastinv.InventoryScheme;
import fr.mrmicky.fastinv.PaginatedFastInv;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Material;
import org.starhc.dduels.Dduels;
import org.starhc.dduels.models.DuelSession;
import org.starhc.dduels.models.MapTemplate;
import org.starhc.dduels.utils.Item;

import java.util.List;

public class MapSelectorUi extends PaginatedFastInv {

    private static final int SLOT_PREVIOUS_PAGE = 39;
    private static final int SLOT_NEXT_PAGE = 41;


    private static final InventoryScheme SCHEME = new InventoryScheme()
            .mask("         ")
            .mask(" 1111111 ")
            .mask(" 1111111 ")
            .mask(" 1111111 ")
            .bindPagination('1');

    public MapSelectorUi(Dduels plugin, DuelSession session) {
        super(45, PlainTextComponentSerializer.plainText().serialize(plugin.getConfigHandler().getMessageFromConfig("ui-names.map-selector")));

        previousPageItem(SLOT_PREVIOUS_PAGE, p -> Item.create(Material.ARROW, 1, Component.text("Page " + p + "/" + lastPage())));
        nextPageItem(SLOT_NEXT_PAGE, p -> Item.create(Material.ARROW, 1, Component.text("Page " + p + "/" + lastPage())));

        List<MapTemplate> mapTemplates = plugin.getMapTemplateHandler().getMapTemplates();

        for (MapTemplate mapTemplate : mapTemplates) {
            boolean isSelected = mapTemplate.equals(session.getSelectedMapTemplate());

            addContent(Item.create(Material.FILLED_MAP, 1, MiniMessage.miniMessage().deserialize(isSelected ? "<aqua><underlined>" : "" + mapTemplate.getTemplateDisplayName())), e -> {
                session.setSelectedMapTemplate(mapTemplate);
                new DuelUi(plugin, session).open(session.getSender());
            });
        }

        SCHEME.apply(this);
    }
}
