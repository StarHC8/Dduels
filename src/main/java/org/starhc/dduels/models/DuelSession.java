package org.starhc.dduels.models;

import org.starhc.dduels.enums.DuelType;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class DuelSession {
    private final UUID sender;
    private List<UUID> teamA;
    private List<UUID> teamB;
    private MapTemplate selectedMapTemplate;
    private Kit selectedKit;
    private DuelType duelType;
    private List<UUID> allPlayers;

    public DuelSession(UUID sender, List<UUID> allPlayers) {
        this.sender = sender;
        this.allPlayers = allPlayers;

        this.teamA = new ArrayList<>(allPlayers.subList(0, allPlayers.size() / 2));
        this.teamB = new ArrayList<>(allPlayers.subList(allPlayers.size() / 2, allPlayers.size()));
    }

    public UUID getSender() {
        return sender;
    }

    public List<UUID> getTeamA() {
        return teamA;
    }

    public List<UUID> getTeamB() {
        return teamB;
    }

    public void setTeamA(List<UUID> teamA) {
        this.teamA = teamA;
    }

    public void setTeamB(List<UUID> teamB) {
        this.teamB = teamB;
    }

    public List<UUID> getAllPlayers() {
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
