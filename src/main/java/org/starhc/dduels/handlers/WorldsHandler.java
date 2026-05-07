package org.starhc.dduels.handlers;

import org.bukkit.Bukkit;
import org.bukkit.GameRule;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.entity.Player;
import org.starhc.dduels.Dduels;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.UUID;
import java.util.logging.Level;

public class WorldsHandler {
    private final Dduels plugin;
    private final File templatesFolder;

    public WorldsHandler(Dduels plugin) {
        this.plugin = plugin;
        this.templatesFolder = new File(plugin.getDataFolder(), "templates");

        if (!templatesFolder.exists()) {
            templatesFolder.mkdirs();
        }

        cleanupOrphanedWorlds();
    }

    public void cleanupOrphanedWorlds() {
        File container = Bukkit.getWorldContainer();
        File[] folders = container.listFiles((dir, name) -> name.startsWith("duel_"));
        if (folders != null) {
            for (File folder : folders) {
                plugin.getLogger().log(Level.INFO, "Cleaning up orphaned duel world: " + folder.getName());
                deleteFolder(folder);
            }
        }
    }


    public World createWorldFromTemplate(String templateName) {
        String worldName = "duel_" + UUID.randomUUID().toString().substring(0, 8);
        File worldFolder = new File(Bukkit.getWorldContainer(), worldName);

        try {
            copyTemplate(templateName, worldFolder);

            WorldCreator creator = new WorldCreator(worldName);

            creator.generator(new VoidGenerator());

            World world = Bukkit.createWorld(creator);

            if (world == null) {
                plugin.getLogger().log(Level.SEVERE, "An unknown error occurred while creating world: " + worldName);
                return null;
            }

            world.setGameRule(GameRule.DO_MOB_SPAWNING, false);
            world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
            world.setGameRule(GameRule.DO_WEATHER_CYCLE, false);
            world.setGameRule(GameRule.DO_IMMEDIATE_RESPAWN, true);
            world.setGameRule(GameRule.FALL_DAMAGE, false);
            world.setGameRule(GameRule.FIRE_DAMAGE, true);
            world.setGameRule(GameRule.KEEP_INVENTORY, true);
            world.setGameRule(GameRule.MAX_ENTITY_CRAMMING, 100);
            world.setGameRule(GameRule.RANDOM_TICK_SPEED, 0);
            world.setGameRule(GameRule.LOG_ADMIN_COMMANDS, false);
            world.setGameRule(GameRule.ANNOUNCE_ADVANCEMENTS, false);

            plugin.getLogger().log(Level.INFO, "Created duel world: " + worldName + " from template: " + templateName);
            return world;
        } catch (IOException | RuntimeException e) {
            plugin.getLogger().log(Level.SEVERE, "An unknown error occurred while creating world: " + worldName, e);
            return null;
        }
    }

    /**
     * Deletes a world and its folder.
     *
     * @param world The world to delete.
     * @return true if successful.
     */

    public boolean deleteWorld(World world) {
        if (world == null) return false;

        String worldName = world.getName();
        File worldFolder = world.getWorldFolder();

        World defaultWorld = Bukkit.getWorlds().get(0);
        for (Player player : world.getPlayers()) {
            player.teleport(defaultWorld.getSpawnLocation());
        }

        boolean unloaded = Bukkit.unloadWorld(world, false);
        if (!unloaded) {
            plugin.getLogger().log(Level.WARNING, "An error occurred while unloading world: " + worldName);
            return false;
        }

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            if (deleteFolder(worldFolder)) {
                plugin.getLogger().log(Level.INFO, "Deleted duel world folder: " + worldName);
            } else {
                plugin.getLogger().log(Level.WARNING, "An error occurred while deleting duel world folder: " + worldName);
            }
        });

        return true;
    }

    private void copyTemplate(String templateName, File targetFolder) throws IOException {
        File templateFolder = new File(templatesFolder, templateName);

        if (!templateFolder.exists() || !templateFolder.isDirectory()) {
            templateFolder = new File(Bukkit.getWorldContainer(), templateName);
            if (!templateFolder.exists()) {
                throw new IOException("Template '" + templateName + "' not found.");
            }
        }

        copyRecursive(templateFolder.toPath(), targetFolder.toPath());
    }

    private void copyRecursive(Path source, Path target) throws IOException {
        try (java.util.stream.Stream<Path> stream = Files.walk(source)) {
            stream.forEach(path -> {
                try {
                    Path relative = source.relativize(path);
                    Path destination = target.resolve(relative);

                    String fileName = path.getFileName().toString();
                    if (fileName.equals("session.lock") || fileName.equals("uid.dat")) {
                        return;
                    }

                    if (Files.isDirectory(path)) {
                        if (!Files.exists(destination)) {
                            Files.createDirectories(destination);
                        }
                    } else {
                        Files.copy(path, destination, StandardCopyOption.REPLACE_EXISTING);
                    }
                } catch (IOException e) {
                    throw new RuntimeException("Failed to copy " + path, e);
                }
            });
        }
    }

    private boolean deleteFolder(File folder) {
        File[] files = folder.listFiles();
        if (files != null) {
            for (File f : files) {
                if (f.isDirectory()) {
                    deleteFolder(f);
                } else {
                    f.delete();
                }
            }
        }
        return folder.delete();
    }

    private static class VoidGenerator extends org.bukkit.generator.ChunkGenerator {
        @Override
        public void generateNoise(org.bukkit.generator.WorldInfo worldInfo, java.util.Random random, int x, int z, org.bukkit.generator.ChunkGenerator.ChunkData chunkData) {
            // No noise (void)
        }

        @Override
        public void generateSurface(org.bukkit.generator.WorldInfo worldInfo, java.util.Random random, int x, int z, org.bukkit.generator.ChunkGenerator.ChunkData chunkData) {
            // No surface (void)
        }

        @Override
        public void generateBedrock(org.bukkit.generator.WorldInfo worldInfo, java.util.Random random, int x, int z, org.bukkit.generator.ChunkGenerator.ChunkData chunkData) {
            // No bedrock (void)
        }

        @Override
        public boolean shouldGenerateStructures() {
            return false;
        }

        @Override
        public boolean shouldGenerateCaves() {
            return false;
        }

        @Override
        public boolean shouldGenerateDecorations() {
            return false;
        }

        @Override
        public boolean shouldGenerateMobs() {
            return false;
        }
    }
}
