package net.classicplay.bungee.whitelist.commands;

import net.classicplay.bungee.whitelist.ClassicBungeeWhitelist;
import net.classicplay.bungee.whitelist.managers.WhitelistManager;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;

import java.io.IOException;
import java.util.List;

import static net.classicplay.bungee.whitelist.utils.MessageUtils.*;

public class WhitelistCommand extends Command implements TabExecutor {
    private final ClassicBungeeWhitelist plugin;
    private final WhitelistManager manager;
    public WhitelistCommand(String name, String permission, ClassicBungeeWhitelist plugin, WhitelistManager manager, String... aliases) {
        super(name, permission, aliases);
        this.plugin = plugin;
        this.manager = manager;
    }

    @Override
    public void execute(CommandSender commandSender, String[] args) {
        if(args.length == 0){
            sendPrefix(commandSender, "&cUsa: /wl on|off|toggle|add|remove|list [player]");
            return;
        }
        switch (args[0].toLowerCase()){
            case "on": {
                on(commandSender, args);
                break;
            }
            case "toggle": {
                toggle(commandSender, args);
                break;
            }
            case "off": {
                off(commandSender);
                break;
            }
            case "add": {
                add(commandSender, args);
                break;
            }
            case "remove": {
                remove(commandSender, args);
                break;
            }
            case "list": {
                list(commandSender);
                break;
            }
            default: {
                sendPrefix(commandSender, "&cUsa: /wl on|off|toggle|add|remove|list [player] [--kick]");
                break;
            }
        };
    }

    private void on(CommandSender commandSender, String[] args) {
        if(manager.isEnabled()){
            sendPrefix(commandSender, "&c¡La lista blanca ya está activada!");
            return;
        }

        manager.setEnabled(true);
        try {
            plugin.saveConfig();
        } catch (IOException e) {
            sendPrefix(commandSender, "&7La lista blanca ha sido activada. &cPero ocurrio un error que no permitio guardar la configuración, revisa la consola");
            plugin.getLogger().severe("Ocurrio un erro al guardar el archivo config.yml: "+ e.getMessage());
            if(args.length >= 2){
                if(args[1].equals("--kick"))
                    for (ProxiedPlayer player : ProxyServer.getInstance().getPlayers()) {
                        if(manager.getNames().contains(player.getName())) return;
                        kickPlayer(player, List.of("&fHas sido expulsado del servidor por los", "&fadministradores para realizar mantenimiento"));
                    }
            }
            return;
        }
        sendPrefix(commandSender, "&7La lista blanca ha sido activada con éxito.");

        if(args.length >= 2){
            if(args[1].equals("--kick"))
                for (ProxiedPlayer player : ProxyServer.getInstance().getPlayers()) {
                    if(manager.getNames().contains(player.getName())) return;
                    kickPlayer(player, List.of("&fHas sido expulsado del servidor por los", "&fadministradores para realizar mantenimiento"));
                }
        }
    }

    private void off(CommandSender commandSender) {
        if(!manager.isEnabled()){
            sendPrefix(commandSender, "&c¡La lista blanca no está activada!");
            return;
        }

        manager.setEnabled(false);
        try {
            plugin.saveConfig();
        } catch (IOException e) {
            sendPrefix(commandSender, "&7La lista blanca ha sido desactivada. &cPero ocurrio un error que no permitio guardar la configuración, revisa la consola");
            plugin.getLogger().severe("Ocurrio un erro al guardar el archivo config.yml: "+ e.getMessage());
            return;
        }
        sendPrefix(commandSender, "&7La lista blanca ha sido desactivada con éxito.");
    }

    private void toggle(CommandSender commandSender, String[] args) {
        if(manager.isEnabled())
            off(commandSender);
        else
            on(commandSender, args);
    }

    private void add(CommandSender commandSender, String[] args) {
        if(args.length < 2){
            sendPrefix(commandSender, "&cUsa: /wl on|off|toggle|add|remove|list [player] [--kick]");
            return;
        }
        String name = args[1];
        if(manager.getNames().contains(name)){
            sendPrefix(commandSender, "&cEse jugador ya está en la lista blanca.");
            return;
        }

        manager.getNames().add(name);
        try {
            plugin.saveConfig();
        } catch (IOException e) {
            sendPrefix(commandSender, "&7El jugador fue añadido. &cPero ocurrio un error que no permitio guardar la configuración, revisa la consola");
            plugin.getLogger().severe("Ocurrio un erro al guardar el archivo config.yml: "+ e.getMessage());
            return;
        }

        sendPrefix(commandSender, "&7Añadiste a &a"+name+" &7a la lista blanca.");
    }

    private void remove(CommandSender commandSender, String[] args) {
        if(args.length < 2){
            sendPrefix(commandSender, "&cUsa: /wl on|off|toggle|add|remove|list [player] [--kick]");
            return;
        }
        String name = args[1];
        if(!manager.getNames().contains(name)){
            sendPrefix(commandSender, "&cEse jugador no está en la lista blanca.");
            return;
        }

        manager.getNames().remove(name);
        try {
            plugin.saveConfig();
        } catch (IOException e) {
            sendPrefix(commandSender, "&7El jugador fue removido. &cPero ocurrio un error que no permitio guardar la configuración, revisa la consola");
            plugin.getLogger().severe("Ocurrio un erro al guardar el archivo config.yml: "+ e.getMessage());
            return;
        }
        sendPrefix(commandSender, "&7Removiste a &a"+name+" &7a de la lista blanca.");
    }

    private void list(CommandSender commandSender) {
        if(manager.getNames().isEmpty()){
            sendPrefix(commandSender, "&cNo hay ningún jugador en la lista blanca.");
            return;
        }
        send(commandSender, "&a&lJugadores en la lista blanca:");
        for (String name : manager.getNames()) {
            send(commandSender, " &8- &f"+name);
        }
    }

    @Override
    public Iterable<String> onTabComplete(CommandSender commandSender, String[] args) {
        if(!commandSender.hasPermission("cpbungee.whitelist")) return List.of();

        if(args.length == 1) return filterSuggestions(List.of("on", "off", "toggle", "add", "remove", "list"), args[0]);
        if(args.length == 2) {
            if(args[1].equalsIgnoreCase("add")){
                return filterSuggestions(ProxyServer.getInstance().getPlayers().stream().map(ProxiedPlayer::getName).filter(name -> !manager.getNames().contains(name)).toList(), args[1]);
            }
            else if(args[1].equalsIgnoreCase("remove")){
                return filterSuggestions(ProxyServer.getInstance().getPlayers().stream().map(ProxiedPlayer::getName).filter(name -> manager.getNames().contains(name)).toList(), args[1]);
            }
            else if(args[1].equalsIgnoreCase("on") || args[1].equalsIgnoreCase("toggle")){
                return filterSuggestions(List.of("--kick"), args[1]);
            }
        }

        return List.of();
    }
}
