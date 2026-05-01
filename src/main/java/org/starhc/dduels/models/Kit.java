package org.starhc.dduels.models;

import org.bukkit.inventory.ItemStack;
import org.starhc.dduels.utils.Item;

import java.util.UUID;

public class Kit {
    private final UUID creatorUuid;
    private final int slot;
    private final ItemStack[] contents;
    private final ItemStack[] armor;
    private final ItemStack offHand;

    public Kit(UUID creatorUuid, int slot, ItemStack[] contents, ItemStack[] armor, ItemStack offHand) {
        this.creatorUuid = creatorUuid;
        this.slot = slot;
        this.contents = contents;
        this.armor = armor;
        this.offHand = offHand;
    }


    public UUID getCreatorUuid() {
        return creatorUuid;
    }

    public int getSlot() {
        return slot;
    }

    public ItemStack[] getContents() {
        return contents;
    }

    public ItemStack[] getArmor() {
        return armor;
    }

    public ItemStack getOffHand() {
        return offHand;
    }
}
