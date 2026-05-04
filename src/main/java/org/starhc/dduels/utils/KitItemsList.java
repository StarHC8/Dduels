package org.starhc.dduels.utils;

import org.bukkit.Material;
import org.bukkit.potion.PotionType;

import java.util.List;

public class KitItemsList {

    private static final List<PotionType> kitPotions = List.of(
            PotionType.REGENERATION, PotionType.SWIFTNESS, PotionType.FIRE_RESISTANCE,
            PotionType.HEALING, PotionType.NIGHT_VISION, PotionType.STRENGTH,
            PotionType.LEAPING, PotionType.INVISIBILITY, PotionType.POISON,
            PotionType.WEAKNESS, PotionType.SLOWNESS, PotionType.HARMING,
            PotionType.WATER_BREATHING, PotionType.SLOW_FALLING, PotionType.TURTLE_MASTER
    );

    private static final List<Material> kitUnstackableItems = List.of(
            // Wood
            Material.WOODEN_SWORD, Material.WOODEN_AXE, Material.WOODEN_PICKAXE, Material.WOODEN_SHOVEL, Material.WOODEN_HOE,

            // Stone
            Material.STONE_SWORD, Material.STONE_AXE, Material.STONE_PICKAXE, Material.STONE_SHOVEL, Material.STONE_HOE,

            // Iron
            Material.IRON_SWORD, Material.IRON_AXE, Material.IRON_PICKAXE, Material.IRON_SHOVEL, Material.IRON_HOE,

            // Gold
            Material.GOLDEN_SWORD, Material.GOLDEN_AXE, Material.GOLDEN_PICKAXE, Material.GOLDEN_SHOVEL, Material.GOLDEN_HOE,

            // Diamond
            Material.DIAMOND_SWORD, Material.DIAMOND_AXE, Material.DIAMOND_PICKAXE, Material.DIAMOND_SHOVEL, Material.DIAMOND_HOE,

            // Netherite
            Material.NETHERITE_SWORD, Material.NETHERITE_AXE, Material.NETHERITE_PICKAXE, Material.NETHERITE_SHOVEL, Material.NETHERITE_HOE,

            // Leather armor
            Material.LEATHER_HELMET, Material.LEATHER_CHESTPLATE, Material.LEATHER_LEGGINGS, Material.LEATHER_BOOTS,

            // Iron armor
            Material.IRON_HELMET, Material.IRON_CHESTPLATE, Material.IRON_LEGGINGS, Material.IRON_BOOTS,

            // Golden armor
            Material.GOLDEN_HELMET, Material.GOLDEN_CHESTPLATE, Material.GOLDEN_LEGGINGS, Material.GOLDEN_BOOTS,

            // Diamond armor
            Material.DIAMOND_HELMET, Material.DIAMOND_CHESTPLATE, Material.DIAMOND_LEGGINGS, Material.DIAMOND_BOOTS,

            // Netherite armor
            Material.NETHERITE_HELMET, Material.NETHERITE_CHESTPLATE, Material.NETHERITE_LEGGINGS, Material.NETHERITE_BOOTS,

            // Ranged / extra
            Material.BOW, Material.CROSSBOW, Material.TRIDENT, Material.FISHING_ROD, Material.SHIELD, Material.MACE, Material.BUCKET,
            Material.WATER_BUCKET, Material.LAVA_BUCKET, Material.TOTEM_OF_UNDYING, Material.OAK_BOAT, Material.FLINT_AND_STEEL

    );

    private static final List<Material> kitStackableItems = List.of(

            Material.FIREWORK_ROCKET, Material.ARROW, Material.END_CRYSTAL, Material.OBSIDIAN, Material.GLOWSTONE,

            Material.RESPAWN_ANCHOR, Material.EXPERIENCE_BOTTLE, Material.COBWEB, Material.WIND_CHARGE,
            Material.TNT, Material.COBBLESTONE, Material.OAK_PLANKS,
            Material.GOLDEN_APPLE, Material.ENCHANTED_GOLDEN_APPLE, Material.GOLDEN_CARROT, Material.BEEF,
            Material.CHORUS_FRUIT, Material.BAKED_POTATO
    );

    private static final List<Material> kitLimitedStackableItems = List.of(
            Material.ENDER_PEARL, Material.SNOWBALL, Material.EGG
    );

    public static List<PotionType> getKitPotions() {
        return kitPotions;
    }

    public static List<Material> getKitUnstackableItems() {
        return kitUnstackableItems;
    }

    public static List<Material> getKitStackableItems() {
        return kitStackableItems;
    }

    public static List<Material> getKitLimitedStackableItems() {
        return kitLimitedStackableItems;
    }
}
