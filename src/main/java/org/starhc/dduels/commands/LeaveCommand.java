package org.starhc.dduels.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.starhc.dduels.Dduels;
import org.starhc.dduels.models.Duel;

import java.util.List;

public class LeaveCommand implements CommandExecutor, TabCompleter {
    private Dduels plugin;

    public LeaveCommand(Dduels plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        Player player = (Player) sender;

        Duel duel = plugin.getDuelHandler().getDuel(player);
        if (duel == null || !duel.isActive()) {
            player.sendMessage(plugin.getConfigHandler().getMessageFromConfig("not-in-duel"));
            return true;
        }

        duel.leave(player);
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        return List.of();
    }
}
