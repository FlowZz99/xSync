package it.flowzz.xsync;

import it.flowzz.xsync.api.DefaultImplementation;
import it.flowzz.xsync.database.IDatabase;
import it.flowzz.xsync.database.credentials.Credentials;
import it.flowzz.xsync.database.impl.MongoDB;
import it.flowzz.xsync.database.impl.MySQL;
import it.flowzz.xsync.handlers.CommunicationHandler;
import it.flowzz.xsync.handlers.MessageHandler;
import it.flowzz.xsync.handlers.ZoneHandler;
import it.flowzz.xsync.listeners.PlayerListener;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

@Getter
public final class SyncPlugin extends JavaPlugin {

    private CommunicationHandler communicationHandler;
    private MessageHandler messageHandler;
    private ZoneHandler zoneHandler;
    private IDatabase database;

    private FileConfiguration lang;

    @Override
    public void onEnable() {
        setupConfigs();
        setupDatabase();
        setupHandlers();
        setupPluginMessage();
        registerListeners(
                new PlayerListener(this)
        );
        DefaultImplementation.setImplementation(new DefaultImplementation(this));
    }

    private void setupConfigs() {
        //Load default config
        saveDefaultConfig();
        //Load lang file
        File langFile = new File(getDataFolder(), "lang.yml");
        if (!langFile.exists()) {
            langFile.getParentFile().mkdirs();
            saveResource("lang.yml", false);
        }
        lang = new YamlConfiguration();
        try {
            lang.load(langFile);
        } catch (IOException | InvalidConfigurationException exception) {
            exception.printStackTrace();
        }
    }

    private void setupDatabase() {
        switch (getConfig().getString("database.type", "MySQL").toUpperCase()) {
            case "MONGODB" -> database = new MongoDB(this);
            case "MYSQL" -> database = new MySQL(this);
        }
        database.connect(Credentials.builder()
                .hostname(getConfig().getString("database.hostname"))
                .database(getConfig().getString("database.database"))
                .username(getConfig().getString("database.username"))
                .password(getConfig().getString("database.password"))
                .port(getConfig().getInt("database.port"))
                .build()
        );
    }

    private void setupHandlers() {
        communicationHandler = new CommunicationHandler(this);
        messageHandler = new MessageHandler(this);
        zoneHandler = new ZoneHandler(this);
    }


    private void setupPluginMessage() {
        getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
    }

    private void registerListeners(Listener... listeners) {
        PluginManager pluginManager = Bukkit.getPluginManager();
        for (Listener listener : listeners) {
            pluginManager.registerEvents(listener, this);
        }
    }

    @Override
    public void onDisable() {
        //Save Players data before shutting down
        Bukkit.getOnlinePlayers().forEach(onlinePlayer -> database.savePlayer(onlinePlayer, false));
        //Unregister PluginMessenger in case of reload
        getServer().getMessenger().unregisterOutgoingPluginChannel(this);
        //Shutdown all
        try {
            zoneHandler.shutdown();
            communicationHandler.shutdown();
            database.disconnect();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        zoneHandler = null;
        communicationHandler = null;
        database = null;
    }
}
