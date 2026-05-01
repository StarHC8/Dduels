package org.starhc.dduels.database;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.sql.*;
import java.util.UUID;

public class MySQL {
    private Connection connection;
    private int port;
    private String host;
    private String database;
    private String kits_table;
    private String username;
    private String password;

    public MySQL(int port, String host, String database, String kits_table, String username, String password) throws SQLException {
        this.port = port;
        this.host = host;
        this.database = database;
        this.kits_table = kits_table;
        this.username = username;
        this.password = password;
        this.connection = DriverManager.getConnection(
                "jdbc:mysql://" + host + ":" + port + "/" + database,
                username,
                password
        );
        createTables();
    }

    private void createTables() {
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS " + kits_table + " (uuid VARCHAR(36), slot INT, inventory_data LONGTEXT, armor_data LONGTEXT, offhand_data LONGTEXT, UNIQUE (uuid, slot))");

        } catch (SQLException e) {

        }
    }

    public Connection getConnection() {
        return connection;
    }

    public boolean isConnected() {
        try {
            return connection != null && !connection.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }

    public String getKitTable() {
        return kits_table;
    }

}
