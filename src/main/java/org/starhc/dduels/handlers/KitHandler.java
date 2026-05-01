package org.starhc.dduels.handlers;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.starhc.dduels.Dduels;
import org.starhc.dduels.models.Kit;
import org.starhc.dduels.utils.Item;
import org.starhc.dduels.utils.SerializationUtils;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.logging.Level;

public class KitHandler {
    private final Dduels plugin;

    public KitHandler(Dduels plugin) {
        this.plugin = plugin;
    }

    public void saveKit(UUID uuid, int slot, ItemStack[] contents, ItemStack[] armor, ItemStack offHand) {
        String invData = SerializationUtils.itemStackArrayToBase64(contents);
        String armorData = SerializationUtils.itemStackArrayToBase64(armor);
        String offHandData = SerializationUtils.itemStackArrayToBase64(new ItemStack[]{offHand});
        
        String query = "INSERT INTO " + plugin.getDatabase().getKitTable() + " (uuid, slot, inventory_data, armor_data, offhand_data) VALUES (?, ?, ?, ?, ?) " +
                       "ON DUPLICATE KEY UPDATE inventory_data = ?, armor_data = ?, offhand_data = ?";
        
        try (PreparedStatement ps = plugin.getDatabase().getConnection().prepareStatement(query)) {
            ps.setString(1, String.valueOf(uuid));
            ps.setString(2, String.valueOf(slot));
            ps.setString(3, invData);
            ps.setString(4, armorData);
            ps.setString(5, offHandData);
            ps.setString(6, invData);
            ps.setString(7, armorData);
            ps.setString(8, offHandData);
            ps.executeUpdate();
            

        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "An error occurred while loading a " + uuid + " 's kit: ", e);
        }
    }


    public Kit getKit(UUID uuid , int slot) {
        String query = "SELECT * FROM " + plugin.getDatabase().getKitTable() + " WHERE uuid = ? AND slot = ?";

        try {
            PreparedStatement ps = plugin.getDatabase().getConnection().prepareStatement(query);
            ps.setString(1, String.valueOf(uuid));
            ps.setString(2, String.valueOf(slot));
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                String invData = rs.getString("inventory_data");
                String armorData = rs.getString("armor_data");
                String offHandData = rs.getString("offhand_data");

                ItemStack[] contents = SerializationUtils.itemStackArrayFromBase64(invData);
                ItemStack[] armor = SerializationUtils.itemStackArrayFromBase64(armorData);
                ItemStack offHand = SerializationUtils.itemStackArrayFromBase64(offHandData)[0];

                return new Kit(uuid, slot, contents, armor, offHand);
            }

        } catch (SQLException | IOException e) {
            plugin.getLogger().log(Level.SEVERE, "An error occurred while loading a " + uuid + " 's kit: ", e);
        }
        return null;
    }

    public List<Kit> getKits(UUID uuid) {
        String query = "SELECT * FROM " + plugin.getDatabase().getKitTable() + " WHERE uuid = ?";
        List<Kit> kits = new ArrayList<>();


        try {
            PreparedStatement ps = plugin.getDatabase().getConnection().prepareStatement(query);
            ps.setString(1, String.valueOf(uuid));
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                int slot = rs.getInt("slot");
                String invData = rs.getString("inventory_data");
                String armorData = rs.getString("armor_data");
                String offHandData = rs.getString("offhand_data");

                ItemStack[] contents = SerializationUtils.itemStackArrayFromBase64(invData);
                ItemStack[] armor = SerializationUtils.itemStackArrayFromBase64(armorData);
                ItemStack offHand = SerializationUtils.itemStackArrayFromBase64(offHandData)[0];

                kits.add(new Kit(uuid, slot, contents, armor, offHand));
            }

            return kits;

        } catch (SQLException | IOException e) {
            plugin.getLogger().log(Level.SEVERE, "An error occurred while loading a " + uuid + " 's kit: ", e);
        }

        return List.of();
    }



    public boolean applyKit(Player player, int slot) {
        Kit kit = getKit(player.getUniqueId(), slot);
        if (kit == null) return false;
        
        player.getInventory().clear();
        player.getInventory().setStorageContents(kit.getContents());
        player.getInventory().setArmorContents(kit.getArmor());
        player.getInventory().setItemInOffHand(kit.getOffHand());
        player.updateInventory();
        return true;
    }

    public void deleteKit(UUID uuid , int slot) {

        String query = "DELETE FROM "+ plugin.getDatabase().getKitTable() +" WHERE uuid = ? AND slot = ?";

        try (PreparedStatement ps = plugin.getDatabase().getConnection().prepareStatement(query)) {
            ps.setString(1, String.valueOf(uuid));
            ps.setString(2, String.valueOf(slot));
            ps.executeUpdate();

        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "An error occurred while deleting a " + uuid + " 's kit: ", e);
        }
    }

}
