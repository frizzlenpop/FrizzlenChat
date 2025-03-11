package com.frizzlenpop.frizzlenchat.utils;

import com.frizzlenpop.frizzlenchat.FrizzlenChat;
import org.bukkit.ChatColor;

public class MessageUtils {
    private static FrizzlenChat plugin;

    public static void initialize(FrizzlenChat instance) {
        plugin = instance;
    }

    public static String colorize(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public static String stripColor(String message) {
        return ChatColor.stripColor(message);
    }

    public static String formatMessage(String format, String... replacements) {
        if (replacements.length % 2 != 0) {
            throw new IllegalArgumentException("Replacements must be in pairs!");
        }

        String result = format;
        for (int i = 0; i < replacements.length; i += 2) {
            result = result.replace(replacements[i], replacements[i + 1]);
        }

        return colorize(result);
    }
} 