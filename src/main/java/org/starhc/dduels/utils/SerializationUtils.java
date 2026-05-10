package org.starhc.dduels.utils;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;
import java.util.List;

public class SerializationUtils {

    public static String itemStackArrayToString(ItemStack[] items) throws IllegalStateException {
        YamlConfiguration config = new YamlConfiguration();
        config.set("items", items);
        return config.saveToString();
    }

    public static ItemStack[] itemStackArrayFromString(String data) throws IOException {
        if (data == null || data.isEmpty()) return new ItemStack[0];

        YamlConfiguration config = new YamlConfiguration();
        try {
            config.loadFromString(data);
            List<?> list = config.getList("items");
            if (list == null) return new ItemStack[0];

            return list.toArray(new ItemStack[0]);
        } catch (InvalidConfigurationException e) {
            throw new IOException("An error occurred while deserializing the item stack.", e);
        }
    }
}
