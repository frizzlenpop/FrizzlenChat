package com.frizzlenpop.frizzlenchat.listeners;

import com.frizzlenpop.frizzlenchat.FrizzlenChat;
import com.frizzlenpop.frizzlenchat.commands.MuteCommand;
import com.frizzlenpop.frizzlenchat.commands.IgnoreCommand;
import com.frizzlenpop.frizzlenchat.commands.SlowChatCommand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class ChatListener implements Listener {
    private final FrizzlenChat plugin;
    private final MuteCommand muteCommand;
    private final IgnoreCommand ignoreCommand;
    private final SlowChatCommand slowChatCommand;

    public ChatListener(FrizzlenChat plugin, MuteCommand muteCommand, IgnoreCommand ignoreCommand, SlowChatCommand slowChatCommand) {
        this.plugin = plugin;
        this.muteCommand = muteCommand;
        this.ignoreCommand = ignoreCommand;
        this.slowChatCommand = slowChatCommand;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        if (event.isCancelled()) {
            return;
        }

        Player player = event.getPlayer();

        // Check if player is muted
        if (muteCommand.isMuted(player.getUniqueId())) {
            player.sendMessage(plugin.getConfigManager().getMuteMessage()
                .replace("{reason}", muteCommand.getMuteReason(player.getUniqueId())));
            event.setCancelled(true);
            return;
        }

        // Check slowmode
        int slowmode = slowChatCommand.getSlowmodeDuration();
        if (slowmode > 0 && !player.hasPermission("frizzlenchat.bypass.slowmode")) {
            if (!plugin.getChatManager().checkCooldown(player)) {
                event.setCancelled(true);
                return;
            }
        }

        // Check chat cooldown
        if (!plugin.getChatManager().checkCooldown(player)) {
            event.setCancelled(true);
            return;
        }

        String channel = plugin.getChannelManager().getPlayerChannel(player);

        // If player is not in a channel, put them in the default channel
        if (channel == null) {
            channel = plugin.getConfigManager().getDefaultChannel();
            plugin.getChannelManager().joinChannel(player, channel);
        }

        // Format the message
        String formattedMessage = plugin.getChatManager().formatMessage(player, event.getMessage(), channel);
        
        // Handle local chat
        int radius = plugin.getChannelManager().getChannelRadius(channel);
        if (radius > 0) {
            // Get players in range
            event.getRecipients().clear();
            event.getRecipients().addAll(
                plugin.getChatManager().getLocalRecipients(player, radius)
            );
        } else {
            // Global chat - only send to players in the same channel who haven't ignored the sender
            event.getRecipients().clear();
            for (Player recipient : plugin.getServer().getOnlinePlayers()) {
                if (plugin.getChannelManager().isInChannel(recipient, channel) &&
                    !ignoreCommand.isIgnored(recipient.getUniqueId(), player.getUniqueId())) {
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
        Player player = event.getPlayer();
        plugin.getChannelManager().handlePlayerQuit(player);
        plugin.getChatManager().removePlayerCooldown(player.getUniqueId());
        ignoreCommand.removeAllIgnores(player.getUniqueId());
    }
} 