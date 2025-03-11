package com.frizzlenpop.frizzlenchat.commands;

import com.frizzlenpop.frizzlenchat.FrizzlenChat;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ChannelCommand implements CommandExecutor {
    private final FrizzlenChat plugin;

    public ChannelCommand(FrizzlenChat plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This command can only be used by players!");
            return true;
        }

        Player player = (Player) sender;

        if (args.length == 0) {
            // Show current channel
            String currentChannel = plugin.getChannelManager().getPlayerChannel(player);
            if (currentChannel == null) {
                currentChannel = plugin.getConfigManager().getDefaultChannel();
            }
            player.sendMessage(ChatColor.GREEN + "You are currently in the " + 
                plugin.getChannelManager().getChannelColor(currentChannel) + currentChannel + 
                ChatColor.GREEN + " channel.");
            return true;
        }

        String subCommand = args[0].toLowerCase();
        switch (subCommand) {
            case "join":
                if (args.length < 2) {
                    player.sendMessage(ChatColor.RED + "Usage: /channel join <channel>");
                    return true;
                }
                String targetChannel = args[1].toLowerCase();
                if (!plugin.getChannelManager().channelExists(targetChannel)) {
                    player.sendMessage(ChatColor.RED + "That channel does not exist!");
                    return true;
                }
                if (!player.hasPermission("frizzlenchat.channel." + targetChannel)) {
                    player.sendMessage(ChatColor.RED + "You don't have permission to join that channel!");
                    return true;
                }
                if (plugin.getChannelManager().joinChannel(player, targetChannel)) {
                    player.sendMessage(ChatColor.GREEN + "You have joined the " + 
                        plugin.getChannelManager().getChannelColor(targetChannel) + targetChannel + 
                        ChatColor.GREEN + " channel.");
                } else {
                    player.sendMessage(ChatColor.RED + "Failed to join channel!");
                }
                break;

            case "leave":
                if (plugin.getChannelManager().leaveChannel(player)) {
                    String defaultChannel = plugin.getConfigManager().getDefaultChannel();
                    player.sendMessage(ChatColor.GREEN + "You have left your current channel and joined " + 
                        plugin.getChannelManager().getChannelColor(defaultChannel) + defaultChannel + 
                        ChatColor.GREEN + ".");
                } else {
                    player.sendMessage(ChatColor.RED + "You are not in any channel!");
                }
                break;

            case "list":
                player.sendMessage(ChatColor.GREEN + "Available channels:");
                for (String channel : plugin.getChannelManager().getAvailableChannels(player)) {
                    String currentChannel = plugin.getChannelManager().getPlayerChannel(player);
                    boolean isCurrentChannel = channel.equals(currentChannel);
                    player.sendMessage(plugin.getChannelManager().getChannelColor(channel) + channel + 
                        (isCurrentChannel ? ChatColor.YELLOW + " (Current)" : ""));
                }
                break;

            default:
                player.sendMessage(ChatColor.RED + "Unknown sub-command. Use /channel <join|leave|list>");
                break;
        }

        return true;
    }
} 