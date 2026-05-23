package org.starhc.dduels.handlers;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.starhc.dduels.Dduels;
import org.starhc.dduels.models.*;

import java.util.*;

public class DuelHandler {
    private Dduels plugin;

    public DuelHandler(Dduels plugin) {
        this.plugin = plugin;
    }

    private List<Duel> activeDuels = new ArrayList<>();


    public void newDuel(DuelSession session) {

        List<UUID> allPlayers = session.getAllPlayers();

        boolean valid = true;
        for (UUID uuid : allPlayers) {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null && getDuel(player) != null) {
                valid = false;
                break;
            }
        }

        if (!valid) {
            for (UUID uuid : allPlayers) {
                Player player = Bukkit.getPlayer(uuid);
                if (player != null) {
                    player.sendMessage(plugin.getConfigHandler().getMessageFromConfig("player-already-in-duel"));
                }
            }
            return;
        }

        Duel duel = new Duel(plugin, session);
        duel.init();
        activeDuels.add(duel);
    }

    public Duel getDuel(Player player) {
        for (Duel duel : activeDuels) {
            if (duel.getAlivePlayers().contains(player.getUniqueId())) {
                return duel;
            }
        }
        return null;
    }

    public Duel getSpectatingDuel(Player player) {
        for (Duel duel : activeDuels) {
            if (duel.getSpectators().contains(player.getUniqueId())) {
                return duel;
            }
        }
        return null;
    }

    public void deleteDuel(Duel duel) {
        activeDuels.remove(duel);
    }


}
