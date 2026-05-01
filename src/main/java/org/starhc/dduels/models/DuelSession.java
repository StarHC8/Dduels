package org.starhc.dduels.models;

import org.bukkit.entity.Player;
import java.util.List;
import java.util.Optional;

public class DuelSession {
    private final Player sender;
    private final List<Player> enemies;
    private MapTemplate selectedMapTemplate;
    private Kit selectedKit;

    public DuelSession(Player sender, List<Player> enemies) {
        this.sender = sender;
        this.enemies = enemies;
    }

    public Player getSender() {
        return sender;
    }

    public List<Player> getEnemies() {
        return enemies;
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

    public boolean isReady() {
        return selectedMapTemplate != null && selectedKit != null;
    }
}
