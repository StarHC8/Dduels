package org.starhc.dduels.ui;

import fr.mrmicky.fastinv.InventoryScheme;
import fr.mrmicky.fastinv.PaginatedFastInv;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionType;
import org.starhc.dduels.Dduels;
import org.starhc.dduels.models.DuelSession;
import org.starhc.dduels.models.Kit;
import org.starhc.dduels.utils.Item;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static org.starhc.dduels.utils.EnchantmentsUtils.getValidEnchantments;
import static org.starhc.dduels.utils.KitItemsList.*;

public class KitCreatorUi extends PaginatedFastInv {


    private static final int SLOT_PREVIOUS_PAGE = 17;
    private static final int SLOT_NEXT_PAGE = 35;
    private static final int SLOT_SAVE = 26;
    private static final int SLOT_DESTROY = 44;
    private static final int SLOT_HELMET = 47;
    private static final int SLOT_CHESTPLATE = 48;
    private static final int SLOT_LEGGINGS = 49;
    private static final int SLOT_BOOTS = 50;
    private static final int SLOT_OFFHAND = 51;

    private static final Map<Material, String> NAME_CACHE = new ConcurrentHashMap<>();
    private static final Map<PotionType, String> POTIONS_NAME_CACHE = new ConcurrentHashMap<>();

    private final Dduels plugin;
    private final DuelSession session;
    private final Kit selectedKit;

    private static final InventoryScheme SCHEME = new InventoryScheme()
            .mask("         ")
            .mask(" 111111  ")
            .mask(" 111111  ")
            .mask(" 111111  ")
            .mask("         ")
            .bindPagination('1');


    public KitCreatorUi(Dduels plugin, DuelSession session, Kit selectedKit) {
        super(54, PlainTextComponentSerializer.plainText().serialize(plugin.getConfigHandler().getMessageFromConfig("ui-names.kit-creator")));
        this.plugin = plugin;
        this.session = session;
        this.selectedKit = selectedKit;

        setupNavigation();
        setupPlayerInventory();
        setupArmorSlots();
        setupActionButtons();
        setupContent();

        SCHEME.apply(this);
    }

    private void setupNavigation() {
        previousPageItem(SLOT_PREVIOUS_PAGE, p -> Item.create(Material.ARROW, 1, Component.text("Page " + p + "/" + lastPage())));
        nextPageItem(SLOT_NEXT_PAGE, p -> Item.create(Material.ARROW, 1, Component.text("Page " + p + "/" + lastPage())));
    }

    private void setupPlayerInventory() {
        Player player = Bukkit.getPlayer(session.getSender());
        if (player != null) {
            player.getInventory().clear();
            player.getInventory().setContents(selectedKit.getContents());
        }
    }

    private void setupArmorSlots() {
        ItemStack[] armorItems = selectedKit.getArmor();
        int armorIndexToShow = 0;
        for (int slot : List.of(SLOT_BOOTS, SLOT_LEGGINGS, SLOT_CHESTPLATE, SLOT_HELMET)) {
            if (armorItems[armorIndexToShow] != null) {
                setItem(slot, armorItems[armorIndexToShow]);
            } else {
                Component name = Component.text("");
                switch (slot) {
                    case SLOT_HELMET:
                        name = plugin.getConfigHandler().getMessageFromConfig("items-names.helmet-slot");
                        break;
                    case SLOT_CHESTPLATE:
                        name = plugin.getConfigHandler().getMessageFromConfig("items-names.chestplate-slot");
                        break;
                    case SLOT_LEGGINGS:
                        name = plugin.getConfigHandler().getMessageFromConfig("items-names.leggings-slot");
                        break;
                    case SLOT_BOOTS:
                        name = plugin.getConfigHandler().getMessageFromConfig("items-names.boots-slot");
                        break;
                }
                setItem(slot, Item.create(Material.ARMOR_STAND, 1, name));
            }
            armorIndexToShow++;
        }

        ItemStack offHandItem = selectedKit.getOffHand();
        if (offHandItem != null) {
            setItem(SLOT_OFFHAND, offHandItem);
        } else {
            setItem(SLOT_OFFHAND, Item.create(Material.ITEM_FRAME, 1, plugin.getConfigHandler().getMessageFromConfig("items-names.offhand-slot")));
        }
    }

    private void setupActionButtons() {
        setItem(SLOT_SAVE, Item.create(Material.LIME_WOOL, 1, plugin.getConfigHandler().getMessageFromConfig("items-names.save-item")), event -> {
            Player player = Bukkit.getPlayer(session.getSender());
            if (player == null) return;

            ItemStack[] inventoryItemsToSave = player.getInventory().getStorageContents();
            ItemStack[] armorItemsToSave = new ItemStack[4];
            ItemStack offHandItemToSave;

            int armorIndexToSave = 0;
            for (int slot : List.of(SLOT_BOOTS, SLOT_LEGGINGS, SLOT_CHESTPLATE, SLOT_HELMET)) {
                ItemStack itemInSlot = event.getInventory().getItem(slot);
                if (itemInSlot != null && itemInSlot.getType() != Material.ARMOR_STAND) {
                    armorItemsToSave[armorIndexToSave] = itemInSlot;
                } else {
                    armorItemsToSave[armorIndexToSave] = null;
                }
                armorIndexToSave++;
            }

            ItemStack itemInSlot = event.getInventory().getItem(SLOT_OFFHAND);
            if (itemInSlot != null && itemInSlot.getType() != Material.ITEM_FRAME) {
                offHandItemToSave = itemInSlot;
            } else {
                offHandItemToSave = null;
            }

            plugin.getKitHandler().saveKit(player.getUniqueId(),
                    selectedKit.getSlot(),
                    inventoryItemsToSave,
                    armorItemsToSave,
                    offHandItemToSave).thenRun(() -> {
                Bukkit.getScheduler().runTask(plugin, () -> {
                    new KitSelectorUi(plugin, session).open(player);
                    player.sendMessage(plugin.getConfigHandler().getMessageFromConfig("kit-saved"));
                    player.getInventory().clear();
                });
            });
        });

        setItem(SLOT_DESTROY, Item.create(Material.COBWEB, 1, plugin.getConfigHandler().getMessageFromConfig("items-names.destroy-item")), event -> {
            event.setCancelled(true);
            event.getWhoClicked().setItemOnCursor(null);
        });
    }

    private void setupContent() {

        for (Material material : getKitUnstackableItems()) {
            String name = getItemDisplayName(material);
            ItemStack itemStack = Item.create(material, 1, Component.text(name));
            List<Enchantment> enchants = new ArrayList<>(getValidEnchantments(itemStack).keySet());
            final int[] selectedIndex = {0};

            updateLore(itemStack, enchants, selectedIndex[0]);

            addContent(itemStack, event -> {
                event.setCancelled(true);
                if (event.getClick() == ClickType.RIGHT) {
                    if (enchants.isEmpty()) return;
                    selectedIndex[0] = (selectedIndex[0] + 1) % enchants.size();
                    updateLore(itemStack, enchants, selectedIndex[0]);
                    getInventory().setItem(event.getSlot(), itemStack);
                } else if (event.getClick() == ClickType.SHIFT_LEFT) {
                    if (enchants.isEmpty()) return;
                    Enchantment currentEnch = enchants.get(selectedIndex[0]);
                    int currentLevel = itemStack.getEnchantmentLevel(currentEnch);
                    int nextLevel = (currentLevel + 1) > currentEnch.getMaxLevel() ? 0 : currentLevel + 1;
                    if (nextLevel == 0) {
                        itemStack.removeEnchantment(currentEnch);
                    } else {
                        itemStack.addUnsafeEnchantment(currentEnch, nextLevel);
                    }
                    updateLore(itemStack, enchants, selectedIndex[0]);
                    getInventory().setItem(event.getSlot(), itemStack);
                } else if (event.getClick() == ClickType.LEFT) {
                    ItemStack clone = itemStack.clone();
                    ItemMeta cloneMeta = clone.getItemMeta();
                    if (cloneMeta != null) {
                        cloneMeta.lore(null);
                        clone.setItemMeta(cloneMeta);
                    }
                    event.getWhoClicked().getInventory().addItem(clone);
                    for (Enchantment ench : new HashSet<>(itemStack.getEnchantments().keySet())) {
                        itemStack.removeEnchantment(ench);
                    }
                    selectedIndex[0] = 0;
                    updateLore(itemStack, enchants, selectedIndex[0]);
                    getInventory().setItem(event.getSlot(), itemStack);
                }
            });
        }

        for (Material material : getKitStackableItems()) {
            String name = getItemDisplayName(material);

            ItemStack itemStack = Item.create(material, 1, Component.text(name));
            addContent(itemStack, event -> {
                event.setCancelled(true);
                if (event.getClick() == ClickType.LEFT) {
                    ItemStack toGive = Item.create(itemStack.getType(), 64, itemStack.getItemMeta().displayName());
                    event.getWhoClicked().getInventory().addItem(toGive);
                }
            });
        }

        for (Material material : getKitLimitedStackableItems()) {
            String name = getItemDisplayName(material);

            ItemStack itemStack = Item.create(material, 1, Component.text(name));
            addContent(itemStack, event -> {
                event.setCancelled(true);
                if (event.getClick() == ClickType.LEFT) {
                    ItemStack toGive = Item.create(itemStack.getType(), 16, itemStack.getItemMeta().displayName());
                    event.getWhoClicked().getInventory().addItem(toGive);
                }
            });
        }

        for (PotionType baseType : getKitPotions()) {
            ItemStack itemStack = new ItemStack(Material.POTION);
            PotionMeta meta = (PotionMeta) itemStack.getItemMeta();
            if (meta == null) continue;
            meta.setBasePotionType(baseType);
            String name = getItemDisplayName(baseType);
            meta.displayName(Component.text(name));
            itemStack.setItemMeta(meta);

            final int[] selectedIndex = {0};
            updatePotionLore(itemStack, selectedIndex[0]);

            addContent(itemStack, event -> {
                event.setCancelled(true);
                PotionMeta currentMeta = (PotionMeta) itemStack.getItemMeta();
                if (currentMeta == null) return;
                PotionType currentType = currentMeta.getBasePotionType();

                if (event.getClick() == ClickType.RIGHT) {
                    selectedIndex[0] = (selectedIndex[0] + 1) % 2;
                    updatePotionLore(itemStack, selectedIndex[0]);
                    getInventory().setItem(event.getSlot(), itemStack);
                } else if (event.getClick() == ClickType.SHIFT_LEFT) {
                    if (selectedIndex[0] == 0) {
                        Material nextMaterial = Material.POTION;
                        if (itemStack.getType() == Material.POTION) nextMaterial = Material.SPLASH_POTION;
                        else if (itemStack.getType() == Material.SPLASH_POTION)
                            nextMaterial = Material.LINGERING_POTION;

                        itemStack.setType(nextMaterial);
                    } else {
                        PotionType base = getBaseType(currentType);
                        PotionType strong = getStrongVariant(base);
                        PotionType longV = getLongVariant(base);

                        PotionType nextType = base;
                        if (currentType == base) {
                            if (strong != null) nextType = strong;
                            else if (longV != null) nextType = longV;
                        } else if (currentType == strong) {
                            if (longV != null) nextType = longV;
                            else nextType = base;
                        } else {
                            nextType = base;
                        }
                        currentMeta.setBasePotionType(nextType);
                        itemStack.setItemMeta(currentMeta);
                    }
                    updatePotionLore(itemStack, selectedIndex[0]);
                    getInventory().setItem(event.getSlot(), itemStack);
                } else if (event.getClick() == ClickType.LEFT) {
                    ItemStack clone = itemStack.clone();
                    ItemMeta cloneMeta = clone.getItemMeta();
                    if (cloneMeta != null) {
                        cloneMeta.lore(null);
                        clone.setItemMeta(cloneMeta);
                    }
                    event.getWhoClicked().getInventory().addItem(clone);

                    itemStack.setType(Material.POTION);
                    PotionMeta resetMeta = (PotionMeta) itemStack.getItemMeta();
                    if (resetMeta != null) {
                        resetMeta.setBasePotionType(baseType);
                        itemStack.setItemMeta(resetMeta);
                    }
                    selectedIndex[0] = 0;
                    updatePotionLore(itemStack, selectedIndex[0]);
                    getInventory().setItem(event.getSlot(), itemStack);
                }
            });
        }

    }

    private void updateLore(ItemStack item, List<Enchantment> enchants, int selectedIndex) {
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return;
        List<Component> lore = new ArrayList<>();
        for (int i = 0; i < enchants.size(); i++) {
            Enchantment ench = enchants.get(i);
            int level = item.getEnchantmentLevel(ench);
            String name = ench.getKey().getKey().replace("_", " ");
            name = name.substring(0, 1).toUpperCase() + name.substring(1);
            Component line = MiniMessage.miniMessage().deserialize(
                    ((i == selectedIndex) ? "<aqua>▶ <white>" : "<gray> ") + name + ": <yellow>" + level
            );
            lore.add(line);
        }
        meta.lore(lore);
        item.setItemMeta(meta);
    }

    private void updatePotionLore(ItemStack item, int selectedIndex) {
        ItemMeta meta = item.getItemMeta();
        if (!(meta instanceof PotionMeta potionMeta)) return;

        List<Component> lore = new ArrayList<>();

        // Form
        String formName = item.getType().name().replace("_POTION", "").toLowerCase();
        if (formName.equals("potion")) formName = "normal";
        formName = formName.substring(0, 1).toUpperCase() + formName.substring(1);
        lore.add(MiniMessage.miniMessage().deserialize((selectedIndex == 0 ? "<aqua>▶ <white>" : "<gray> ") + "Form: <yellow>" + formName));

        // Upgrade
        PotionType type = potionMeta.getBasePotionType();
        String upgrade = "None";
        if (type.name().startsWith("STRONG_")) upgrade = "Level II";
        else if (type.name().startsWith("LONG_")) upgrade = "Extended";

        lore.add(MiniMessage.miniMessage().deserialize((selectedIndex == 1 ? "<aqua>▶ <white>" : "<gray> ") + "Upgrade: <yellow>" + upgrade));

        meta.lore(lore);
        item.setItemMeta(meta);
    }

    private PotionType getBaseType(PotionType type) {
        String name = type.name();
        if (name.startsWith("STRONG_")) {
            try {
                return PotionType.valueOf(name.substring(7));
            } catch (Exception ignored) {
            }
        }
        if (name.startsWith("LONG_")) {
            try {
                return PotionType.valueOf(name.substring(5));
            } catch (Exception ignored) {
            }
        }
        return type;
    }

    private PotionType getStrongVariant(PotionType base) {
        try {
            return PotionType.valueOf("STRONG_" + base.name());
        } catch (Exception e) {
            return null;
        }
    }

    private PotionType getLongVariant(PotionType base) {
        try {
            return PotionType.valueOf("LONG_" + base.name());
        } catch (Exception e) {
            return null;
        }
    }

    public String getItemDisplayName(Material material) {
        return NAME_CACHE.computeIfAbsent(material, mat -> {
            String name = mat.name().replace("_", " ").toLowerCase();
            return name.substring(0, 1).toUpperCase() + name.substring(1);
        });
    }

    public String getItemDisplayName(PotionType potionType) {
        return POTIONS_NAME_CACHE.computeIfAbsent(potionType, pType -> {
            String name = pType.name().replace("_", " ").toLowerCase();
            if (name.startsWith("strong_")) name = name.substring(7);
            if (name.startsWith("long_")) name = name.substring(5);
            return name.substring(0, 1).toUpperCase() + name.substring(1);
        });
    }

    @Override
    public void onClick(InventoryClickEvent event) {
        event.setCancelled(false);
        int eventSlot = event.getSlot();
        ItemStack cursor = event.getCursor();
        ItemStack itemInSlot = event.getInventory().getItem(eventSlot);
        Player clicker = (Player) event.getWhoClicked();

        if (event.getClick() == ClickType.DOUBLE_CLICK || event.getClick() == ClickType.DROP) {
            event.setCancelled(true);
            return;
        }

        if (event.getClick() == ClickType.RIGHT) {
            if (cursor.getType() != Material.AIR) return;

            if (event.getClickedInventory().equals(clicker.getInventory())) {
                event.setCancelled(true);
                clicker.setItemOnCursor(event.getClickedInventory().getItem(eventSlot));
            }
        }

        if (List.of(SLOT_PREVIOUS_PAGE, SLOT_SAVE, SLOT_NEXT_PAGE).contains(eventSlot)) {
            event.setCancelled(true);
            return;
        }

        if (List.of(SLOT_OFFHAND, SLOT_BOOTS, SLOT_LEGGINGS, SLOT_CHESTPLATE, SLOT_HELMET).contains(eventSlot)) {
            if (cursor != null && cursor.getType() != Material.AIR) {
                boolean valid = false;
                switch (eventSlot) {
                    case SLOT_HELMET:
                        valid = cursor.getType().name().endsWith("_HELMET");
                        break;
                    case SLOT_CHESTPLATE:
                        valid = cursor.getType().name().endsWith("_CHESTPLATE");
                        break;
                    case SLOT_LEGGINGS:
                        valid = cursor.getType().name().endsWith("_LEGGINGS");
                        break;
                    case SLOT_BOOTS:
                        valid = cursor.getType().name().endsWith("_BOOTS");
                        break;
                    case SLOT_OFFHAND:
                        valid = true;
                        break;
                }

                if (!valid) {
                    event.setCancelled(true);
                } else {
                    setItem(eventSlot, null);
                }
            } else {
                if (itemInSlot != null && itemInSlot.getType() != Material.ARMOR_STAND && itemInSlot.getType() != Material.ITEM_FRAME) {
                    event.setCancelled(false);
                    Bukkit.getScheduler().runTask(plugin, () -> {
                        switch (eventSlot) {
                            case SLOT_HELMET:
                                setItem(eventSlot, Item.create(Material.ARMOR_STAND, 1, plugin.getConfigHandler().getMessageFromConfig("items-names.helmet-slot")));
                                break;
                            case SLOT_CHESTPLATE:
                                setItem(eventSlot, Item.create(Material.ARMOR_STAND, 1, plugin.getConfigHandler().getMessageFromConfig("items-names.chestplate-slot")));
                                break;
                            case SLOT_LEGGINGS:
                                setItem(eventSlot, Item.create(Material.ARMOR_STAND, 1, plugin.getConfigHandler().getMessageFromConfig("items-names.leggings-slot")));
                                break;
                            case SLOT_BOOTS:
                                setItem(eventSlot, Item.create(Material.ARMOR_STAND, 1, plugin.getConfigHandler().getMessageFromConfig("items-names.boots-slot")));
                                break;
                            case SLOT_OFFHAND:
                                setItem(eventSlot, Item.create(Material.ITEM_FRAME, 1, plugin.getConfigHandler().getMessageFromConfig("items-names.offhand-slot")));
                                break;
                        }
                    });
                } else {
                    event.setCancelled(true);
                }
            }
        }
    }

    @Override
    public void onClose(InventoryCloseEvent event) {
        if (event.getReason().equals(InventoryCloseEvent.Reason.OPEN_NEW)) {
            return;
        }
        event.getPlayer().sendMessage(plugin.getConfigHandler().getMessageFromConfig("kit-unsaved"));
        event.getPlayer().getInventory().clear();
    }


}
