package org.starhc.dduels.handlers;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.starhc.dduels.Dduels;
import org.starhc.dduels.models.MapTemplate;
import org.starhc.dduels.models.Spawn;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;


public class MapTemplateHandler {

    private final Dduels plugin;
    private final List<MapTemplate> mapTemplates = new ArrayList<>();


    public MapTemplateHandler(Dduels plugin) {
        this.plugin = plugin;
        loadGameMapTemplates();
    }

    private void loadGameMapTemplates() {
        FileConfiguration mapsConfig = plugin.getConfigHandler().getConfig("maps");
        ConfigurationSection mapsSection = mapsConfig.getConfigurationSection("maps");
        if (mapsSection == null) {
            plugin.getLogger().log(Level.WARNING, "No 'maps' section found in maps.yml!");
            return;
        }

        mapsSection.getKeys(false).forEach(mapId -> {
            String templateName = mapsConfig.getString("maps." + mapId + ".world");
            String templateDisplayName = mapsConfig.getString("maps." + mapId + ".name");

            if (templateName == null || templateDisplayName == null) {
                plugin.getLogger().log(Level.WARNING, "Map " + mapId + " is missing world or name!");
                return;
            }

            Map<Integer, Spawn> spawns = new HashMap<>();
            ConfigurationSection spawnsSection = mapsConfig.getConfigurationSection("maps." + mapId + ".spawns");
            if (spawnsSection == null) {
                plugin.getLogger().log(Level.WARNING, "Map " + mapId + " (" + templateDisplayName + ") has no spawns section!");
                return;
            }

            spawnsSection.getKeys(false).forEach(spawnId -> {
                Spawn spawn = new Spawn(
                        mapsConfig.getDouble("maps." + mapId + ".spawns." + spawnId + ".x"),
                        mapsConfig.getDouble("maps." + mapId + ".spawns." + spawnId + ".y"),
                        mapsConfig.getDouble("maps." + mapId + ".spawns." + spawnId + ".z"),
                        mapsConfig.getDouble("maps." + mapId + ".spawns." + spawnId + ".yaw")
                );

                try {
                    spawns.put(Integer.parseInt(spawnId), spawn);
                } catch (NumberFormatException e) {
                    plugin.getLogger().log(Level.WARNING, "Invalid spawn ID '" + spawnId + "' for map " + mapId);
                }
            });

            MapTemplate template = new MapTemplate(templateName, templateDisplayName, spawns);
            mapTemplates.add(template);
        });
    }


    public List<MapTemplate> getMapTemplates() {
        return mapTemplates;
    }

}
