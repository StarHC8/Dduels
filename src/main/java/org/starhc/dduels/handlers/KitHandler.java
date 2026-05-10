package org.starhc.dduels.handlers;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.starhc.dduels.Dduels;
import org.starhc.dduels.models.Kit;
import org.starhc.dduels.utils.SerializationUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;

public class KitHandler {
    private final Dduels plugin;

    public KitHandler(Dduels plugin) {
        this.plugin = plugin;
    }

    public CompletableFuture<Void> saveKit(UUID uuid, int slot, ItemStack[] contents, ItemStack[] armor, ItemStack offHand) {
        CompletableFuture<Void> future = new CompletableFuture<>();
        String query = "INSERT INTO " + plugin.getDatabase().getKitTable() + " (uuid, slot, inventory_data, armor_data, offhand_data) VALUES (?, ?, ?, ?, ?) " +
                       "ON DUPLICATE KEY UPDATE inventory_data = ?, armor_data = ?, offhand_data = ?";

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                String invData = SerializationUtils.itemStackArrayToBase64(contents);
                String armorData = SerializationUtils.itemStackArrayToBase64(armor);
                String offHandData = SerializationUtils.itemStackArrayToBase64(new ItemStack[]{offHand});

                try (Connection conn = plugin.getDatabase().getConnection();
                     PreparedStatement ps = conn.prepareStatement(query)) {
                    ps.setString(1, uuid.toString());
                    ps.setInt(2, slot);
                    ps.setString(3, invData);
                    ps.setString(4, armorData);
                    ps.setString(5, offHandData);
                    ps.setString(6, invData);
                    ps.setString(7, armorData);
                    ps.setString(8, offHandData);
                    ps.executeUpdate();
                    future.complete(null);
                }
            } catch (Exception e) {
                plugin.getLogger().log(Level.SEVERE, "An error occurred while saving " + uuid + " 's kit: ", e);
                future.completeExceptionally(e);
            }
        });
        return future;
    }

    public Kit getKit(UUID uuid, int slot) {
        String query = "SELECT * FROM " + plugin.getDatabase().getKitTable() + " WHERE uuid = ? AND slot = ?";

        try (Connection conn = plugin.getDatabase().getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, uuid.toString());
            ps.setInt(2, slot);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapKit(rs, uuid);
                }
            }
        } catch (Exception e) {
            plugin.getLogger().log(Level.SEVERE, "An error occurred while loading a " + uuid + " 's kit: ", e);
        }
        return null;
    }

    public List<Kit> getKits(UUID uuid) {
        String query = "SELECT * FROM " + plugin.getDatabase().getKitTable() + " WHERE uuid = ?";
        List<Kit> kits = new ArrayList<>();

        try (Connection conn = plugin.getDatabase().getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, uuid.toString());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    kits.add(mapKit(rs, uuid));
                }
            }
        } catch (Exception e) {
            plugin.getLogger().log(Level.SEVERE, "An error occurred while loading " + uuid + " 's kits: ", e);
        }
        return kits;
    }

    private Kit mapKit(ResultSet rs, UUID uuid) throws Exception {
        int slot = rs.getInt("slot");
        String invData = rs.getString("inventory_data");
        String armorData = rs.getString("armor_data");
        String offHandData = rs.getString("offhand_data");

        ItemStack[] contents = SerializationUtils.itemStackArrayFromBase64(invData);
        ItemStack[] armor = SerializationUtils.itemStackArrayFromBase64(armorData);
        ItemStack offHand = SerializationUtils.itemStackArrayFromBase64(offHandData)[0];

        return new Kit(uuid, slot, contents, armor, offHand);
    }


    public CompletableFuture<Void> deleteKit(UUID uuid, int slot) {
        CompletableFuture<Void> future = new CompletableFuture<>();
        String query = "DELETE FROM " + plugin.getDatabase().getKitTable() + " WHERE uuid = ? AND slot = ?";

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try (Connection conn = plugin.getDatabase().getConnection();
                 PreparedStatement ps = conn.prepareStatement(query)) {
                ps.setString(1, uuid.toString());
                ps.setInt(2, slot);
                ps.executeUpdate();
                future.complete(null);
            } catch (SQLException e) {
                plugin.getLogger().log(Level.SEVERE, "An error occurred while deleting " + uuid + " 's kit: ", e);
                future.completeExceptionally(e);
            }
        });
        return future;
    }
}
