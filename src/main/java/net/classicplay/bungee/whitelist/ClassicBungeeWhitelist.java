package net.classicplay.bungee.whitelist;

import lombok.Getter;
import net.classicplay.bungee.whitelist.listeners.WhitelistListener;
import net.classicplay.bungee.whitelist.commands.WhitelistCommand;
import net.classicplay.bungee.whitelist.managers.WhitelistManager;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

@Getter
public final class ClassicBungeeWhitelist extends Plugin {
    private WhitelistManager whitelistManager;
    private Configuration config;

    @Override
    public void onEnable() {
        whitelistManager = new WhitelistManager();
        try {
            loadConfig();
        } catch (IOException e) {
            throw new RuntimeException("No se pudo cargar el archivo de configuración config.yml: ", e);
        }
        getProxy().getPluginManager().registerCommand(this, new WhitelistCommand("whitelist", "cpbungee.whitelist", this, whitelistManager, "listablanca", "wl"));
        getProxy().getPluginManager().registerListener(this, new WhitelistListener(whitelistManager));
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private void loadConfig() throws IOException {
        if(!getDataFolder().exists()) getDataFolder().mkdirs();
        File configFile = new File(getDataFolder(), "config.yml");
        if(!configFile.exists()){
            try (InputStream in = getResourceAsStream("config.yml")) {
                Files.copy(in, configFile.toPath());
                this.config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(configFile);
            } catch (IOException e) {
                throw new RuntimeException("No se pudo copiar el archivo predeterminado de config.yml: ", e);
            }
            return;
        }
        config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(new File(getDataFolder(), "config.yml"));
        whitelistManager.load(config, getLogger());
    }

    public void saveConfig() throws IOException {
        config.set("enable", whitelistManager.isEnabled());
        config.set("players", whitelistManager.getNames());
        File configFile = new File(getDataFolder(), "config.yml");
        if(!configFile.exists()) configFile.createNewFile();
        ConfigurationProvider.getProvider(YamlConfiguration.class).save(config, configFile);
    }

    @Override
    public void onDisable() {

    }
}
