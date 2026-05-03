package org.starhc.dduels.models;

import java.util.UUID;

public class PlayerStats {

    private final UUID uuid;

    private int wins;
    private int losses;
    private int duels;
    private int winStreak;
    private int longestWinStreak;

    public PlayerStats(UUID uuid, int wins, int losses, int duels, int winStreak, int longestWinStreak) {
        this.uuid = uuid;
        this.wins = wins;
        this.losses = losses;
        this.duels = duels;
        this.winStreak = winStreak;
        this.longestWinStreak = longestWinStreak;
    }

    public UUID getUuid() {
        return uuid;
    }

    public int getWins() {
        return wins;
    }

    public int getLosses() {
        return losses;
    }

    public int getDuels() {
        return duels;
    }

    public int getWinStreak() {
        return winStreak;
    }

    public int getLongestWinStreak() {
        return longestWinStreak;
    }
}