package com.frizzlenpop.frizzlenchat.commands;

import com.frizzlenpop.frizzlenchat.FrizzlenChat;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import java.util.*;

public class MuteCommand implements CommandExecutor {
    private final FrizzlenChat plugin;
    private final Map<UUID, Long> mutedPlayers;
    private final Map<UUID, String> muteReasons;

    public MuteCommand(FrizzlenChat plugin) {
        this.plugin = plugin;
        this.mutedPlayers = new HashMap<>();
        this.muteReasons = new HashMap<>();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("frizzlenchat.mute")) {
            sender.sendMessage(plugin.getConfigManager().getMessage("no-permission"));
            return true;
        }

        if (args.length < 1) {
            sender.sendMessage(plugin.getConfigManager().getMessage("invalid-usage"));
            return true;
        }

        String subCommand = args[0].toLowerCase();
        switch (subCommand) {
            case "add":
                if (args.length < 2) {
                    sender.sendMessage(plugin.getConfigManager().getMessage("invalid-usage"));
                    return true;
                }
                handleMute(sender, args);
                break;

            case "remove":
                if (args.length < 2) {
                    sender.sendMessage(plugin.getConfigManager().getMessage("invalid-usage"));
                    return true;
                }
                handleUnmute(sender, args[1]);
                break;

            case "list":
                handleMuteList(sender);
                break;

            default:
                // Default to muting player
                handleMute(sender, args);
                break;
        }

        return true;
    }

    private void handleMute(CommandSender sender, String[] args) {
        Player target = plugin.getServer().getPlayer(args[0]);
        if (target == null) {
            sender.sendMessage(plugin.getConfigManager().getMessage("player-not-found"));
            return;
        }

        if (target.hasPermission("frizzlenchat.bypass.mute")) {
            sender.sendMessage(plugin.getConfigManager().getMessage("cannot-mute-player"));
            return;
        }

        // Parse duration (in minutes)
        int duration = plugin.getConfigManager().getDefaultMuteDuration();
        if (args.length > 1) {
            try {
                duration = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                // Use default duration if not a valid number
            }
        }

        // Build reason
        StringBuilder reason = new StringBuilder();
        if (args.length > 2) {
            for (int i = 2; i < args.length; i++) {
                reason.append(args[i]).append(" ");
            }
        } else {
            reason.append("No reason specified");
        }

        // Set mute
        long muteEndTime = System.currentTimeMillis() + (duration * 60 * 1000L);
        mutedPlayers.put(target.getUniqueId(), muteEndTime);
        muteReasons.put(target.getUniqueId(), reason.toString().trim());

        // Notify player and staff
        String muteMessage = plugin.getConfigManager().getMessage("player-muted")
            .replace("{player}", target.getName())
            .replace("{duration}", String.valueOf(duration))
            .replace("{reason}", reason.toString().trim());
        
        target.sendMessage(plugin.getConfigManager().getMessage("you-are-muted")
            .replace("{duration}", String.valueOf(duration))
            .replace("{reason}", reason.toString().trim()));

        sender.sendMessage(muteMessage);

        // Broadcast to staff if enabled
        if (plugin.getConfigManager().isMuteBroadcastEnabled()) {
            for (Player staff : plugin.getServer().getOnlinePlayers()) {
                if (staff.hasPermission("frizzlenchat.mute") && !staff.equals(sender)) {
                    staff.sendMessage(muteMessage);
                }
            }
        }
    }

    private void handleUnmute(CommandSender sender, String targetName) {
        Player target = plugin.getServer().getPlayer(targetName);
        if (target == null) {
            sender.sendMessage(plugin.getConfigManager().getMessage("player-not-found"));
            return;
        }

        if (mutedPlayers.remove(target.getUniqueId()) != null) {
            muteReasons.remove(target.getUniqueId());
            
            String unmutedMessage = plugin.getConfigManager().getMessage("player-unmuted")
                .replace("{player}", target.getName());
            
            sender.sendMessage(unmutedMessage);
            target.sendMessage(plugin.getConfigManager().getMessage("you-are-unmuted"));

            // Broadcast to staff if enabled
            if (plugin.getConfigManager().isMuteBroadcastEnabled()) {
                for (Player staff : plugin.getServer().getOnlinePlayers()) {
                    if (staff.hasPermission("frizzlenchat.mute") && !staff.equals(sender)) {
                        staff.sendMessage(unmutedMessage);
                    }
                }
            }
        } else {
            sender.sendMessage(plugin.getConfigManager().getMessage("player-not-muted")
                .replace("{player}", target.getName()));
        }
    }

    private void handleMuteList(CommandSender sender) {
        if (mutedPlayers.isEmpty()) {
            sender.sendMessage(plugin.getConfigManager().getMessage("mute-list-empty"));
            return;
        }

        sender.sendMessage(plugin.getConfigManager().getMessage("mute-list-header"));
        long currentTime = System.currentTimeMillis();

        for (Map.Entry<UUID, Long> entry : mutedPlayers.entrySet()) {
            Player mutedPlayer = plugin.getServer().getPlayer(entry.getKey());
            if (mutedPlayer != null) {
                long remainingTime = (entry.getValue() - currentTime) / 1000 / 60; // Convert to minutes
                String reason = muteReasons.get(entry.getKey());
                
                sender.sendMessage(plugin.getConfigManager().getMessage("mute-list-format")
                    .replace("{player}", mutedPlayer.getName())
                    .replace("{duration}", String.valueOf(remainingTime))
                    .replace("{reason}", reason));
            }
        }
    }

    public boolean isMuted(UUID playerId) {
        Long muteEndTime = mutedPlayers.get(playerId);
        if (muteEndTime == null) {
            return false;
        }

        if (System.currentTimeMillis() >= muteEndTime) {
            // Mute has expired
            mutedPlayers.remove(playerId);
            muteReasons.remove(playerId);
            return false;
        }

        return true;
    }

    public String getMuteReason(UUID playerId) {
        return muteReasons.get(playerId);
    }

    public void removeAllMutes() {
        mutedPlayers.clear();
        muteReasons.clear();
    }
} 