package org.starhc.dduels.commands;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.starhc.dduels.Dduels;
import org.starhc.dduels.models.PlayerStats;
import org.starhc.dduels.ui.StatsUi;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class StatsCommand implements CommandExecutor, TabCompleter {
    private Dduels plugin;

    public StatsCommand(Dduels plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(plugin.getConfigHandler().getMessageFromConfig("console-command"));
            return false;
        }

        Player player = (Player) sender;
        OfflinePlayer target;
        if (args.length == 0) {
            target = player;
        } else {
            target = Bukkit.getOfflinePlayer(args[0]);
        }


        CompletableFuture<PlayerStats> completableTargetStats = plugin.getStatsHandler().getPlayerStatsFromDB(target.getUniqueId());
        completableTargetStats.thenAccept(targetStats -> Bukkit.getScheduler().runTask(plugin, () -> {
            if (targetStats == null) {
                player.sendMessage(plugin.getConfigHandler().getMessageFromConfig("never-played"));
            } else {
                new StatsUi(plugin, targetStats).open(player);
            }

        }));
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
