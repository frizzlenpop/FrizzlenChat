package com.frizzlenpop.frizzlenchat.listeners;

import com.frizzlenpop.frizzlenchat.FrizzlenChat;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerListener implements Listener {
    private final FrizzlenChat plugin;

    public PlayerListener(FrizzlenChat plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        // Add player to default channel
        plugin.getChannelManager().handlePlayerJoin(event.getPlayer());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        // Clean up player data
        plugin.getChannelManager().handlePlayerQuit(event.getPlayer());
    }
} 