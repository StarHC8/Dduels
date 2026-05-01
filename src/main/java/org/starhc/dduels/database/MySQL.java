package org.starhc.dduels.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class MySQL {
    private final HikariDataSource dataSource;
    private final String kits_table;

    public MySQL(int port, String host, String database, String kits_table, String username, String password) {
        this.kits_table = kits_table;

        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:mysql://" + host + ":" + port + "/" + database);
        config.setUsername(username);
        config.setPassword(password);

        // Optimization for MySQL
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        config.addDataSourceProperty("useServerPrepStmts", "true");

        // Pool management
        config.setMaximumPoolSize(10);
        config.setMinimumIdle(2);
        config.setIdleTimeout(300000); // 5 minutes
        config.setConnectionTimeout(5000); // 5 seconds
        config.setMaxLifetime(1800000); // 30 minutes

        this.dataSource = new HikariDataSource(config);
        createTables();
    }

    private void createTables() {
        String query = "CREATE TABLE IF NOT EXISTS " + kits_table + " (" +
                "uuid VARCHAR(36), " +
                "slot INT, " +
                "inventory_data LONGTEXT, " +
                "armor_data LONGTEXT, " +
                "offhand_data LONGTEXT, " +
                "UNIQUE (uuid, slot))";

        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(query);
        } catch (SQLException e) {
            e.printStackTrace();
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

    public String getKitTable() {
        return kits_table;
    }
}
