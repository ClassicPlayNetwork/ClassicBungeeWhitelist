package net.classicplay.bungee.whitelist.listeners;

import net.classicplay.bungee.whitelist.managers.WhitelistManager;
import net.md_5.bungee.api.event.PreLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.List;

import static net.classicplay.bungee.whitelist.utils.MessageUtils.getKickMessage;

public class WhitelistListener implements Listener {

    private final WhitelistManager manager;

    public WhitelistListener(WhitelistManager manager) {
        this.manager = manager;
    }

    @SuppressWarnings("deprecation")
    @EventHandler(priority = 1)
    public void onPreLogin(PreLoginEvent e){
        if(manager.isEnabled() && !manager.getNames().contains(e.getConnection().getName())){
            e.setCancelled(true);
            e.setCancelReason(getKickMessage(List.of("&cEl servidor se encuentra en mantenimiento")));
        }
    }
}
