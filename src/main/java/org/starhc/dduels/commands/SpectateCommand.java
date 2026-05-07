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
import org.starhc.dduels.models.Duel;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SpectateCommand implements CommandExecutor, TabCompleter {
    private Dduels plugin;

    public SpectateCommand(Dduels plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if (!(sender instanceof Player)) {
            sender.sendMessage(plugin.getConfigHandler().getMessageFromConfig("console-command"));
            return true;
        }


        Player player = (Player) sender;

        if (args.length == 0) {
            Duel playerDuel = plugin.getDuelHandler().getSpectatingDuel(player);
            if (playerDuel == null) {
                player.sendMessage(plugin.getConfigHandler().getMessageFromConfig("not-in-duel"));
                return false;
            }

            if (!playerDuel.getSpectators().contains(player)) {
                player.sendMessage(plugin.getConfigHandler().getMessageFromConfig("not-a-spectator"));
                return false;
            }

            playerDuel.stopSpectating(player);
            return true;
        }

        if (args.length == 1) {

            Player target = Bukkit.getPlayer(args[0]);
            if (target == null) {
                player.sendMessage(plugin.getConfigHandler().getMessageFromConfig("player-not-found"));
                return false;
            }

            if (target.equals(player)) {
                player.sendMessage(plugin.getConfigHandler().getMessageFromConfig("self-spectate"));
                return false;
            }

            Duel targetDuel = plugin.getDuelHandler().getDuel(target);
            if (targetDuel == null || !targetDuel.isActive()) {
                player.sendMessage(plugin.getConfigHandler().getMessageFromConfig("player-not-in-duel"));
                return false;
            }

            if (!targetDuel.getSpectators().contains(player)) {
                targetDuel.startSpectating(player, target);
            } else {
                player.teleport(target.getLocation());
            }
            return true;
        }

        player.sendMessage(plugin.getConfigHandler().getMessageFromConfig("commands-correct-use.spectate"));
        return false;
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
