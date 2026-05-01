package org.starhc.dduels.utils;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EnchantmentsUtils {

    public static Map<Enchantment, List<Integer>> getValidEnchantments(ItemStack item) {

        Map<Enchantment, List<Integer>> result = new HashMap<>();

        for (Enchantment ench : Enchantment.values()) {
            if (ench.canEnchantItem(item)) {
                List<Integer> levels = new ArrayList<>();
                for (int i = ench.getStartLevel(); i <= ench.getMaxLevel(); i++) {
                    levels.add(i);
                }
                result.put(ench, levels);
            }
        }

        return result;

    }
}
