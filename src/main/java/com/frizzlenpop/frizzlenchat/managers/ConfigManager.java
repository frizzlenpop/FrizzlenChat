package com.frizzlenpop.frizzlenchat.managers;

import com.frizzlenpop.frizzlenchat.FrizzlenChat;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import java.util.HashMap;
import java.util.Map;

public class ConfigManager {
    private final FrizzlenChat plugin;
    private final Map<String, String> channelPermissions;
    private final Map<String, Integer> channelRadii;
    private final Map<String, String> channelColors;
    
    public ConfigManager(FrizzlenChat plugin) {
        this.plugin = plugin;
        this.channelPermissions = new HashMap<>();
        this.channelRadii = new HashMap<>();
        this.channelColors = new HashMap<>();
        loadConfig();
    }
    
    public void loadConfig() {
        plugin.saveDefaultConfig();
        plugin.reloadConfig();
        FileConfiguration config = plugin.getConfig();
        
        // Load channel configurations
        for (String channel : config.getConfigurationSection("channels").getKeys(false)) {
            String path = "channels." + channel;
            channelPermissions.put(channel, config.getString(path + ".permission", "frizzlenchat.channel." + channel));
            channelRadii.put(channel, config.getInt(path + ".radius", -1));
            channelColors.put(channel, config.getString(path + ".color", "&f"));
        }
    }
    
    public String getDefaultChannel() {
        return plugin.getConfig().getString("default-channel", "global");
    }
    
    public String getChannelPermission(String channel) {
        return channelPermissions.getOrDefault(channel, "frizzlenchat.channel." + channel);
    }
    
    public int getChannelRadius(String channel) {
        return channelRadii.getOrDefault(channel, -1);
    }
    
    public String getChannelColor(String channel) {
        String color = channelColors.getOrDefault(channel, "&f");
        return ChatColor.translateAlternateColorCodes('&', color);
    }
    
    public boolean isLocalChatEnabled() {
        return plugin.getConfig().getBoolean("enable-local-chat", true);
    }
    
    public int getDefaultLocalChatRadius() {
        return plugin.getConfig().getInt("default-local-chat-radius", 100);
    }
    
    public boolean isChatSoundEnabled() {
        return plugin.getConfig().getBoolean("enable-chat-sound", true);
    }
    
    public String getChatSoundName() {
        return plugin.getConfig().getString("chat-sound", "ENTITY_EXPERIENCE_ORB_PICKUP");
    }
    
    public float getChatSoundVolume() {
        return (float) plugin.getConfig().getDouble("chat-sound-volume", 0.5);
    }
    
    public float getChatSoundPitch() {
        return (float) plugin.getConfig().getDouble("chat-sound-pitch", 1.0);
    }
    
    public String getChatFormat(String channel) {
        return plugin.getConfig().getString("chat-formats." + channel, 
            "&7[{channel}] {prefix}{player}{suffix}&7: &f{message}");
    }
    
    public boolean isAntiSpamEnabled() {
        return plugin.getConfig().getBoolean("enable-anti-spam", true);
    }
    
    public int getCooldownSeconds() {
        return plugin.getConfig().getInt("chat-cooldown-seconds", 3);
    }
    
    public boolean isMentionsEnabled() {
        return plugin.getConfig().getBoolean("enable-mentions", true);
    }
    
    public String getMentionFormat() {
        return plugin.getConfig().getString("mention-format", "&e@{player}&f");
    }
} 