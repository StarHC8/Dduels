package org.starhc.dduels.handlers;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.starhc.dduels.Dduels;
import org.starhc.dduels.models.Duel;

import java.util.List;

public class TeamHandler {
    private Dduels plugin;

    public TeamHandler(Dduels plugin) {
        this.plugin = plugin;
    }

    public boolean areInSameTeam(Player player, Player damager) {
        Duel duel = plugin.getDuelHandler().getDuel(damager);
        return (duel.getTeamA().contains(player) && duel.getTeamA().contains(damager)) || (duel.getTeamB().contains(player) && duel.getTeamB().contains(damager));
    }

    public void applyDuelTeamName(List<Player> players, List<Player> teamAPlayers, List<Player> teamBPlayers) {
        Scoreboard duelBoard = Bukkit.getScoreboardManager().getNewScoreboard();

        Team teamA = duelBoard.registerNewTeam("a");
        teamA.setColor(ChatColor.RED);

        Team teamB = duelBoard.registerNewTeam("b");
        teamB.setColor(ChatColor.BLUE);

        for (Player p : teamAPlayers) {
            teamA.addEntry(p.getName());
        }

        for (Player p : teamBPlayers) {
            teamB.addEntry(p.getName());
        }

        for (Player player : players) {
            player.setScoreboard(duelBoard);
        }

    }

    public void resetPlayersNames(Player player) {
        Scoreboard main = Bukkit.getScoreboardManager().getMainScoreboard();
        player.setScoreboard(main);
    }

}
