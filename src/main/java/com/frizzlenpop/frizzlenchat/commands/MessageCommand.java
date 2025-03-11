package com.frizzlenpop.frizzlenchat.commands;

import com.frizzlenpop.frizzlenchat.FrizzlenChat;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MessageCommand implements CommandExecutor {
    private final FrizzlenChat plugin;
    private final Map<UUID, UUID> lastMessageSender;

    public MessageCommand(FrizzlenChat plugin) {
        this.plugin = plugin;
        this.lastMessageSender = new HashMap<>();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(plugin.getConfigManager().getMessage("player-only"));
            return true;
        }

        Player player = (Player) sender;

        if (args.length < 2) {
            player.sendMessage(plugin.getConfigManager().getMessage("invalid-usage"));
            return true;
        }

        Player target = plugin.getServer().getPlayer(args[0]);
        if (target == null) {
            player.sendMessage(plugin.getConfigManager().getMessage("player-not-found"));
            return true;
        }

        // Build message from remaining args
        StringBuilder message = new StringBuilder();
        for (int i = 1; i < args.length; i++) {
            message.append(args[i]).append(" ");
        }

        // Send private message
        sendPrivateMessage(player, target, message.toString().trim());
        return true;
    }

    public void sendPrivateMessage(Player sender, Player recipient, String message) {
        // Format messages
        String toFormat = plugin.getConfigManager().getPrivateMessageSentFormat()
            .replace("{player}", recipient.getName())
            .replace("{message}", message);
        
        String fromFormat = plugin.getConfigManager().getPrivateMessageReceivedFormat()
            .replace("{player}", sender.getName())
            .replace("{message}", message);

        // Send messages
        sender.sendMessage(plugin.getChatManager().formatChatColors(sender, toFormat));
        recipient.sendMessage(plugin.getChatManager().formatChatColors(recipient, fromFormat));

        // Play sound to recipient
        plugin.getChatManager().playMessageSound(recipient, "private");

        // Store last message sender for reply command
        lastMessageSender.put(recipient.getUniqueId(), sender.getUniqueId());
    }

    public UUID getLastMessageSender(UUID playerId) {
        return lastMessageSender.get(playerId);
    }

    public void removeLastMessageSender(UUID playerId) {
        lastMessageSender.remove(playerId);
    }
} 