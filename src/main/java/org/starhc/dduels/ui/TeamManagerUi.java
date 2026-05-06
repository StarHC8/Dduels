package org.starhc.dduels.ui;

import fr.mrmicky.fastinv.FastInv;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.starhc.dduels.Dduels;
import org.starhc.dduels.models.DuelSession;
import org.starhc.dduels.models.MapTemplate;
import org.starhc.dduels.utils.Item;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TeamManagerUi extends FastInv {

    private final int GUIDE_SLOT = 4;
    private final int SHUFFLE_SLOT = 31;
    private final int SAVE_SLOT = 49;

    private final List<Integer> NOT_USED_SLOTS = List.of(4, 13, 22, 31, 40, 49);

    private final List<Integer> TEAM_A_SLOTS = List.of(
            0, 1, 2, 3,
            9, 10, 11, 12,
            18, 19, 20, 21,
            27, 28, 29, 30,
            36, 37, 38, 39,
            45, 46, 47, 48);

    private final List<Integer> TEAM_B_SLOTS = List.of(
            5, 6, 7, 8,
            14, 15, 16, 17,
            23, 24, 25, 26,
            32, 33, 34, 35,
            41, 42, 43, 44,
            50, 51, 52, 53
    );

    private final Dduels plugin;
    private final DuelSession session;

    public TeamManagerUi(Dduels plugin, DuelSession session) {
        super(54, plugin.getConfigHandler().getMessageFromConfig("ui-names.team-manager"));
        this.plugin = plugin;
        this.session = session;

        setupItems();
    }

    private void setupItems() {

        setItems(NOT_USED_SLOTS, Item.create(Material.GRAY_STAINED_GLASS_PANE, 1, ""));

        setItem(GUIDE_SLOT, Item.create(Material.WRITTEN_BOOK,
                1,
                plugin.getConfigHandler().getMessageFromConfig("items-names.team-manager-guide-item"),
                plugin.getConfigHandler().getMessageFromConfig("items-names.team-manager-guide-lore").split("\\n")));

        setItem(SHUFFLE_SLOT, Item.create(Material.ITEM_FRAME, 1, plugin.getConfigHandler().getMessageFromConfig("items-names.team-manager-shuffle-item")), event -> {
            event.getWhoClicked().setItemOnCursor(null);
            List<Player> allPlayers = new ArrayList<>(session.getAllPlayers());
            Collections.shuffle(allPlayers);

            List<Player> teamAToSave = new ArrayList<>(allPlayers.subList(0, allPlayers.size() / 2));
            List<Player> teamBToSave = new ArrayList<>(allPlayers.subList(allPlayers.size() / 2, allPlayers.size()));

            session.setTeamA(teamAToSave);
            session.setTeamB(teamBToSave);

            setupItems();
        });

        setItem(SAVE_SLOT, Item.create(Material.LIME_WOOL, 1, plugin.getConfigHandler().getMessageFromConfig("items-names.save-item")), event -> {
            Inventory clickedInventory = event.getClickedInventory();

            if (event.getWhoClicked().getItemOnCursor().getType() != Material.AIR) {
                return;
            }

            List<Player> teamAToSave = new ArrayList<>();
            List<Player> teamBToSave = new ArrayList<>();

            for (int i = 0; i < 24; i++) {
                ItemStack playerAItem = clickedInventory.getItem(TEAM_A_SLOTS.get(i));
                ItemStack playerBItem = clickedInventory.getItem(TEAM_B_SLOTS.get(i));

                if (playerAItem != null) {
                    SkullMeta skullMeta = (SkullMeta) playerAItem.getItemMeta();
                    Player skullPlayer = skullMeta.getOwningPlayer().getPlayer();

                    if (skullPlayer != null) {
                        teamAToSave.add(skullPlayer);
                    }
                }

                if (playerBItem != null) {
                    SkullMeta skullMeta = (SkullMeta) playerBItem.getItemMeta();
                    Player skullPlayer = skullMeta.getOwningPlayer().getPlayer();

                    if (skullPlayer != null) {
                        teamBToSave.add(skullPlayer);
                    }
                }
            }

            if (teamAToSave.isEmpty() || teamBToSave.isEmpty()) {
                session.getSender().sendMessage(plugin.getConfigHandler().getMessageFromConfig("empty-team"));
                return;
            }

            session.setTeamA(teamAToSave);
            session.setTeamB(teamBToSave);

            new DuelUi(plugin, session).open(session.getSender());
        });

        for (int i = 0; i < 24; i++) {
            if (i < session.getTeamA().size()) {
                Player playerA = session.getTeamA().get(i);
                setItem(TEAM_A_SLOTS.get(i), Item.createPlayerHead(playerA.getName(), 1, "§r" + playerA.getName()));
            }

            if (i < session.getTeamB().size()) {
                Player playerB = session.getTeamB().get(i);
                setItem(TEAM_B_SLOTS.get(i), Item.createPlayerHead(playerB.getName(), 1, "§r" + playerB.getName()));
            }
        }

    }

    @Override
    public void onClick(InventoryClickEvent event) {
        event.setCancelled(false);
        int eventSlot = event.getSlot();
        Inventory clickedInventory = event.getClickedInventory();
        Player clicker = (Player) event.getWhoClicked();

        if (event.getClick() == ClickType.DOUBLE_CLICK || event.getClick() == ClickType.DROP || event.getClick() == ClickType.CONTROL_DROP) {
            event.setCancelled(true);
            return;
        }

        if (eventSlot == GUIDE_SLOT || eventSlot == SHUFFLE_SLOT || eventSlot == SAVE_SLOT || NOT_USED_SLOTS.contains(eventSlot)) {
            event.setCancelled(true);
            return;
        }

        if (clickedInventory.equals(clicker.getInventory())) {
            event.setCancelled(true);
        }

    }
}
