package com.frizzlenpop.frizzlenchat.managers;

import com.frizzlenpop.frizzlenchat.FrizzlenChat;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import com.frizzlenpop.frizzlenchat.utils.MessageUtils;

public class ConfigManager {
    private final FrizzlenChat plugin;
    private FileConfiguration config;

    public ConfigManager(FrizzlenChat plugin) {
        this.plugin = plugin;
        loadConfig();
    }

    public void loadConfig() {
        plugin.saveDefaultConfig();
        plugin.reloadConfig();
        this.config = plugin.getConfig();
    }

    // Channel settings
    public String getDefaultChannel() {
        return config.getString("default-channel", "global");
    }

    public String getChannelPermission(String channel) {
        return config.getString("channels." + channel + ".permission", "frizzlenchat.channel." + channel);
    }

    public String getChannelColor(String channel) {
        return config.getString("channels." + channel + ".color", "&f");
    }

    public int getChannelRadius(String channel) {
        return config.getInt("channels." + channel + ".radius", -1);
    }

    public String getChannelFormat(String channel) {
        return config.getString("channels." + channel + ".format", 
            "&7[{channel}] {prefix}{player}{suffix}&7: &f{message}");
    }

    // Local chat settings
    public int getLocalChatRadius() {
        return config.getInt("chat-features.local-chat.default-radius", 100);
    }

    // Private message settings
    public boolean isPrivateMessageSoundEnabled() {
        return config.getBoolean("private-messages.sound.enabled", true);
    }

    public String getPrivateMessageSound() {
        return config.getString("private-messages.sound.name", "ENTITY_EXPERIENCE_ORB_PICKUP");
    }

    public float getPrivateMessageSoundVolume() {
        return (float) config.getDouble("private-messages.sound.volume", 0.5);
    }

    public float getPrivateMessageSoundPitch() {
        return (float) config.getDouble("private-messages.sound.pitch", 1.0);
    }

    // Staff chat settings
    public boolean isStaffChatSoundEnabled() {
        return config.getBoolean("chat-features.staff-chat.sound.enabled", true);
    }

    public String getStaffChatSound() {
        return config.getString("chat-features.staff-chat.sound.name", "ENTITY_EXPERIENCE_ORB_PICKUP");
    }

    public float getStaffChatSoundVolume() {
        return (float) config.getDouble("chat-features.staff-chat.sound.volume", 0.5);
    }

    public float getStaffChatSoundPitch() {
        return (float) config.getDouble("chat-features.staff-chat.sound.pitch", 1.0);
    }

    // Chat features
    public boolean isLocalChatEnabled() {
        return config.getBoolean("chat-features.local-chat.enabled", true);
    }

    public int getDefaultLocalChatRadius() {
        return config.getInt("chat-features.local-chat.default-radius", 100);
    }

    public boolean isMentionsEnabled() {
        return config.getBoolean("chat-features.mentions.enabled", true);
    }

    public String getMentionFormat() {
        return config.getString("chat-features.mentions.format", "&e@{player}&f");
    }

    public boolean isMentionSoundEnabled() {
        return config.getBoolean("chat-features.mentions.sound.enabled", true);
    }

    public String getMentionSoundName() {
        return config.getString("chat-features.mentions.sound.name", "ENTITY_EXPERIENCE_ORB_PICKUP");
    }

    public float getMentionSoundVolume() {
        return (float) config.getDouble("chat-features.mentions.sound.volume", 0.5);
    }

    public float getMentionSoundPitch() {
        return (float) config.getDouble("chat-features.mentions.sound.pitch", 1.0);
    }

    // Anti-spam settings
    public boolean isAntiSpamEnabled() {
        return config.getBoolean("chat-features.anti-spam.enabled", true);
    }

    public int getChatCooldown() {
        return config.getInt("chat-features.anti-spam.cooldown", 3);
    }

    public String getCooldownMessage() {
        return config.getString("chat-features.anti-spam.message", 
            "&cYou must wait {time} seconds before chatting again.");
    }

    // Clear chat settings
    public int getClearChatLines() {
        return config.getInt("chat-features.clear-chat.lines", 100);
    }

    public String getClearChatMessage() {
        return config.getString("chat-features.clear-chat.message", 
            "&aChat has been cleared by {player}");
    }

    // Message formats
    public String getMessage(String path) {
        String message = config.getString("formats." + path);
        return message != null ? MessageUtils.colorize(message) : "";
    }

    // Debug settings
    public boolean isDebugEnabled() {
        return config.getBoolean("debug.enabled", false);
    }

    public String getLogLevel() {
        return config.getString("debug.log-level", "INFO");
    }
} 