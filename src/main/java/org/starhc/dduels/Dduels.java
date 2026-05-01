package org.starhc.dduels;

import fr.mrmicky.fastinv.FastInvManager;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.TabCompleter;
import org.bukkit.plugin.java.JavaPlugin;
import org.starhc.dduels.commands.DuelAcceptCommand;
import org.starhc.dduels.commands.DuelCommand;
import org.starhc.dduels.database.MySQL;
import org.starhc.dduels.handlers.*;
import org.starhc.dduels.listeners.JoinOrQuitListener;
import org.starhc.dduels.listeners.PlayerDeathEvent;


import java.sql.SQLException;
import java.util.Objects;
import java.util.logging.Level;

public final class Dduels extends JavaPlugin {

    private ConfigHandler configHandler;
    private MySQL database;
    private KitHandler kitHandler;
    private WorldsHandler worldsHandler;
    private MapTemplateHandler mapTemplateHandler;
    private RequestHandler requestHandler;
    private DuelHandler duelHandler;


    @Override
    public void onEnable() {

        getLogger().log(Level.INFO, "Dduels started!");

        configHandler = new ConfigHandler(this);
        worldsHandler = new WorldsHandler(this);
        mapTemplateHandler = new MapTemplateHandler(this);

        requestHandler = new RequestHandler(this);
        duelHandler = new DuelHandler(this);


        int port = configHandler.getConfig("settings").getInt("port");
        String host = configHandler.getConfig("settings").getString("host");
        String db = configHandler.getConfig("settings").getString("database");
        String kits_table = configHandler.getConfig("settings").getString("kits_table");
        String username = configHandler.getConfig("settings").getString("username");
        String password = configHandler.getConfig("settings").getString("password");

        try {
            database = new MySQL(port, host, db, kits_table, username, password);
            getLogger().info("Connected to database!");
            kitHandler = new KitHandler(this);
            
        } catch (SQLException e) {
            getLogger().log(Level.SEVERE, "Could not connect to database!", e);
        }

        loadCommand("duel", new DuelCommand(this), new DuelCommand(this));
        loadCommand("duelaccept", new DuelAcceptCommand(this), new DuelAcceptCommand(this));

        getServer().getPluginManager().registerEvents(new PlayerDeathEvent(this), this);
        getServer().getPluginManager().registerEvents(new JoinOrQuitListener(this), this);
        FastInvManager.register(this);

    }

    public MySQL getDatabase() {
        return database;
    }

    public WorldsHandler getWorldsHandler() {
        return worldsHandler;
    }

    public MapTemplateHandler getMapTemplateHandler() {
        return mapTemplateHandler;
    }

    public KitHandler getKitHandler() {
        return kitHandler;
    }

    public RequestHandler getRequestHandler() {
        return requestHandler;
    }

    public ConfigHandler getConfigHandler() {
        return configHandler;
    }

    public DuelHandler getDuelHandler() {
        return duelHandler;
    }


    @Override
    public void onDisable() {
        getLogger().log(Level.INFO, "Starting cleaning up orphaned duel worlds and stopping Dduels: ");
        getWorldsHandler().cleanupOrphanedWorlds();
    }

    private void loadCommand(String name, CommandExecutor executor, TabCompleter tabCompleter) {
        Objects.requireNonNull(getCommand(name)).setExecutor(executor);
        if (tabCompleter != null) {
            Objects.requireNonNull(getCommand(name)).setTabCompleter(tabCompleter);
        }
    }


}
