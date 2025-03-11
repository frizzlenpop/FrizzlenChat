package com.frizzlenpop.frizzlenchat.commands;

import com.frizzlenpop.frizzlenchat.FrizzlenChat;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class BroadcastCommand implements CommandExecutor {
    private final FrizzlenChat plugin;

    public BroadcastCommand(FrizzlenChat plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("frizzlenchat.broadcast")) {
            sender.sendMessage(plugin.getConfigManager().getMessage("no-permission"));
            return true;
        }

        if (args.length < 1) {
            sender.sendMessage(plugin.getConfigManager().getMessage("invalid-usage"));
            return true;
        }

        // Build broadcast message
        StringBuilder message = new StringBuilder();
        for (String arg : args) {
            message.append(arg).append(" ");
        }

        // Format and send broadcast
        String broadcastFormat = plugin.getConfigManager().getMessage("broadcast-format")
            .replace("{message}", message.toString().trim());
        
        plugin.getServer().broadcastMessage(broadcastFormat);
        return true;
    }
} 