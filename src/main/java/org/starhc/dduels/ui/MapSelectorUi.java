package org.starhc.dduels.ui;

import fr.mrmicky.fastinv.InventoryScheme;
import fr.mrmicky.fastinv.PaginatedFastInv;
import org.bukkit.Material;
import org.starhc.dduels.Dduels;
import org.starhc.dduels.models.DuelSession;
import org.starhc.dduels.models.MapTemplate;
import org.starhc.dduels.utils.Item;

import java.util.List;

public class MapSelectorUi extends PaginatedFastInv {

    private static final InventoryScheme SCHEME = new InventoryScheme()
            .mask("         ")
            .mask(" 1111111 ")
            .mask(" 1111111 ")
            .mask(" 1111111 ")
            .bindPagination('1');

    public MapSelectorUi(Dduels plugin, DuelSession session) {
        super(45, plugin.getConfigHandler().getMessageFromConfig("ui-names.map-selector"));

        previousPageItem(39, p -> Item.create(Material.ARROW, 1, "Page " + p + "/" + lastPage()));
        nextPageItem(41, p -> Item.create(Material.ARROW, 1, "Page " + p + "/" + lastPage()));

        List<MapTemplate> mapTemplates = plugin.getMapTemplateHandler().getMapTemplates();

        for (MapTemplate mapTemplate : mapTemplates) {
            boolean isSelected = mapTemplate.equals(session.getSelectedMapTemplate());

            String prefix = isSelected ? "§b§n" : "§r";

            addContent(Item.create(Material.FILLED_MAP, 1, prefix + mapTemplate.getTemplateDisplayName()), e -> {
                session.setSelectedMapTemplate(mapTemplate);
                new DuelUi(plugin, session).open(session.getSender());
            });
        }

        SCHEME.apply(this);
    }
}
