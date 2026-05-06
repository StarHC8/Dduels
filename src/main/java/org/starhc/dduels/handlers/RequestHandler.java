package org.starhc.dduels.handlers;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
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

        String mapName = session.getSelectedMapTemplate().get().getTemplateDisplayName();
        String kitName = "[" + session.getSelectedKit().get().getSlot() + "]";

        sender.sendMessage(" ");
        sender.sendMessage(plugin.getConfigHandler().getMessageFromConfig("request.sent"));
        sender.sendMessage(Component.text(plugin.getConfigHandler().getMessageFromConfig("request.to").replace("[player]", receiver.getName()))
                .clickEvent(ClickEvent.runCommand("/stats " + receiver.getName())));

        sender.sendMessage(plugin.getConfigHandler().getMessageFromConfig("request.map")
                .replace("[map]", mapName));

        sender.sendMessage(plugin.getConfigHandler().getMessageFromConfig("request.kit")
                .replace("[kit]", kitName));
        sender.sendMessage(" ");

        receiver.sendMessage(" ");
        receiver.sendMessage(plugin.getConfigHandler().getMessageFromConfig("request.received"));
        receiver.sendMessage(Component.text(plugin.getConfigHandler().getMessageFromConfig("request.from").replace("[player]", sender.getName()))
                .clickEvent(ClickEvent.runCommand("/stats " + sender.getName())));

        receiver.sendMessage(plugin.getConfigHandler().getMessageFromConfig("request.map")
                .replace("[map]", mapName));

        receiver.sendMessage(plugin.getConfigHandler().getMessageFromConfig("request.kit")
                        .replace("[kit]", kitName));

        receiver.sendMessage(Component.text(plugin.getConfigHandler().getMessageFromConfig("request.accept"))
                .clickEvent(ClickEvent.runCommand("/duelaccept " + sender.getName())));
        receiver.sendMessage(" ");


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
