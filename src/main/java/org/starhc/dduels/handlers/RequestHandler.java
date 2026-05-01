package org.starhc.dduels.handlers;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.starhc.dduels.Dduels;
import org.starhc.dduels.models.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class RequestHandler {
    private final Dduels plugin;
    private final Map<UUID, Request> pendingRequests = new HashMap<>();

    public RequestHandler(Dduels plugin) {
        this.plugin = plugin;
    }

    public void sendRequest(Player sender, Player receiver, DuelSession session) {

        pendingRequests.put(receiver.getUniqueId(), new Request(sender.getUniqueId(), session));
        sender.sendMessage(plugin.getConfigHandler().getMessageFromConfig("request-sent").replace("[player]", receiver.getName()));
        receiver.sendMessage(plugin.getConfigHandler().getMessageFromConfig("request-received").replace("[player]", sender.getName()));

        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            if (pendingRequests.containsKey(receiver.getUniqueId()) &&
                pendingRequests.get(receiver.getUniqueId()).getSender().equals(sender.getUniqueId())) {
                pendingRequests.remove(receiver.getUniqueId());
                sender.sendMessage(plugin.getConfigHandler().getMessageFromConfig("expired-request-to").replace("[player]", receiver.getName()));
                receiver.sendMessage(plugin.getConfigHandler().getMessageFromConfig("expired-request-from").replace("[player]", sender.getName()));
            }
        }, 20 * 60L);
    }

    public Request getRequest(Player receiver) {
        return pendingRequests.get(receiver.getUniqueId());
    }

    public void acceptRequest(Player receiver, Player sender) {
        sender.sendMessage(plugin.getConfigHandler().getMessageFromConfig("accepted-request-from").replace("[player]", receiver.getName()));
        receiver.sendMessage(plugin.getConfigHandler().getMessageFromConfig("accepted-request-to").replace("[player]", sender.getName()));
        pendingRequests.remove(receiver.getUniqueId());
    }
}
