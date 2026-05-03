package org.starhc.dduels.ui;

import fr.mrmicky.fastinv.FastInv;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.starhc.dduels.Dduels;
import org.starhc.dduels.models.DuelSession;
import org.starhc.dduels.models.PlayerStats;
import org.starhc.dduels.utils.Item;

public class StatsUi extends FastInv {
    private static final int SLOT_WINS = 4;
    private static final int SLOT_LOSSES = 16;
    private static final int SLOT_DUELS = 10;
    private static final int SLOT_WIN_STREAK = 12;
    private static final int SLOT_LONGEST_WIN_STREAK = 14;
    private static final int SLOT_GO_BACK = 22;

    private final Dduels plugin;
    private final PlayerStats playerStats;

    public StatsUi(Dduels plugin, PlayerStats playerStats) {
        super(27, plugin.getConfigHandler().getMessageFromConfig("ui-names.stats").replace("[player]", Bukkit.getOfflinePlayer(playerStats.getUuid()).getName()));
        this.plugin = plugin;
        this.playerStats = playerStats;

        setupItems();
    }

    private void setupItems() {
        setItem(SLOT_DUELS, Item.create(
                Material.DIAMOND_SWORD,
                1,
                plugin.getConfigHandler().getMessageFromConfig("items-names.duels-item"),
                String.valueOf(playerStats.getDuels())
        ));

        setItem(SLOT_WIN_STREAK, Item.create(
                Material.DIAMOND,
                1,
                plugin.getConfigHandler().getMessageFromConfig("items-names.win-streak-item"),
                String.valueOf(playerStats.getWinStreak())
        ));

        setItem(SLOT_WINS, Item.create(
                Material.NETHER_STAR,
                1,
                plugin.getConfigHandler().getMessageFromConfig("items-names.wins-item"),
                String.valueOf(playerStats.getWins())
        ));

        setItem(SLOT_LONGEST_WIN_STREAK, Item.create(
                Material.ENCHANTED_GOLDEN_APPLE,
                1,
                plugin.getConfigHandler().getMessageFromConfig("items-names.longest-ws-item"),
                String.valueOf(playerStats.getLongestWinStreak())
        ));

        setItem(SLOT_LOSSES, Item.create(
                Material.RED_DYE,
                1,
                plugin.getConfigHandler().getMessageFromConfig("items-names.losses-item"),
                String.valueOf(playerStats.getLosses())
        ));

        setItem(SLOT_GO_BACK, Item.create(
                        Material.BARRIER,
                        1,
                        plugin.getConfigHandler().getMessageFromConfig("items-names.go-back-item")),
                event -> getInventory().close()
        );

    }

    @Override
    public void onClick(InventoryClickEvent event) {
        event.setCancelled(true);
    }


}
