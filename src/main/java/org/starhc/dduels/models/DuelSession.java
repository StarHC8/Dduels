package org.starhc.dduels.models;

import org.bukkit.entity.Player;
import org.starhc.dduels.enums.DuelType;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DuelSession {
    private final Player sender;
    private List<Player> teamA;
    private List<Player> teamB;
    private MapTemplate selectedMapTemplate;
    private Kit selectedKit;
    private DuelType duelType;
    private List<Player> allPlayers;

    public DuelSession(Player sender, List<Player> allPlayers) {
        this.sender = sender;
        this.allPlayers = allPlayers;

        this.teamA = new ArrayList<>(allPlayers.subList(0, allPlayers.size() / 2));
        this.teamB = new ArrayList<>(allPlayers.subList(allPlayers.size() / 2, allPlayers.size()));
    }

    public Player getSender() {
        return sender;
    }

    public List<Player> getTeamA() {
        return teamA;
    }

    public List<Player> getTeamB() {
        return teamB;
    }

    public void setTeamA(List<Player> teamA) {
        this.teamA = teamA;
    }

    public void setTeamB(List<Player> teamB) {
        this.teamB = teamB;
    }

    public List<Player> getAllPlayers() {
        return allPlayers;
    }

    public Optional<MapTemplate> getSelectedMapTemplate() {
        return Optional.ofNullable(selectedMapTemplate);
    }

    public void setSelectedMapTemplate(MapTemplate selectedMapTemplate) {
        this.selectedMapTemplate = selectedMapTemplate;
    }

    public Optional<Kit> getSelectedKit() {
        return Optional.ofNullable(selectedKit);
    }

    public void setSelectedKit(Kit selectedKit) {
        this.selectedKit = selectedKit;
    }

    public DuelType getDuelType() {
        return duelType;
    }

    public void setDuelType(DuelType duelType) {
        this.duelType = duelType;
    }

}
