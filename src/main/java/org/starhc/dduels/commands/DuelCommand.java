package org.starhc.dduels.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.starhc.dduels.Dduels;
import org.starhc.dduels.models.DuelSession;
import org.starhc.dduels.models.Kit;
import org.starhc.dduels.ui.DuelUi;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class DuelCommand implements CommandExecutor, TabCompleter {

    private Dduels plugin;
    public DuelCommand(Dduels plugin) { this.plugin = plugin; }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if (!(sender instanceof Player)) {
            sender.sendMessage(plugin.getConfigHandler().getMessageFromConfig("console-command"));
            return true;
        }

        Player player = (Player) sender;

        if (args.length == 0) {
            player.sendMessage(plugin.getConfigHandler().getMessageFromConfig("commands-correct-use.duel"));
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            player.sendMessage(plugin.getConfigHandler().getMessageFromConfig("player-not-found"));
            return true;
        }

        if (target.equals(player)) {
            player.sendMessage(plugin.getConfigHandler().getMessageFromConfig("self-duel"));
            return true;
        }

        if (plugin.getMapTemplateHandler().getMapTemplates().isEmpty()) {
            player.sendMessage(plugin.getConfigHandler().getMessageFromConfig("system-errors.no-map-templates"));
            return true;
        }

        DuelSession session = new DuelSession(player, List.of(target));

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
        List<String> results = new ArrayList<>();
        for (Player player : Bukkit.getOnlinePlayers()) {
            results.add(player.getName());
        }
        return results.stream().filter(s -> s.toLowerCase().startsWith(args[args.length - 1].toLowerCase())).collect(Collectors.toList());
    }
}
