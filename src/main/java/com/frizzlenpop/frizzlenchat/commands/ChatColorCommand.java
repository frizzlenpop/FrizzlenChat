package com.frizzlenpop.frizzlenchat.commands;

import com.frizzlenpop.frizzlenchat.FrizzlenChat;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ChatColorCommand implements CommandExecutor {
    private final FrizzlenChat plugin;

    public ChatColorCommand(FrizzlenChat plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(plugin.getConfigManager().getMessage("player-only"));
            return true;
        }

        Player player = (Player) sender;

        if (!player.hasPermission("frizzlenchat.chatcolor")) {
            player.sendMessage(plugin.getConfigManager().getMessage("no-permission"));
            return true;
        }

        if (args.length < 1) {
            // Remove chat color
            plugin.getChatManager().removePlayerColor(player);
            player.sendMessage(plugin.getConfigManager().getMessage("chatcolor-removed"));
            return true;
        }

        String colorName = args[0].toUpperCase();
        ChatColor color;

        try {
            color = ChatColor.valueOf(colorName);
        } catch (IllegalArgumentException e) {
            player.sendMessage(plugin.getConfigManager().getMessage("invalid-color"));
            return true;
        }

        // Check if player has permission for this color
        if (!player.hasPermission("frizzlenchat.chatcolor." + colorName.toLowerCase())) {
            player.sendMessage(plugin.getConfigManager().getMessage("no-permission"));
            return true;
        }

        // Set chat color
        plugin.getChatManager().setPlayerColor(player, color);
        player.sendMessage(plugin.getConfigManager().getMessage("chatcolor-set")
            .replace("{color}", color + colorName.toLowerCase()));

        return true;
    }
} 