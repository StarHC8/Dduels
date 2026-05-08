package org.starhc.dduels.ui;

import fr.mrmicky.fastinv.FastInv;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.starhc.dduels.Dduels;
import org.starhc.dduels.enums.DuelType;
import org.starhc.dduels.models.DuelSession;
import org.starhc.dduels.models.MapTemplate;
import org.starhc.dduels.utils.Item;

import java.util.List;

public class DuelUi extends FastInv {

    private final List<Integer> SLOTS_SEND = List.of(0, 1, 2, 9, 10, 11, 18, 19, 20);
    private final List<Integer> SLOTS_CANCEL = List.of(6, 7, 8, 15, 16, 17, 24, 25, 26);
    private final int SLOT_PLAYERS = 22;
    private final int SLOT_DUEL_TYPE = 12;
    private final int SLOT_MAP = 4;
    private final int SLOT_KIT = 13;

    private final Dduels plugin;
    private final DuelSession session;

    public DuelUi(Dduels plugin, DuelSession session) {
        super(27, PlainTextComponentSerializer.plainText().serialize(plugin.getConfigHandler().getMessageFromConfig("ui-names.duel-sender")));
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
        setItems(SLOTS_SEND,
                Item.create(Material.LIME_STAINED_GLASS_PANE, 1, plugin.getConfigHandler().getMessageFromConfig("items-names.send-item")),
                event -> {
                    if (session.getSelectedKit().isEmpty()) {
                        session.getSender().sendMessage(plugin.getConfigHandler().getMessageFromConfig("system-errors.no-kit-available"));
                        return;
                    }
                    sendDuelRequest();
                });

        setItems(SLOTS_CANCEL,
                Item.create(Material.RED_STAINED_GLASS_PANE, 1, plugin.getConfigHandler().getMessageFromConfig("items-names.cancel-item")),
                event -> {
                    session.getSender().sendMessage(plugin.getConfigHandler().getMessageFromConfig("duel-cancelled"));
                    session.getSender().closeInventory();
                });


        if (session.getAllPlayers().size() == 2) {
            String enemyName = session.getAllPlayers().stream().filter(player -> !player.equals(session.getSender())).findFirst().get().getName();

            setItem(SLOT_PLAYERS, Item.createPlayerHead(enemyName, 1,
                    plugin.getConfigHandler().getMessageFromConfig("items-names.enemy-item", Placeholder.component("player", Component.text(enemyName)))));

        } else {
            boolean isFfa = session.getDuelType().equals(DuelType.FFA);

            Component duelTypeItemName = plugin.getConfigHandler().getMessageFromConfig("items-names.duel-type-item", Placeholder.component("type", Component.text((isFfa) ? "FFA" : "SPLIT")));

            setItem(SLOT_DUEL_TYPE, Item.create((isFfa) ? Material.REDSTONE_BLOCK : Material.LAPIS_BLOCK,
                    1,
                    duelTypeItemName), event -> {

                DuelType newDuelType = (isFfa) ? DuelType.SPLIT : DuelType.FFA;
                session.setDuelType(newDuelType);
                setupItems();
            });

            if (!isFfa) {
                setItem(SLOT_PLAYERS, Item.create(Material.BLUE_BANNER, 1,
                        plugin.getConfigHandler().getMessageFromConfig("items-names.team-manager")), event -> {
                    new TeamManagerUi(plugin, session).open(session.getSender());
                });
            } else {
                setItem(SLOT_PLAYERS, null);
            }

        }

        MapTemplate currentMap = session.getSelectedMapTemplate().get();
        setItem(SLOT_MAP, Item.create(Material.GRASS_BLOCK, 1, plugin.getConfigHandler().getMessageFromConfig("items-names.map-selector", Placeholder.component("map", Component.text(currentMap.getTemplateDisplayName())))),
                event -> new MapSelectorUi(plugin, session).open(session.getSender()));

        String kitDisplay = session.getSelectedKit().map(k -> "[" + k.getSlot() + "]").orElse("[]");
        setItem(SLOT_KIT, Item.create(Material.IRON_CHESTPLATE, 1,
                        plugin.getConfigHandler().getMessageFromConfig("items-names.kit-selector", Placeholder.component("kit", Component.text(kitDisplay)))),
                event -> new KitSelectorUi(plugin, session).open(session.getSender()));


    }

    private void sendDuelRequest() {
        List<Player> allPlayers = session.getAllPlayers();

        boolean isInParty = false;
        if (plugin.getPartyManager() != null) {
            isInParty = plugin.getPartyHandler().getParty(session.getSender()) != null;
        }

        if (allPlayers.size() == 2 && !isInParty) {
            Player enemy = allPlayers.stream().filter(player -> !player.equals(session.getSender())).findFirst().get();
            plugin.getRequestHandler().sendRequest(session.getSender(), enemy, session);
        } else {
            plugin.getDuelHandler().newDuel(session);
        }

        session.getSender().closeInventory();
    }

    @Override
    public void onClick(InventoryClickEvent event) {
        event.setCancelled(true);
    }
}
