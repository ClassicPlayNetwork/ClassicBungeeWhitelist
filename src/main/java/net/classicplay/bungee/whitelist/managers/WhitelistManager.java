package net.classicplay.bungee.whitelist.managers;

import lombok.Getter;
import lombok.Setter;
import net.md_5.bungee.config.Configuration;

import java.io.ObjectInputFilter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

@Getter @Setter
public class WhitelistManager {
    private final List<String> names = new ArrayList<>();
    private boolean enabled = false;

    public void load(Configuration config, Logger logger){
        names.clear();
        names.addAll(config.getStringList("players"));
        enabled = config.getBoolean("enable", false);
        logger.fine("Se cargaron "+names.size()+" jugador(es) en la lista blanca.");
    }

}
