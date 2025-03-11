package com.frizzlenpop.frizzlenchat.managers;

import com.frizzlenpop.frizzlenchat.FrizzlenChat;
import com.frizzlenpop.frizzlenchat.utils.MessageUtils;
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
    private final Map<UUID, Long> lastMessageTime;
    
    public ChatManager(FrizzlenChat plugin) {
        this.plugin = plugin;
        this.playerFormats = new HashMap<>();
        this.playerColors = new HashMap<>();
        this.colorPattern = Pattern.compile("(?i)&[0-9A-FK-OR]");
        this.mentionPattern = Pattern.compile("@(\\w+)");
        this.lastMessageTime = new HashMap<>();
    }
    
    public String formatMessage(Player player, String message, String channel) {
        String format = plugin.getConfigManager().getChannelFormat(channel);
        
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
        
        return MessageUtils.colorize(format);
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
        if (!plugin.getConfigManager().isLocalChatEnabled()) {
            return true;
        }
        
        // Check if players are in the same world
        if (!sender.getWorld().equals(receiver.getWorld())) {
            return false;
        }
        
        // Check distance between players
        Location senderLoc = sender.getLocation();
        Location receiverLoc = receiver.getLocation();
        return senderLoc.distance(receiverLoc) <= plugin.getConfigManager().getLocalChatRadius();
    }
    
    public void playMessageSound(Player player, String soundType) {
        try {
            String soundName;
            float volume;
            float pitch;

            switch (soundType.toLowerCase()) {
                case "private":
                    if (!plugin.getConfigManager().isPrivateMessageSoundEnabled()) return;
                    soundName = plugin.getConfigManager().getPrivateMessageSound();
                    volume = plugin.getConfigManager().getPrivateMessageSoundVolume();
                    pitch = plugin.getConfigManager().getPrivateMessageSoundPitch();
                    break;
                case "mention":
                    if (!plugin.getConfigManager().isMentionSoundEnabled()) return;
                    soundName = plugin.getConfigManager().getMentionSoundName();
                    volume = plugin.getConfigManager().getMentionSoundVolume();
                    pitch = plugin.getConfigManager().getMentionSoundPitch();
                    break;
                case "staff":
                    if (!plugin.getConfigManager().isStaffChatSoundEnabled()) return;
                    soundName = plugin.getConfigManager().getStaffChatSound();
                    volume = plugin.getConfigManager().getStaffChatSoundVolume();
                    pitch = plugin.getConfigManager().getStaffChatSoundPitch();
                    break;
                default:
                    return;
            }

            Sound sound = Sound.valueOf(soundName);
            player.playSound(player.getLocation(), sound, volume, pitch);
        } catch (IllegalArgumentException e) {
            plugin.getLogger().warning("Invalid sound name for " + soundType + " chat sound");
        }
    }
    
    public void clearChat(Player player) {
        int lines = plugin.getConfigManager().getClearChatLines();
        for (int i = 0; i < lines; i++) {
            player.sendMessage("");
        }
    }

    public void clearChatForAll() {
        int lines = plugin.getConfigManager().getClearChatLines();
        for (Player player : plugin.getServer().getOnlinePlayers()) {
            for (int i = 0; i < lines; i++) {
                player.sendMessage("");
            }
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
        if (!plugin.getConfigManager().isMentionsEnabled()) {
            return;
        }

        Matcher matcher = mentionPattern.matcher(message);
        while (matcher.find()) {
            String playerName = matcher.group(1);
            Player mentioned = plugin.getServer().getPlayer(playerName);
            
            if (mentioned != null && recipients.contains(mentioned)) {
                playMessageSound(mentioned, "mention");
            }
        }
    }

    private String getPrefix(Player player) {
        try {
            Class<?> permsClass = Class.forName("org.frizzlenpop.frizzlenperms.FrizzlenPerms");
            Object permsInstance = permsClass.getMethod("getInstance").invoke(null);
            Object userManager = permsClass.getMethod("getUserManager").invoke(permsInstance);
            Object user = userManager.getClass().getMethod("getUser", UUID.class).invoke(userManager, player.getUniqueId());
            
            if (user != null) {
                String prefix = (String) user.getClass().getMethod("getPrefix").invoke(user);
                return prefix != null ? prefix : "";
            }
        } catch (Exception e) {
            if (plugin.getConfigManager().isDebugEnabled()) {
                plugin.getLogger().warning("Failed to get prefix from FrizzlenPerms: " + e.getMessage());
            }
        }
        return "";
    }

    private String getSuffix(Player player) {
        try {
            Class<?> permsClass = Class.forName("org.frizzlenpop.frizzlenperms.FrizzlenPerms");
            Object permsInstance = permsClass.getMethod("getInstance").invoke(null);
            Object userManager = permsClass.getMethod("getUserManager").invoke(permsInstance);
            Object user = userManager.getClass().getMethod("getUser", UUID.class).invoke(userManager, player.getUniqueId());
            
            if (user != null) {
                String suffix = (String) user.getClass().getMethod("getSuffix").invoke(user);
                return suffix != null ? suffix : "";
            }
        } catch (Exception e) {
            if (plugin.getConfigManager().isDebugEnabled()) {
                plugin.getLogger().warning("Failed to get suffix from FrizzlenPerms: " + e.getMessage());
            }
        }
        return "";
    }

    public boolean checkCooldown(Player player) {
        if (!plugin.getConfigManager().isAntiSpamEnabled() || 
            player.hasPermission("frizzlenchat.bypass.cooldown")) {
            return true;
        }

        long currentTime = System.currentTimeMillis();
        long lastTime = lastMessageTime.getOrDefault(player.getUniqueId(), 0L);
        int cooldown = plugin.getConfigManager().getChatCooldown() * 1000; // Convert to milliseconds

        if (currentTime - lastTime < cooldown) {
            int remainingSeconds = (int) ((cooldown - (currentTime - lastTime)) / 1000) + 1;
            String message = plugin.getConfigManager().getCooldownMessage()
                .replace("{time}", String.valueOf(remainingSeconds));
            player.sendMessage(MessageUtils.colorize(message));
            return false;
        }

        lastMessageTime.put(player.getUniqueId(), currentTime);
        return true;
    }

    public void removePlayerCooldown(UUID playerId) {
        lastMessageTime.remove(playerId);
    }
} 