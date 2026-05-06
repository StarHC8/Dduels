package org.starhc.dduels.utils;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.starhc.dduels.Dduels;

import java.util.ArrayList;
import java.util.Map;

public class Item {
    public static void createAndSet(Inventory inv, Material material, int amount, int invSlot, String displayName, String... loreString) {
        ItemStack item = create(material, amount, displayName, loreString);
        inv.setItem(invSlot, item);
    }

    public static ItemStack create(Material material, int amount, String displayName, String... loreString) {
        ItemStack item;
        ArrayList lore = new ArrayList();

        item = new ItemStack(material, amount);

        ItemMeta meta = item.getItemMeta();

        meta.setDisplayName(displayName);

        for (String s : loreString) {
            lore.add(s);
        }
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack createEnchanted(Material material, int amount, Map<Enchantment, Integer> enchantements, String displayName, String... loreString) {
        ItemStack item;
        ArrayList lore = new ArrayList();

        item = new ItemStack(material, amount);

        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(displayName);

        for (String s : loreString) {
            lore.add(s);
        }
        meta.setLore(lore);

        for (Map.Entry<Enchantment, Integer> enchant : enchantements.entrySet()) {
            meta.addEnchant(enchant.getKey(), enchant.getValue(), true);
        }
        item.setItemMeta(meta);
        return item;
    }

    public static void createEnchantedAndSet(Inventory inv, Material material, int amount, int invSlot, Map<Enchantment, Integer> enchantements, String displayName, String... loreString) {
        ItemStack item = createEnchanted(material, amount, enchantements, displayName, loreString);
        inv.setItem(invSlot, item);
    }

    public static ItemStack createVisualEnchanted(Material material, int amount, String displayName, String... loreString) {
        ItemStack item;
        ArrayList lore = new ArrayList();

        item = new ItemStack(material, amount);

        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(displayName);

        for (String s : loreString) {
            lore.add(s);
        }
        meta.setLore(lore);

        meta.addEnchant(Enchantment.SHARPNESS, 1, true);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        item.setItemMeta(meta);

        return item;
    }

    public static void createVisualEnchantedAndSet(Inventory inv, Material material, int amount, int invSlot, String displayName, String... loreString) {
        ItemStack item = createVisualEnchanted(material, amount, displayName, loreString);
        inv.setItem(invSlot, item);
    }


    public static ItemStack createPlayerHead(String player, int amount, String displayName, String... loreString) {
        ItemStack item;
        ArrayList lore = new ArrayList();

        item = new ItemStack(Material.PLAYER_HEAD, amount);

        SkullMeta skullMeta = (SkullMeta) item.getItemMeta();

        skullMeta.setOwner(player);
        skullMeta.setDisplayName(displayName);
        item.setItemMeta(skullMeta);

        ItemMeta meta = item.getItemMeta();

        for (String s : loreString) {
            lore.add(s);
        }
        meta.setLore(lore);
        item.setItemMeta(meta);

        return item;
    }

    public static void  createPlayerHeadAndSet(Inventory inv, String player, int amount, int invSlot, String displayName, String... loreString) {
        ItemStack item = createPlayerHead(player, amount, displayName, loreString);
        inv.setItem(invSlot, item);
    }

    public static boolean getClickedItem(ItemStack clicked, String message) {
        return clicked.getItemMeta().getDisplayName().equalsIgnoreCase(message);
    }

}
