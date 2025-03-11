package com.frizzlenpop.frizzlenchat;

import com.frizzlenpop.frizzlenchat.commands.ChannelCommand;
import com.frizzlenpop.frizzlenchat.commands.ClearChatCommand;
import com.frizzlenpop.frizzlenchat.listeners.ChatListener;
import com.frizzlenpop.frizzlenchat.listeners.PlayerListener;
import com.frizzlenpop.frizzlenchat.managers.ChatManager;
import com.frizzlenpop.frizzlenchat.managers.ChannelManager;
import com.frizzlenpop.frizzlenchat.managers.ConfigManager;
import com.frizzlenpop.frizzlenchat.utils.MessageUtils;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import java.util.logging.Logger;

public class FrizzlenChat extends JavaPlugin {
    
    private static FrizzlenChat instance;
    private ConfigManager configManager;
    private ChatManager chatManager;
    private ChannelManager channelManager;
    
    @Override
    public void onEnable() {
        // Set instance for static access
        instance = this;
        
        // Initialize logger
        Logger logger = getLogger();
        
        // Display startup message
        logger.info(ChatColor.GREEN + "FrizzlenChat v" + getDescription().getVersion() + " is starting up!");
        
        // Check for required dependencies
        if (!checkDependencies()) {
            logger.severe("Required dependencies not found! Disabling plugin...");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        
        // Load configuration
        saveDefaultConfig();
        configManager = new ConfigManager(this);
        
        // Initialize MessageUtils
        MessageUtils.initialize(this);
        
        // Initialize managers
        initializeManagers();
        
        // Register event listeners
        registerListeners();
        
        // Register commands
        registerCommands();
        
        logger.info(ChatColor.GREEN + "FrizzlenChat v" + getDescription().getVersion() + " has been enabled!");
    }

    @Override
    public void onDisable() {
        // Save data
        if (channelManager != null) {
            channelManager.savePlayerChannels();
        }
        
        getLogger().info(ChatColor.RED + "FrizzlenChat v" + getDescription().getVersion() + " has been disabled!");
        
        // Clear instance
        instance = null;
    }
    
    private boolean checkDependencies() {
        if (Bukkit.getPluginManager().getPlugin("FrizzlenEssentials") == null) {
            getLogger().severe("FrizzlenEssentials not found!");
            return false;
        }
        
        return true;
    }
    
    private void initializeManagers() {
        chatManager = new ChatManager(this);
        channelManager = new ChannelManager(this);
    }
    
    private void registerListeners() {
        getServer().getPluginManager().registerEvents(new ChatListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
    }
    
    private void registerCommands() {
        getCommand("channel").setExecutor(new ChannelCommand(this));
        getCommand("clearchat").setExecutor(new ClearChatCommand(this));
    }
    
    // Getter methods for managers
    public static FrizzlenChat getInstance() {
        return instance;
    }
    
    public ConfigManager getConfigManager() {
        return configManager;
    }
    
    public ChatManager getChatManager() {
        return chatManager;
    }
    
    public ChannelManager getChannelManager() {
        return channelManager;
    }
} 