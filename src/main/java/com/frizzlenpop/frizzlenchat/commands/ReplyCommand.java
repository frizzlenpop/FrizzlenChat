package com.frizzlenpop.frizzlenchat.commands;

import com.frizzlenpop.frizzlenchat.FrizzlenChat;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import java.util.UUID;

public class ReplyCommand implements CommandExecutor {
    private final FrizzlenChat plugin;
    private final MessageCommand messageCommand;

    public ReplyCommand(FrizzlenChat plugin, MessageCommand messageCommand) {
        this.plugin = plugin;
        this.messageCommand = messageCommand;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(plugin.getConfigManager().getMessage("player-only"));
            return true;
        }

        Player player = (Player) sender;

        if (args.length < 1) {
            player.sendMessage(plugin.getConfigManager().getMessage("invalid-usage"));
            return true;
        }

        UUID lastSenderId = messageCommand.getLastMessageSender(player.getUniqueId());
        if (lastSenderId == null) {
            player.sendMessage(plugin.getConfigManager().getMessage("no-reply-target"));
            return true;
        }

        Player target = plugin.getServer().getPlayer(lastSenderId);
        if (target == null) {
            player.sendMessage(plugin.getConfigManager().getMessage("player-not-found"));
            return true;
        }

        // Build message from args
        StringBuilder message = new StringBuilder();
        for (String arg : args) {
            message.append(arg).append(" ");
        }

        // Send private message
        messageCommand.sendPrivateMessage(player, target, message.toString().trim());
        return true;
    }
} 