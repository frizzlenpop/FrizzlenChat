package com.frizzlenpop.frizzlenchat.managers;

import com.frizzlenpop.frizzlenchat.FrizzlenChat;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.Location;
import java.util.HashMap;
import java.util.Map;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class ChatManager {
    private final FrizzlenChat plugin;
    private final Map<UUID, String> playerFormats;
    private final Map<UUID, ChatColor> playerColors;
    private final Pattern colorPattern;
    private final Pattern mentionPattern;
    
    public ChatManager(FrizzlenChat plugin) {
        this.plugin = plugin;
        this.playerFormats = new HashMap<>();
        this.playerColors = new HashMap<>();
        this.colorPattern = Pattern.compile("(?i)&[0-9A-FK-OR]");
        this.mentionPattern = Pattern.compile("@(\\w+)");
    }
    
    public String formatMessage(Player player, String message, String channel) {
        String format = "&7[{channel}] {prefix}{player}{suffix}&7: &f{message}";
        
        // Replace placeholders
        format = format.replace("{channel}", plugin.getChannelManager().getChannelColor(channel) + channel);
        format = format.replace("{player}", player.getName());
        format = format.replace("{message}", message);
        
        // Add prefix and suffix from FrizzlenPerms if available
        try {
            Class<?> permsClass = Class.forName("org.frizzlenpop.frizzlenperms.FrizzlenPerms");
            format = format.replace("{prefix}", getPrefix(player));
            format = format.replace("{suffix}", getSuffix(player));
        } catch (ClassNotFoundException e) {
            format = format.replace("{prefix}", "");
            format = format.replace("{suffix}", "");
        }
        
        // Translate color codes
        return ChatColor.translateAlternateColorCodes('&', format);
    }
    
    public String formatChatColors(Player player, String message) {
        // Check if player has permission to use colors
        if (!player.hasPermission("frizzlenchat.color.*")) {
            return stripColors(message);
        }
        
        // Replace color codes
        message = ChatColor.translateAlternateColorCodes('&', message);
        
        // Apply player's custom color if set
        ChatColor playerColor = playerColors.get(player.getUniqueId());
        if (playerColor != null) {
            message = playerColor + message;
        }
        
        return message;
    }
    
    public String stripColors(String message) {
        return colorPattern.matcher(message).replaceAll("");
    }
    
    public void setPlayerFormat(Player player, String format) {
        playerFormats.put(player.getUniqueId(), format);
    }
    
    public void removePlayerFormat(Player player) {
        playerFormats.remove(player.getUniqueId());
    }
    
    public String getPlayerFormat(Player player) {
        return playerFormats.get(player.getUniqueId());
    }
    
    public void setPlayerColor(Player player, ChatColor color) {
        playerColors.put(player.getUniqueId(), color);
    }
    
    public void removePlayerColor(Player player) {
        playerColors.remove(player.getUniqueId());
    }
    
    public ChatColor getPlayerColor(Player player) {
        return playerColors.get(player.getUniqueId());
    }
    
    public boolean isInRange(Player sender, Player receiver) {
        // If local chat is disabled, always return true
        int radius = plugin.getConfigManager().getLocalChatRadius();
        if (radius <= 0) {
            return true;
        }
        
        // Check if players are in the same world
        if (!sender.getWorld().equals(receiver.getWorld())) {
            return false;
        }
        
        // Check distance between players
        Location senderLoc = sender.getLocation();
        Location receiverLoc = receiver.getLocation();
        return senderLoc.distance(receiverLoc) <= radius;
    }
    
    public void playMessageSound(Player player, String soundType) {
        Sound sound;
        float volume;
        float pitch;
        
        switch (soundType.toLowerCase()) {
            case "private":
                if (!plugin.getConfigManager().isPrivateMessageSoundEnabled()) return;
                sound = Sound.valueOf(plugin.getConfigManager().getPrivateMessageSound());
                volume = plugin.getConfigManager().getPrivateMessageSoundVolume();
                pitch = plugin.getConfigManager().getPrivateMessageSoundPitch();
                break;
            case "mention":
                if (!plugin.getConfigManager().isMentionSoundEnabled()) return;
                sound = Sound.valueOf(plugin.getConfigManager().getMentionSound());
                volume = plugin.getConfigManager().getMentionSoundVolume();
                pitch = plugin.getConfigManager().getMentionSoundPitch();
                break;
            case "staff":
                if (!plugin.getConfigManager().isStaffChatSoundEnabled()) return;
                sound = Sound.valueOf(plugin.getConfigManager().getStaffChatSound());
                volume = plugin.getConfigManager().getStaffChatSoundVolume();
                pitch = plugin.getConfigManager().getStaffChatSoundPitch();
                break;
            default:
                return;
        }
        
        player.playSound(player.getLocation(), sound, volume, pitch);
    }
    
    public void clearChat(Player player) {
        for (int i = 0; i < 100; i++) {
            player.sendMessage("");
        }
    }

    public void clearChatForAll() {
        for (Player player : plugin.getServer().getOnlinePlayers()) {
            clearChat(player);
        }
    }

    public Set<Player> getLocalRecipients(Player sender, int radius) {
        Set<Player> recipients = new HashSet<>();
        Location senderLoc = sender.getLocation();
        
        for (Player player : plugin.getServer().getOnlinePlayers()) {
            if (player.getWorld().equals(sender.getWorld()) &&
                player.getLocation().distance(senderLoc) <= radius) {
                recipients.add(player);
            }
        }
        
        return recipients;
    }

    public void handleMentions(String message, Set<Player> recipients) {
        Matcher matcher = mentionPattern.matcher(message);
        while (matcher.find()) {
            String playerName = matcher.group(1);
            Player mentioned = plugin.getServer().getPlayer(playerName);
            
            if (mentioned != null && recipients.contains(mentioned)) {
                playMentionSound(mentioned);
            }
        }
    }

    private void playMentionSound(Player player) {
        try {
            Sound sound = Sound.valueOf("ENTITY_EXPERIENCE_ORB_PICKUP");
            player.playSound(player.getLocation(), 
                sound,
                0.5f,  // Default volume
                1.0f); // Default pitch
        } catch (IllegalArgumentException e) {
            plugin.getLogger().warning("Invalid sound name: ENTITY_EXPERIENCE_ORB_PICKUP");
        }
    }

    private String getPrefix(Player player) {
        // This would integrate with FrizzlenPerms
        return ""; // Placeholder until FrizzlenPerms integration is implemented
    }

    private String getSuffix(Player player) {
        // This would integrate with FrizzlenPerms
        return ""; // Placeholder until FrizzlenPerms integration is implemented
    }
} 