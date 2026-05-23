package org.starhc.dduels.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.starhc.dduels.Dduels;
import org.starhc.dduels.enums.DuelType;
import org.starhc.dduels.models.DuelSession;
import org.starhc.dduels.models.Kit;
import org.starhc.dduels.ui.DuelUi;
import org.starhc.partyManager.models.Party;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class PartyDuelCommand implements CommandExecutor, TabCompleter {
    private Dduels plugin;

    public PartyDuelCommand(Dduels plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        Player player = (Player) sender;

        if (plugin.getPartyManager() == null) {
            player.sendMessage(plugin.getConfigHandler().getMessageFromConfig("party.party-not-available"));
            return true;
        }

        Party party = plugin.getPartyHandler().getParty(player);

        if (party == null) {
            player.sendMessage(plugin.getPartyManager().getConfigHandler().getMessageFromConfig("not-in-party"));
            return true;
        }

        if (!party.isLeader(player.getUniqueId())) {
            player.sendMessage(plugin.getPartyManager().getConfigHandler().getMessageFromConfig("only-leader"));
            return true;
        }

        if (party.getMembers().size() < 2) {
            player.sendMessage(plugin.getConfigHandler().getMessageFromConfig("party.not-enough-players"));
            return true;
        }

        if (plugin.getMapTemplateHandler().getMapTemplates().isEmpty()) {
            player.sendMessage(plugin.getConfigHandler().getMessageFromConfig("system-errors.no-map-templates"));
            return true;
        }


        List<UUID> duelPlayers = new ArrayList<>(party.getMembers());

        DuelSession session = new DuelSession(player.getUniqueId(), duelPlayers);

        session.setDuelType(DuelType.FFA);

        session.setSelectedMapTemplate(plugin.getMapTemplateHandler().getMapTemplates().getFirst());

        List<Kit> playerKits = plugin.getKitHandler().getKits(player.getUniqueId());
        if (!playerKits.isEmpty()) {
            session.setSelectedKit(playerKits.getFirst());
        }

        new DuelUi(plugin, session).open(player);
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        return List.of();
    }
}
