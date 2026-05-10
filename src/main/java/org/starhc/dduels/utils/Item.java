package org.starhc.dduels.utils;

import net.kyori.adventure.text.Component;
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
    public static ItemStack create(Material material, int amount, Component displayName, Component... loreString) {
        ItemStack item;
        ArrayList lore = new ArrayList();

        item = new ItemStack(material, amount);

        ItemMeta meta = item.getItemMeta();

        meta.displayName(displayName);

        for (Component s : loreString) {
            lore.add(s);
        }
        meta.lore(lore);
        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack createEnchanted(Material material, int amount, Map<Enchantment, Integer> enchantements, Component displayName, Component... loreString) {
        ItemStack item;
        ArrayList lore = new ArrayList();

        item = new ItemStack(material, amount);

        ItemMeta meta = item.getItemMeta();
        meta.displayName(displayName);

        for (Component s : loreString) {
            lore.add(s);
        }
        meta.lore(lore);

        for (Map.Entry<Enchantment, Integer> enchant : enchantements.entrySet()) {
            meta.addEnchant(enchant.getKey(), enchant.getValue(), true);
        }
        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack createPlayerHead(String player, int amount, Component displayName, Component... loreString) {
        ItemStack item;
        ArrayList lore = new ArrayList();

        item = new ItemStack(Material.PLAYER_HEAD, amount);

        SkullMeta skullMeta = (SkullMeta) item.getItemMeta();

        skullMeta.setOwner(player);
        skullMeta.displayName(displayName);
        item.setItemMeta(skullMeta);

        ItemMeta meta = item.getItemMeta();

        for (Component s : loreString) {
            lore.add(s);
        }
        meta.lore(lore);
        item.setItemMeta(meta);

        return item;
    }


}
