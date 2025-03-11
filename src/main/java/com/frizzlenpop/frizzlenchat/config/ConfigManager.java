package com.frizzlenpop.frizzlenchat.config;

import com.frizzlenpop.frizzlenchat.FrizzlenChat;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

public class ConfigManager {
    private final FrizzlenChat plugin;
    private FileConfiguration config;
    private File configFile;
    
    public ConfigManager(FrizzlenChat plugin) {
        this.plugin = plugin;
        loadConfigs();
    }
    
    public void loadConfigs() {
        // Create config folder if it doesn't exist
        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdirs();
        }
        
        // Load main config
        configFile = new File(plugin.getDataFolder(), "config.yml");
        if (!configFile.exists()) {
            plugin.saveResource("config.yml", false);
        }
        config = YamlConfiguration.loadConfiguration(configFile);
        
        // Load or create data files
        createDataFile("muted-players.yml");
        createDataFile("player-channels.yml");
        createDataFile("ignored-players.yml");
    }
    
    private void createDataFile(String fileName) {
        File file = new File(plugin.getDataFolder(), fileName);
        if (!file.exists()) {
            try {
                file.createNewFile();
                YamlConfiguration.loadConfiguration(file).save(file);
            } catch (IOException e) {
                plugin.getLogger().log(Level.SEVERE, "Could not create " + fileName, e);
            }
        }
    }
    
    public void saveConfigs() {
        try {
            config.save(configFile);
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "Could not save config.yml", e);
        }
    }
    
    public FileConfiguration getConfig() {
        return config;
    }
    
    public void reloadConfig() {
        config = YamlConfiguration.loadConfiguration(configFile);
    }
    
    public File getConfigFile() {
        return configFile;
    }
    
    // Configuration getters
    public String getChatFormat() {
        return config.getString("chat.format", "{PREFIX}{PLAYER}{SUFFIX}&8: &f{MESSAGE}");
    }
    
    public int getLocalChatRadius() {
        return config.getInt("chat.local-chat-radius", 0);
    }
    
    public int getDefaultCooldown() {
        return config.getInt("chat.default-cooldown", 1);
    }
    
    public int getMaxMessageLength() {
        return config.getInt("chat.max-message-length", 256);
    }
    
    public boolean areChannelsEnabled() {
        return config.getBoolean("channels.enabled", true);
    }
    
    public String getDefaultChannel() {
        return config.getString("channels.default", "global");
    }
    
    public String getChannelFormat(String channel) {
        return config.getString("channels.list." + channel + ".format", getChatFormat());
    }
    
    public String getChannelPermission(String channel) {
        return config.getString("channels.list." + channel + ".permission", "frizzlenchat.channel." + channel);
    }
    
    public int getChannelRadius(String channel) {
        return config.getInt("channels.list." + channel + ".radius", 0);
    }
    
    public String getChannelColor(String channel) {
        return config.getString("channels.list." + channel + ".color", "&7");
    }
    
    public String getPrivateMessageSentFormat() {
        return config.getString("private-messages.format.sent", "&7[&cTo &7{PLAYER}&7] &f{MESSAGE}");
    }
    
    public String getPrivateMessageReceivedFormat() {
        return config.getString("private-messages.format.received", "&7[&cFrom &7{PLAYER}&7] &f{MESSAGE}");
    }
    
    public boolean isPrivateMessageSoundEnabled() {
        return config.getBoolean("private-messages.sound.enabled", true);
    }
    
    public String getPrivateMessageSound() {
        return config.getString("private-messages.sound.name", "BLOCK_NOTE_BLOCK_PLING");
    }
    
    public float getPrivateMessageSoundVolume() {
        return (float) config.getDouble("private-messages.sound.volume", 1.0);
    }
    
    public float getPrivateMessageSoundPitch() {
        return (float) config.getDouble("private-messages.sound.pitch", 1.0);
    }
    
    public boolean isMuteBroadcastEnabled() {
        return config.getBoolean("mute.broadcast-to-staff", true);
    }
    
    public int getDefaultMuteDuration() {
        return config.getInt("mute.default-duration", 60);
    }
    
    public String getMuteMessage() {
        return config.getString("mute.message", "&cYou are muted. Reason: {REASON}");
    }
    
    public boolean isChatFilterEnabled() {
        return config.getBoolean("filter.enabled", true);
    }
    
    public String getFilterReplacement() {
        return config.getString("filter.replacement", "****");
    }
    
    public boolean isFilterNotifyStaffEnabled() {
        return config.getBoolean("filter.notify-staff", true);
    }
    
    public String getFilterAction() {
        return config.getString("filter.action", "REPLACE");
    }
    
    public String getStaffChatFormat() {
        return config.getString("staff-chat.format", "&c[Staff] {PREFIX}{PLAYER}{SUFFIX}&8: &f{MESSAGE}");
    }
    
    public boolean isStaffChatSoundEnabled() {
        return config.getBoolean("staff-chat.sound.enabled", true);
    }
    
    public String getStaffChatSound() {
        return config.getString("staff-chat.sound.name", "BLOCK_NOTE_BLOCK_BELL");
    }
    
    public float getStaffChatSoundVolume() {
        return (float) config.getDouble("staff-chat.sound.volume", 1.0);
    }
    
    public float getStaffChatSoundPitch() {
        return (float) config.getDouble("staff-chat.sound.pitch", 1.0);
    }
    
    public int getClearChatLines() {
        return config.getInt("clear-chat.lines", 100);
    }
    
    public String getClearChatMessage() {
        return config.getString("clear-chat.message", "&aChat has been cleared by {PLAYER}");
    }
    
    public String getSlowModeEnabledMessage() {
        return config.getString("slow-mode.enabled-message", "&cSlow mode is now enabled. You can chat every {SECONDS} seconds.");
    }
    
    public String getSlowModeDisabledMessage() {
        return config.getString("slow-mode.disabled-message", "&aSlow mode has been disabled.");
    }
    
    public String getSlowModeCooldownMessage() {
        return config.getString("slow-mode.cooldown-message", "&cYou must wait {REMAINING} seconds before chatting again.");
    }
    
    public boolean areMentionsEnabled() {
        return config.getBoolean("mentions.enabled", true);
    }
    
    public boolean isMentionSoundEnabled() {
        return config.getBoolean("mentions.sound.enabled", true);
    }
    
    public String getMentionSound() {
        return config.getString("mentions.sound.name", "ENTITY_EXPERIENCE_ORB_PICKUP");
    }
    
    public float getMentionSoundVolume() {
        return (float) config.getDouble("mentions.sound.volume", 1.0);
    }
    
    public float getMentionSoundPitch() {
        return (float) config.getDouble("mentions.sound.pitch", 1.0);
    }
    
    public String getMentionHighlightColor() {
        return config.getString("mentions.highlight-color", "&e");
    }
    
    public String getMentionSymbol() {
        return config.getString("mentions.symbol", "@");
    }
    
    public boolean isDebugEnabled() {
        return config.getBoolean("debug.enabled", false);
    }
    
    public String getDebugLogLevel() {
        return config.getString("debug.log-level", "INFO");
    }
} 