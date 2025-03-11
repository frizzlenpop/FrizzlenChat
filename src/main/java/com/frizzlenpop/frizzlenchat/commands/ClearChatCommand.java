package com.frizzlenpop.frizzlenchat.commands;

import com.frizzlenpop.frizzlenchat.FrizzlenChat;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ClearChatCommand implements CommandExecutor {
    private final FrizzlenChat plugin;

    public ClearChatCommand(FrizzlenChat plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This command can only be used by players!");
            return true;
        }

        Player player = (Player) sender;

        if (args.length > 0 && args[0].equalsIgnoreCase("all")) {
            if (!player.hasPermission("frizzlenchat.command.clearchat.all")) {
                player.sendMessage(ChatColor.RED + "You don't have permission to clear chat for all players!");
                return true;
            }

            plugin.getChatManager().clearChatForAll();
            plugin.getServer().broadcastMessage(ChatColor.GREEN + "Chat has been cleared by " + player.getName());
        } else {
            plugin.getChatManager().clearChat(player);
            player.sendMessage(ChatColor.GREEN + "Your chat has been cleared.");
        }

        return true;
    }
} 