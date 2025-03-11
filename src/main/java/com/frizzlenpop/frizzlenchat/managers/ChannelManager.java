package com.frizzlenpop.frizzlenchat.managers;

import com.frizzlenpop.frizzlenchat.FrizzlenChat;
import org.bukkit.entity.Player;
import org.bukkit.configuration.file.YamlConfiguration;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;

public class ChannelManager {
    private final FrizzlenChat plugin;
    private final Map<UUID, String> playerChannels;
    private final Map<String, Set<UUID>> channelMembers;
    private final File channelFile;
    private final YamlConfiguration channelConfig;
    
    public ChannelManager(FrizzlenChat plugin) {
        this.plugin = plugin;
        this.playerChannels = new HashMap<>();
        this.channelMembers = new HashMap<>();
        this.channelFile = new File(plugin.getDataFolder(), "player-channels.yml");
        this.channelConfig = YamlConfiguration.loadConfiguration(channelFile);
        
        // Initialize default channels
        initializeChannels();
        loadPlayerChannels();
    }
    
    private void initializeChannels() {
        // Create default channel sets
        channelMembers.put("global", new HashSet<>());
        channelMembers.put("local", new HashSet<>());
        channelMembers.put("trade", new HashSet<>());
        channelMembers.put("staff", new HashSet<>());
    }
    
    public void loadPlayerChannels() {
        if (!channelFile.exists()) {
            return;
        }
        
        // Load player channels from file
        for (String uuidStr : channelConfig.getKeys(false)) {
            UUID uuid = UUID.fromString(uuidStr);
            String channel = channelConfig.getString(uuidStr);
            playerChannels.put(uuid, channel);
            
            // Add player to channel members
            channelMembers.get(channel).add(uuid);
        }
    }
    
    public void savePlayerChannels() {
        try {
            // Save all player channels
            for (Map.Entry<UUID, String> entry : playerChannels.entrySet()) {
                channelConfig.set(entry.getKey().toString(), entry.getValue());
            }
            
            // Save to file
            channelConfig.save(channelFile);
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "Could not save player channels", e);
        }
    }
    
    public boolean joinChannel(Player player, String channel) {
        // Check if channel exists
        if (!channelMembers.containsKey(channel)) {
            return false;
        }
        
        // Check permission
        if (!player.hasPermission(plugin.getConfigManager().getChannelPermission(channel))) {
            return false;
        }
        
        // Remove from current channel
        String currentChannel = playerChannels.get(player.getUniqueId());
        if (currentChannel != null) {
            channelMembers.get(currentChannel).remove(player.getUniqueId());
        }
        
        // Add to new channel
        playerChannels.put(player.getUniqueId(), channel);
        channelMembers.get(channel).add(player.getUniqueId());
        
        return true;
    }
    
    public boolean leaveChannel(Player player) {
        String channel = playerChannels.get(player.getUniqueId());
        if (channel == null) {
            return false;
        }
        
        // Remove from channel
        channelMembers.get(channel).remove(player.getUniqueId());
        playerChannels.remove(player.getUniqueId());
        
        // Join default channel
        String defaultChannel = plugin.getConfigManager().getDefaultChannel();
        return joinChannel(player, defaultChannel);
    }
    
    public String getPlayerChannel(Player player) {
        return playerChannels.get(player.getUniqueId());
    }
    
    public Set<UUID> getChannelMembers(String channel) {
        return channelMembers.getOrDefault(channel, new HashSet<>());
    }
    
    public boolean isInChannel(Player player, String channel) {
        String playerChannel = playerChannels.get(player.getUniqueId());
        return playerChannel != null && playerChannel.equals(channel);
    }
    
    public Set<String> getAvailableChannels(Player player) {
        Set<String> available = new HashSet<>();
        
        for (String channel : channelMembers.keySet()) {
            if (player.hasPermission(plugin.getConfigManager().getChannelPermission(channel))) {
                available.add(channel);
            }
        }
        
        return available;
    }
    
    public boolean channelExists(String channel) {
        return channelMembers.containsKey(channel);
    }
    
    public int getChannelRadius(String channel) {
        return plugin.getConfigManager().getChannelRadius(channel);
    }
    
    public String getChannelColor(String channel) {
        return plugin.getConfigManager().getChannelColor(channel);
    }
    
    public void handlePlayerQuit(Player player) {
        String channel = playerChannels.get(player.getUniqueId());
        if (channel != null) {
            channelMembers.get(channel).remove(player.getUniqueId());
            playerChannels.remove(player.getUniqueId());
        }
    }
    
    public void handlePlayerJoin(Player player) {
        // Join default channel
        String defaultChannel = plugin.getConfigManager().getDefaultChannel();
        joinChannel(player, defaultChannel);
    }
} 