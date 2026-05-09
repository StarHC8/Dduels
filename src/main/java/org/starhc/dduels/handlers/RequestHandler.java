package org.starhc.dduels.handlers;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.starhc.dduels.Dduels;
import org.starhc.dduels.models.*;

import java.util.*;

public class RequestHandler {
    private final Dduels plugin;
    private final Map<UUID, List<Request>> pendingRequests = new HashMap<>();

    public RequestHandler(Dduels plugin) {
        this.plugin = plugin;
    }

    public void sendRequest(Player sender, Player receiver, DuelSession session) {

        Request request = new Request(sender.getUniqueId(), session);
        pendingRequests.get(receiver.getUniqueId()).add(request);

        String mapName = session.getSelectedMapTemplate().get().getTemplateDisplayName();
        String kitName = "[" + session.getSelectedKit().get().getSlot() + "]";

        sender.sendMessage(" ");
        sender.sendMessage(plugin.getConfigHandler().getMessageFromConfig("request.sent"));
        sender.sendMessage(plugin.getConfigHandler().getMessageFromConfig("request.to",
                        Placeholder.component("player",
                        Component.text(receiver.getName())))
                .clickEvent(ClickEvent.runCommand("/stats " + receiver.getName())));

        sender.sendMessage(plugin.getConfigHandler().getMessageFromConfig("request.map",
                Placeholder.component("map",
                Component.text(mapName))));

        sender.sendMessage(plugin.getConfigHandler().getMessageFromConfig("request.kit", Placeholder.component("kit", Component.text(kitName))));
        sender.sendMessage(" ");

        receiver.sendMessage(" ");
        receiver.sendMessage(plugin.getConfigHandler().getMessageFromConfig("request.received"));
        receiver.sendMessage(plugin.getConfigHandler().getMessageFromConfig("request.from", Placeholder.component("player", Component.text(sender.getName())))
                .clickEvent(ClickEvent.runCommand("/stats " + sender.getName())));

        receiver.sendMessage(plugin.getConfigHandler().getMessageFromConfig("request.map", Placeholder.component("map", Component.text(mapName))));

        receiver.sendMessage(plugin.getConfigHandler().getMessageFromConfig("request.kit", Placeholder.component("kit", Component.text(kitName))));

        receiver.sendMessage(plugin.getConfigHandler().getMessageFromConfig("request.accept")
                .clickEvent(ClickEvent.runCommand("/duelaccept " + sender.getName())));
        receiver.sendMessage(" ");


        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            if (pendingRequests.get(receiver.getUniqueId()).contains(request)) {
                pendingRequests.get(receiver.getUniqueId()).remove(request);
                sender.sendMessage(plugin.getConfigHandler().getMessageFromConfig("expired-request-to", Placeholder.component("player", Component.text(receiver.getName()))));
                receiver.sendMessage(plugin.getConfigHandler().getMessageFromConfig("expired-request-from", Placeholder.component("player", Component.text(sender.getName()))));
            }
        }, 20 * 60L);
    }

    public Request getRequest(Player receiver, Player target) {
        Request receivedRequest = null;
        for (Request request : pendingRequests.get(receiver.getUniqueId())) {
            if (request.getSender().equals(target.getUniqueId())) {
                receivedRequest = request;
            }
        }
        return receivedRequest;
    }

    public void acceptRequest(Player receiver, Player sender) {
        sender.sendMessage(plugin.getConfigHandler().getMessageFromConfig("accepted-request-from", Placeholder.component("player", Component.text(receiver.getName()))));
        receiver.sendMessage(plugin.getConfigHandler().getMessageFromConfig("accepted-request-to", Placeholder.component("player", Component.text(sender.getName()))));
        pendingRequests.get(receiver.getUniqueId()).remove(getRequest(receiver, sender));
    }

    public void addPlayerToRequestsList(Player player) {
        pendingRequests.put(player.getUniqueId(), new ArrayList<>());
    }

    public void removePlayerFromRequestsList(Player player) {
        pendingRequests.remove(player.getUniqueId());
    }
}
