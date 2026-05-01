package org.starhc.dduels.handlers;

import org.bukkit.entity.Player;
import org.starhc.dduels.Dduels;
import org.starhc.dduels.models.*;

import java.util.*;

public class DuelHandler {
    private Dduels plugin;
    public DuelHandler(Dduels plugin) { this.plugin = plugin; }

    private List<Duel> activeDuels = new ArrayList<>();


    public void newDuel(DuelSession session) {

        List<Player> players = new ArrayList<>(session.getEnemies());
        players.add(session.getSender());

        Duel duel = new Duel(plugin, players, session.getSelectedMapTemplate().get(), session.getSelectedKit().get());
        duel.init();
        activeDuels.add(duel);
    }

    public Duel getDuel(Player player) {
        for (Duel duel : activeDuels) {
            if (duel.getPlayers().contains(player)) {
                return duel;
            }
        }
        return null;
    }

    public void deleteDuel(Duel duel) {
        activeDuels.remove(duel);
    }



}
