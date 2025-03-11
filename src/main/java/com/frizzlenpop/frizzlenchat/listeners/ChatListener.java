package com.frizzlenpop.frizzlenchat.listeners;

import com.frizzlenpop.frizzlenchat.FrizzlenChat;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class ChatListener implements Listener {
    private final FrizzlenChat plugin;

    public ChatListener(FrizzlenChat plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        if (event.isCancelled()) {
            return;
        }

        Player player = event.getPlayer();
        String channel = plugin.getChannelManager().getPlayerChannel(player);

        // If player is not in a channel, put them in the default channel
        if (channel == null) {
            channel = plugin.getConfigManager().getDefaultChannel();
            plugin.getChannelManager().joinChannel(player, channel);
        }

        // Format the message
        String formattedMessage = plugin.getChatManager().formatMessage(player, event.getMessage(), channel);
        
        // Handle local chat
        if (plugin.getChannelManager().getChannelRadius(channel) > 0) {
            // Get players in range
            event.getRecipients().clear();
            event.getRecipients().addAll(
                plugin.getChatManager().getLocalRecipients(player, 
                    plugin.getChannelManager().getChannelRadius(channel))
            );
        } else {
            // Global chat - only send to players in the same channel
            event.getRecipients().clear();
            for (Player recipient : plugin.getServer().getOnlinePlayers()) {
                if (plugin.getChannelManager().isInChannel(recipient, channel)) {
                    event.getRecipients().add(recipient);
                }
            }
        }

        // Set the formatted message
        event.setFormat(formattedMessage);

        // Handle mentions
        plugin.getChatManager().handleMentions(event.getMessage(), event.getRecipients());
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        plugin.getChannelManager().handlePlayerJoin(event.getPlayer());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        plugin.getChannelManager().handlePlayerQuit(event.getPlayer());
    }
} 