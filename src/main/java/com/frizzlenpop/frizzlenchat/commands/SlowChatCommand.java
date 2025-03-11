package com.frizzlenpop.frizzlenchat.commands;

import com.frizzlenpop.frizzlenchat.FrizzlenChat;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class SlowChatCommand implements CommandExecutor {
    private final FrizzlenChat plugin;
    private int slowmodeDuration = 0;

    public SlowChatCommand(FrizzlenChat plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("frizzlenchat.slowchat")) {
            sender.sendMessage(plugin.getConfigManager().getMessage("no-permission"));
            return true;
        }

        if (args.length < 1) {
            sender.sendMessage(plugin.getConfigManager().getMessage("invalid-usage"));
            return true;
        }

        if (args[0].equalsIgnoreCase("off")) {
            // Disable slowmode
            slowmodeDuration = 0;
            plugin.getServer().broadcastMessage(plugin.getConfigManager().getMessage("slowmode-disabled"));
            return true;
        }

        try {
            int seconds = Integer.parseInt(args[0]);
            if (seconds < 0) {
                sender.sendMessage(plugin.getConfigManager().getMessage("invalid-duration"));
                return true;
            }

            slowmodeDuration = seconds;
            plugin.getServer().broadcastMessage(plugin.getConfigManager().getMessage("slowmode-enabled")
                .replace("{duration}", String.valueOf(seconds)));

        } catch (NumberFormatException e) {
            sender.sendMessage(plugin.getConfigManager().getMessage("invalid-duration"));
        }

        return true;
    }

    public int getSlowmodeDuration() {
        return slowmodeDuration;
    }
} 