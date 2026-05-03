package org.starhc.dduels.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.starhc.dduels.Dduels;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;

public class MySQL {
    private Dduels plugin;
    private final HikariDataSource dataSource;
    private final String stats_table;
    private final String kits_table;

    public MySQL(Dduels plugin, int port, String host, String database, String stats_table, String kits_table, String username, String password) {
        this.plugin = plugin;
        this.stats_table = stats_table;
        this.kits_table = kits_table;

        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:mysql://" + host + ":" + port + "/" + database);
        config.setUsername(username);
        config.setPassword(password);

        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        config.addDataSourceProperty("useServerPrepStmts", "true");

        config.setMaximumPoolSize(10);
        config.setMinimumIdle(2);
        config.setIdleTimeout(300000);
        config.setConnectionTimeout(5000);
        config.setMaxLifetime(1800000);

        this.dataSource = new HikariDataSource(config);
        createTables();
    }

    private void createTables() {
        String statsTableQuery = "CREATE TABLE IF NOT EXISTS " + stats_table + " (" +
                "uuid VARCHAR(36) PRIMARY KEY, " +
                "wins INT, " +
                "losses INT, " +
                "duels INT, " +
                "win_streak INT, " +
                "longest_win_streak INT)";

        String kitTableQuery = "CREATE TABLE IF NOT EXISTS " + kits_table + " (" +
                "uuid VARCHAR(36), " +
                "slot INT, " +
                "inventory_data LONGTEXT, " +
                "armor_data LONGTEXT, " +
                "offhand_data LONGTEXT, " +
                "UNIQUE (uuid, slot))";

        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(statsTableQuery);
            stmt.executeUpdate(kitTableQuery);
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "An error occurred while creating needed tables: ", e);
        }
    }

    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    public void close() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
        }
    }

    public String getStatsTable() {
        return stats_table;
    }

    public String getKitTable() {
        return kits_table;
    }
}
