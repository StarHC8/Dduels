package org.starhc.dduels.handlers;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.starhc.dduels.Dduels;
import org.starhc.dduels.models.Duel;

import java.util.List;
import java.util.UUID;

public class TeamHandler {
    private Dduels plugin;

    public TeamHandler(Dduels plugin) {
        this.plugin = plugin;
    }

    public boolean areInSameTeam(Player player, Player damager) {
        Duel duel = plugin.getDuelHandler().getDuel(damager);
        if (duel == null) return false;
        
        UUID playerUUID = player.getUniqueId();
        UUID damagerUUID = damager.getUniqueId();
        
        return (duel.getTeamA().contains(playerUUID) && duel.getTeamA().contains(damagerUUID)) || (duel.getTeamB().contains(playerUUID) && duel.getTeamB().contains(damagerUUID));
    }

    public void applyDuelTeamName(List<UUID> players, List<UUID> teamA_UUIDs, List<UUID> teamB_UUIDs) {
        Scoreboard duelBoard = Bukkit.getScoreboardManager().getNewScoreboard();

        Team teamA = duelBoard.registerNewTeam("a");
        teamA.setColor(ChatColor.RED);

        Team teamB = duelBoard.registerNewTeam("b");
        teamB.setColor(ChatColor.BLUE);

        for (UUID uuid : teamA_UUIDs) {
            String name = Bukkit.getOfflinePlayer(uuid).getName();
            if (name != null) teamA.addEntry(name);
        }

        for (UUID uuid : teamB_UUIDs) {
            String name = Bukkit.getOfflinePlayer(uuid).getName();
            if (name != null) teamB.addEntry(name);
        }

        for (UUID uuid : players) {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null) {
                player.setScoreboard(duelBoard);
            }
        }

    }

    public void resetPlayersNames(Player player) {
        Scoreboard main = Bukkit.getScoreboardManager().getMainScoreboard();
        player.setScoreboard(main);
    }

}
