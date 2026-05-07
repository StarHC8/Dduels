package org.starhc.dduels.handlers;

import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.starhc.dduels.Dduels;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ConfigHandler {

    private final Dduels plugin;
    private final Map<String, FileConfiguration> configs = new HashMap<>();
    private final Map<String, File> configFiles = new HashMap<>();
    private final Map<String, String> messagesCache = new HashMap<>();

    public ConfigHandler(Dduels plugin) {
        this.plugin = plugin;
        loadConfigs();
    }

    public void loadConfigs() {
        saveDefaultConfig("messages.yml");
        saveDefaultConfig("maps.yml");
        saveDefaultConfig("settings.yml");

        loadConfig("messages.yml");
        loadConfig("maps.yml");
        loadConfig("settings.yml");

        loadMessages();
    }

    private void loadMessages() {
        messagesCache.clear();
        FileConfiguration msgConfig = getConfig("messages");
        if (msgConfig == null) return;

        ConfigurationSection section = msgConfig.getConfigurationSection("messages");
        if (section == null) return;

        cacheSection(section, "");
    }

    private void cacheSection(ConfigurationSection section, String prefix) {
        for (String key : section.getKeys(false)) {
            Object value = section.get(key);
            String fullKey = prefix.isEmpty() ? key : prefix + "." + key;

            if (value instanceof ConfigurationSection childSection) {
                cacheSection(childSection, fullKey);
            } else if (value instanceof String str) {
                messagesCache.put(fullKey, ChatColor.translateAlternateColorCodes('&', str));
            }
        }
    }

    private void saveDefaultConfig(String name) {
        File file = new File(plugin.getDataFolder(), name);
        if (!file.exists()) {
            plugin.saveResource(name, false);
        }
    }

    private void loadConfig(String name) {
        File file = new File(plugin.getDataFolder(), name);
        if (!file.exists()) {
            saveDefaultConfig(name);
        }
        configs.put(name.replace(".yml", ""), YamlConfiguration.loadConfiguration(file));
        configFiles.put(name.replace(".yml", ""), file);
    }

    public FileConfiguration getConfig(String name) {
        if (name.equals("config")) {
            return plugin.getConfig();
        }
        return configs.get(name);
    }

    public void saveConfig(String name) {
        try {
            if (name.equals("config")) {
                plugin.saveConfig();
            } else {
                FileConfiguration config = configs.get(name);
                File file = configFiles.get(name);
                if (config != null && file != null) {
                    config.save(file);
                }
            }
        } catch (IOException e) {
            plugin.getLogger().severe("Failed to save config: " + name);
            e.printStackTrace();
        }
    }

    public void reloadConfig(String name) {
        if (name.equals("config")) {
            plugin.reloadConfig();
        } else {
            loadConfig(name + ".yml");
        }
        loadMessages();
    }

    public String getMessageFromConfig(String messagePath) {
        String message = messagesCache.get(messagePath);
        if (message != null) {
            return message;
        }
        return "§cMissing message: " + messagePath;
    }
}
