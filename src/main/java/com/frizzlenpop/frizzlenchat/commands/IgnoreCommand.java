package com.frizzlenpop.frizzlenchat.commands;

import com.frizzlenpop.frizzlenchat.FrizzlenChat;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import java.util.*;

public class IgnoreCommand implements CommandExecutor {
    private final FrizzlenChat plugin;
    private final Map<UUID, Set<UUID>> ignoredPlayers;

    public IgnoreCommand(FrizzlenChat plugin) {
        this.plugin = plugin;
        this.ignoredPlayers = new HashMap<>();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(plugin.getConfigManager().getMessage("player-only"));
            return true;
        }

        Player player = (Player) sender;

        if (args.length < 1) {
            player.sendMessage(plugin.getConfigManager().getMessage("invalid-usage"));
            return true;
        }

        String subCommand = args[0].toLowerCase();
        switch (subCommand) {
            case "add":
                if (args.length < 2) {
                    player.sendMessage(plugin.getConfigManager().getMessage("invalid-usage"));
                    return true;
                }
                handleIgnore(player, args[1]);
                break;

            case "remove":
                if (args.length < 2) {
                    player.sendMessage(plugin.getConfigManager().getMessage("invalid-usage"));
                    return true;
                }
                handleUnignore(player, args[1]);
                break;

            case "list":
                handleIgnoreList(player);
                break;

            default:
                // Default to adding player to ignore list
                handleIgnore(player, args[0]);
                break;
        }

        return true;
    }

    private void handleIgnore(Player player, String targetName) {
        Player target = plugin.getServer().getPlayer(targetName);
        if (target == null) {
            player.sendMessage(plugin.getConfigManager().getMessage("player-not-found"));
            return;
        }

        if (target.equals(player)) {
            player.sendMessage(plugin.getConfigManager().getMessage("cannot-ignore-self"));
            return;
        }

        if (target.hasPermission("frizzlenchat.bypass.ignore")) {
            player.sendMessage(plugin.getConfigManager().getMessage("cannot-ignore-player"));
            return;
        }

        Set<UUID> ignored = ignoredPlayers.computeIfAbsent(player.getUniqueId(), k -> new HashSet<>());
        if (ignored.add(target.getUniqueId())) {
            player.sendMessage(plugin.getConfigManager().getMessage("player-ignored")
                .replace("{player}", target.getName()));
        } else {
            player.sendMessage(plugin.getConfigManager().getMessage("already-ignored")
                .replace("{player}", target.getName()));
        }
    }

    private void handleUnignore(Player player, String targetName) {
        Player target = plugin.getServer().getPlayer(targetName);
        if (target == null) {
            player.sendMessage(plugin.getConfigManager().getMessage("player-not-found"));
            return;
        }

        Set<UUID> ignored = ignoredPlayers.get(player.getUniqueId());
        if (ignored != null && ignored.remove(target.getUniqueId())) {
            player.sendMessage(plugin.getConfigManager().getMessage("player-unignored")
                .replace("{player}", target.getName()));
        } else {
            player.sendMessage(plugin.getConfigManager().getMessage("not-ignored")
                .replace("{player}", target.getName()));
        }
    }

    private void handleIgnoreList(Player player) {
        Set<UUID> ignored = ignoredPlayers.get(player.getUniqueId());
        if (ignored == null || ignored.isEmpty()) {
            player.sendMessage(plugin.getConfigManager().getMessage("ignore-list-empty"));
            return;
        }

        player.sendMessage(plugin.getConfigManager().getMessage("ignore-list-header"));
        for (UUID ignoredId : ignored) {
            Player ignoredPlayer = plugin.getServer().getPlayer(ignoredId);
            if (ignoredPlayer != null) {
                player.sendMessage(plugin.getConfigManager().getMessage("ignore-list-format")
                    .replace("{player}", ignoredPlayer.getName()));
            }
        }
    }

    public boolean isIgnored(UUID playerId, UUID targetId) {
        Set<UUID> ignored = ignoredPlayers.get(playerId);
        return ignored != null && ignored.contains(targetId);
    }

    public void removeAllIgnores(UUID playerId) {
        ignoredPlayers.remove(playerId);
    }
} 