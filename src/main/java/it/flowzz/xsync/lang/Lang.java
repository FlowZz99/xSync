package it.flowzz.xsync.lang;

import it.flowzz.xsync.SyncPlugin;
import lombok.AllArgsConstructor;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;

@AllArgsConstructor
public enum Lang {

    PLAYER_LOADED("player-loaded"),
    PLAYER_LOADING("player-loading");

    static {
        lang = SyncPlugin.getPlugin(SyncPlugin.class).getLang();
    }
    private static FileConfiguration lang;

    private final String configNode;

    public String getTranslation() {
        return ChatColor.translateAlternateColorCodes('&', lang.getString(configNode));
    }
}

