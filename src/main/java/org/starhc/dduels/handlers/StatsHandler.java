package org.starhc.dduels.handlers;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.starhc.dduels.Dduels;
import org.starhc.dduels.models.PlayerStats;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;

public class StatsHandler {
    private final Dduels plugin;

    public StatsHandler(Dduels plugin) {
        this.plugin = plugin;
    }

    public void addPlayer(Player player) {
        String query = "INSERT INTO " + plugin.getDatabase().getStatsTable() + " (uuid, wins, losses, duels, win_streak, longest_win_streak) VALUES (?, ?, ?, ?, ?, ?)";
        String uuid = player.getUniqueId().toString();
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try (Connection conn = plugin.getDatabase().getConnection(); PreparedStatement ps = conn.prepareStatement(query)) {
                ps.setString(1, uuid);
                ps.setInt(2, 0);
                ps.setInt(3, 0);
                ps.setInt(4, 0);
                ps.setInt(5, 0);
                ps.setInt(6, 0);
                ps.executeUpdate();

            } catch (SQLException e) {
                plugin.getLogger().log(Level.SEVERE, "An error occurred while adding " + uuid + " into the database: ", e);
            }
        });

    }

    public void addWin(OfflinePlayer player) {

        String query = "UPDATE " + plugin.getDatabase().getStatsTable() + " SET wins = wins + 1," + " duels = duels + 1," + " win_streak = win_streak + 1," + " longest_win_streak = GREATEST(longest_win_streak, win_streak)" + " WHERE uuid = ?";

        String uuid = player.getUniqueId().toString();
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try (Connection conn = plugin.getDatabase().getConnection(); PreparedStatement ps = conn.prepareStatement(query)) {
                ps.setString(1, uuid);
                ps.executeUpdate();

            } catch (SQLException e) {
                plugin.getLogger().log(Level.SEVERE, "An error occurred while changing a " + uuid + "'s statistic: ", e);
            }
        });

    }

    public void addLoss(OfflinePlayer player) {

        String query = "UPDATE " + plugin.getDatabase().getStatsTable() + " SET losses = losses + 1," + " duels = duels + 1," + " win_streak = 0" + " WHERE uuid = ?";

        String uuid = player.getUniqueId().toString();
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try (Connection conn = plugin.getDatabase().getConnection(); PreparedStatement ps = conn.prepareStatement(query)) {
                ps.setString(1, uuid);
                ps.executeUpdate();

            } catch (SQLException e) {
                plugin.getLogger().log(Level.SEVERE, "An error occurred while changing a " + uuid + "'s statistic: ", e);
            }
        });

    }

    public CompletableFuture<PlayerStats> getPlayerStatsFromDB(UUID uuid) {
        CompletableFuture<PlayerStats> future = new CompletableFuture<>();
        String query = "SELECT * FROM " + plugin.getDatabase().getStatsTable() + " WHERE uuid = ?";

        try (Connection conn = plugin.getDatabase().getConnection(); PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, uuid.toString());

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int wins = rs.getInt("wins");
                int losses = rs.getInt("losses");
                int duels = rs.getInt("duels");
                int winStreak = rs.getInt("win_streak");
                int longestWinStreak = rs.getInt("longest_win_streak");

                future.complete(new PlayerStats(uuid, wins, losses, duels, winStreak, longestWinStreak));
                return future;
            }
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "An error occurred while loading " + uuid + "'s statistics: ", e);
            future.completeExceptionally(e);
            return future;

        }

        future.complete(null);
        return future;
    }

    public CompletableFuture<Boolean> isPlayerInDatabase(UUID uuid) {
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        String query = "SELECT EXISTS(SELECT 1 FROM " + plugin.getDatabase().getStatsTable() + " WHERE uuid = ?)";

        try (Connection conn = plugin.getDatabase().getConnection(); PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, uuid.toString());

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                future.complete(rs.getInt(1) == 1);
            }

        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "An error occurred while checking if " + uuid + " is in database: ", e);
            future.completeExceptionally(e);
        }

        return future;
    }

}
