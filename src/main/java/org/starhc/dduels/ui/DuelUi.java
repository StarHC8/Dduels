package org.starhc.dduels.ui;

import fr.mrmicky.fastinv.FastInv;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.starhc.dduels.Dduels;
import org.starhc.dduels.models.DuelSession;
import org.starhc.dduels.models.MapTemplate;
import org.starhc.dduels.utils.Item;

import java.util.ArrayList;
import java.util.List;

public class DuelUi extends FastInv {

    private final Dduels plugin;
    private final DuelSession session;

    public DuelUi(Dduels plugin, DuelSession session) {
        super(27, plugin.getConfigHandler().getMessageFromConfig("ui-names.duel-sender"));
        this.plugin = plugin;
        this.session = session;

        if (session.getSelectedKit().isEmpty()) {
            plugin.getKitHandler().getKits(session.getSender().getUniqueId()).stream()
                    .findFirst()
                    .ifPresent(session::setSelectedKit);
        }

        setupItems();
    }

    private void setupItems() {
        setItems(List.of(0, 1, 2, 9, 10, 11, 18, 19, 20),
                Item.create(Material.LIME_STAINED_GLASS_PANE, 1, plugin.getConfigHandler().getMessageFromConfig("items-names.send-item")),
                event -> {
                    if (session.getSelectedKit().isEmpty()) {
                        session.getSender().sendMessage(plugin.getConfigHandler().getMessageFromConfig("system-errors.no-kit-available"));
                        return;
                    }
                    sendDuelRequest();
                });

        setItems(List.of(6, 7, 8, 15, 16, 17, 24, 25, 26),
                Item.create(Material.RED_STAINED_GLASS_PANE, 1, plugin.getConfigHandler().getMessageFromConfig("items-names.cancel-item")),
                event -> {
                    session.getSender().sendMessage(plugin.getConfigHandler().getMessageFromConfig("duel-cancelled"));
                    session.getSender().closeInventory();
                });

        setItem(22, Item.createPlayerHead(session.getEnemies().getFirst().getName(), 1,
                plugin.getConfigHandler().getMessageFromConfig("items-names.enemy-item")
                        .replace("[player]", session.getEnemies().getFirst().getName())));

        MapTemplate currentMap = session.getSelectedMapTemplate().get();
        setItem(4, Item.create(Material.GRASS_BLOCK, 1,
                "§r" + plugin.getConfigHandler().getMessageFromConfig("items-names.map-selector")
                        .replace("[map]", currentMap.getTemplateDisplayName())),
                event -> new MapSelectorUi(plugin, session).open(session.getSender()));

        String kitDisplay = session.getSelectedKit().map(k -> "[" + k.getSlot() + "]").orElse("[]");
        setItem(13, Item.create(Material.IRON_CHESTPLATE, 1,
                "§r" + plugin.getConfigHandler().getMessageFromConfig("items-names.kit-selector")
                        .replace("[kit]", kitDisplay)),
                event -> new KitSelectorUi(plugin, session).open(session.getSender()));
    }

    private void sendDuelRequest() {
        List<Player> allPlayers = new ArrayList<>(session.getEnemies());
        allPlayers.add(session.getSender());

        plugin.getRequestHandler().sendRequest(session.getSender(), session.getEnemies().getFirst(), session);
        session.getSender().closeInventory();
    }

    @Override
    public void onClick(InventoryClickEvent event) {
        event.setCancelled(true);
    }
}
