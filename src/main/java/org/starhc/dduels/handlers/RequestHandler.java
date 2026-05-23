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
    private final Map<UUID, List<DuelSession>> pendingRequests = new HashMap<>();

    public RequestHandler(Dduels plugin) {
        this.plugin = plugin;
    }

    public void sendRequest(Player sender, Player receiver, DuelSession session) {

        List<DuelSession> receiverRequests = pendingRequests.get(receiver.getUniqueId());
        receiverRequests.add(session);

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
            List<DuelSession> currentRequests = pendingRequests.get(receiver.getUniqueId());
            if (currentRequests != null && currentRequests.contains(session)) {
                currentRequests.remove(session);
                sender.sendMessage(plugin.getConfigHandler().getMessageFromConfig("expired-request-to", Placeholder.component("player", Component.text(receiver.getName()))));
                receiver.sendMessage(plugin.getConfigHandler().getMessageFromConfig("expired-request-from", Placeholder.component("player", Component.text(sender.getName()))));
            }
        }, 20 * 60L);
    }

    public DuelSession getRequest(Player receiver, Player target) {
        List<DuelSession> receiverRequests = pendingRequests.get(receiver.getUniqueId());
        if (receiverRequests == null) return null;

        for (DuelSession session : receiverRequests) {
            if (session.getSender().equals(target.getUniqueId())) {
                return session;
            }
        }
        return null;
    }

    public void acceptRequest(Player receiver, Player sender) {
        sender.sendMessage(plugin.getConfigHandler().getMessageFromConfig("accepted-request-from", Placeholder.component("player", Component.text(receiver.getName()))));
        receiver.sendMessage(plugin.getConfigHandler().getMessageFromConfig("accepted-request-to", Placeholder.component("player", Component.text(sender.getName()))));
        
        List<DuelSession> receiverRequests = pendingRequests.get(receiver.getUniqueId());
        if (receiverRequests != null) {
            receiverRequests.remove(getRequest(receiver, sender));
        }
    }

    public void addPlayerToRequestsList(Player player) {
        pendingRequests.put(player.getUniqueId(), new ArrayList<>());
    }

    public void removePlayerFromRequestsList(Player player) {
        pendingRequests.remove(player.getUniqueId());
    }
}
