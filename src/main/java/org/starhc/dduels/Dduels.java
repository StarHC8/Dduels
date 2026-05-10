package org.starhc.dduels;

import fr.mrmicky.fastinv.FastInvManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.TabCompleter;
import org.bukkit.plugin.java.JavaPlugin;
import org.starhc.dduels.commands.*;
import org.starhc.dduels.database.MySQL;
import org.starhc.dduels.handlers.*;
import org.starhc.dduels.listeners.HitListener;
import org.starhc.dduels.listeners.JoinOrQuitListener;
import org.starhc.dduels.listeners.PlayerDeathListener;
import org.starhc.dduels.listeners.SpectatorListener;
import org.starhc.partyManager.PartyManager;
import org.starhc.partyManager.handlers.PartyHandler;

import java.awt.*;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;

public final class Dduels extends JavaPlugin {

    private ConfigHandler configHandler;
    private MySQL database;
    private StatsHandler statsHandler;
    private KitHandler kitHandler;
    private WorldsHandler worldsHandler;
    private MapTemplateHandler mapTemplateHandler;
    private RequestHandler requestHandler;
    private DuelHandler duelHandler;
    private SpectatorHandler spectatorHandler;
    private TeamHandler teamHandler;
    private LobbyHandler lobbyHandler;

    private PartyManager partyManager;
    private PartyHandler partyHandler;

    @Override
    public void onEnable() {

        getLogger().log(Level.INFO, "Dduels started!");

        configHandler = new ConfigHandler(this);
        worldsHandler = new WorldsHandler(this);
        mapTemplateHandler = new MapTemplateHandler(this);
        requestHandler = new RequestHandler(this);
        duelHandler = new DuelHandler(this);
        spectatorHandler = new SpectatorHandler(this);
        teamHandler = new TeamHandler(this);
        lobbyHandler = new LobbyHandler(this);

        int port = configHandler.getConfig("settings").getInt("port");
        String host = configHandler.getConfig("settings").getString("host");
        String db = configHandler.getConfig("settings").getString("database");
        String stats_table = configHandler.getConfig("settings").getString("stats_table");
        String kits_table = configHandler.getConfig("settings").getString("kits_table");
        String username = configHandler.getConfig("settings").getString("username");
        String password = configHandler.getConfig("settings").getString("password");

        try {
            database = new MySQL(this, port, host, db, stats_table, kits_table, username, password);
            getLogger().info("Connected to database!");
            statsHandler = new StatsHandler(this);
            kitHandler = new KitHandler(this);

        } catch (Exception e) {
            getLogger().log(Level.SEVERE, "Could not connect to database!", e);
            Bukkit.getPluginManager().disablePlugin(this);
        }

        if (Bukkit.getPluginManager().getPlugin("PartyManager") != null) {
            partyManager = (PartyManager) Bukkit.getPluginManager().getPlugin("PartyManager");
            assert partyManager != null;
            partyHandler = partyManager.getPartyHandler();
            getLogger().log(Level.INFO, "PartyManager found!");
        } else {
            getLogger().log(Level.WARNING, "Could not find PartyManager! Party-related functions will not be available.");
        }

        loadCommand("duel", new DuelCommand(this), new DuelCommand(this));
        loadCommand("duelaccept", new DuelAcceptCommand(this), new DuelAcceptCommand(this));
        loadCommand("leave", new LeaveCommand(this), new LeaveCommand(this));
        loadCommand("spectate", new SpectateCommand(this), new SpectateCommand(this));
        loadCommand("stats", new StatsCommand(this), new StatsCommand(this));
        loadCommand("partyduel", new PartyDuelCommand(this), new PartyDuelCommand(this));

        getServer().getPluginManager().registerEvents(new PlayerDeathListener(this), this);
        getServer().getPluginManager().registerEvents(new JoinOrQuitListener(this), this);
        getServer().getPluginManager().registerEvents(new SpectatorListener(this), this);
        getServer().getPluginManager().registerEvents(new HitListener(this), this);
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

    public StatsHandler getStatsHandler() {
        return statsHandler;
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

    public SpectatorHandler getSpectatorHandler() {
        return spectatorHandler;
    }

    public TeamHandler getTeamHandler() {
        return teamHandler;
    }

    public LobbyHandler getLobbyHandler() {
        return lobbyHandler;
    }

    public PartyManager getPartyManager() {
        return partyManager;
    }

    public PartyHandler getPartyHandler() {
        return partyHandler;
    }

    @Override
    public void onDisable() {
        getLogger().log(Level.INFO, "Starting cleaning up orphaned duel worlds and stopping Dduels: ");
        getWorldsHandler().cleanupOrphanedWorlds();
        if (database != null) {
            database.close();
        }
    }

    private void loadCommand(String name, CommandExecutor executor, TabCompleter tabCompleter) {
        Objects.requireNonNull(getCommand(name)).setExecutor(executor);
        if (tabCompleter != null) {
            Objects.requireNonNull(getCommand(name)).setTabCompleter(tabCompleter);
        }
    }


}
