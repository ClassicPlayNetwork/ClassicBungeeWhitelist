package net.classicplay.bungee.whitelist.utils;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@SuppressWarnings("deprecation")
public class MessageUtils {
    public static String colorize(String str){
        return ChatColor.translateAlternateColorCodes('&', str);
    }

    public static void send(CommandSender sender, String msg){
        sender.sendMessage(colorize(msg));
    }

    public static void sendPrefix(CommandSender sender, String msg){
        sender.sendMessage(colorize("&a&lSERVIDOR &8• "+msg));
    }

    public static List<String> filterSuggestions(List<String> suggestions, String input){
        Set<String> l = new HashSet<>();
        for (String suggestion : suggestions) {
            if(suggestion.toLowerCase().startsWith(input.toLowerCase())){
                l.add(suggestion);
            }
        }

        for (String suggestion : suggestions) {
            if(suggestion.toLowerCase().contains(input.toLowerCase())){
                l.add(suggestion);
            }
        }

        return l.stream().toList();
    }

    public static List<String> colorateList(List<String> list){
        List<String> newList = new ArrayList<>();
        for (String s : list) {
            newList.add(colorize(s));
        }
        return newList;
    }

    public static void kickPlayer(ProxiedPlayer player, List<String> lines) {
        player.disconnect(getKickMessage(lines));
    }

    public static String getKickMessage(List<String> lines){
        StringBuilder builder = new StringBuilder();

        builder.append(colorize("&d&lClassic&f&lPlay &8• &6Sistema de Mantenimiento\n\n"));

        for (String line : lines) {
            builder.append(line.replace("&", "§")).append("\n");
        }

        builder.append("\n§7Estate al tanto de las actualizaciones");
        builder.append("\n§7en nuestro discord: §dhttps://classicplay.net/discord");
        return builder.toString();
    }
}
