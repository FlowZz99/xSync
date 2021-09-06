package it.flowzz.xsync.database;

import it.flowzz.xsync.SyncPlugin;
import it.flowzz.xsync.api.events.PlayerPostLoadEvent;
import it.flowzz.xsync.api.events.PlayerPostSaveEvent;
import it.flowzz.xsync.database.credentials.Credentials;
import it.flowzz.xsync.messages.impl.LoadPlayerMessage;
import it.flowzz.xsync.models.PlayerData;
import it.flowzz.xsync.zones.ServerZone;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Optional;
import java.util.logging.Level;


public interface IDatabase {

    /**
     * Connect to database using the given credentials.
     *
     * @param credentials the credentials.
     */
    void connect(Credentials credentials);

    /**
     * Disconnect from Database
     */
    void disconnect();

    /**
     * Saves the PlayerData to database for a given player.
     * This will call the PlayerSaveEvent
     *
     * @param player the player.
     */
    void savePlayer(Player player, boolean async);

    /**
     * Loads the PlayerData from database for a given player.
     *
     * @param player the player.
     */
    void loadPlayer(Player player);

    /**
     * Loads the data for the given player with data
     * retrieved from the database implementation.
     * This function also calls the PlayerPostLoadEvent
     * once the player loaded all the data.
     *
     * @param syncPlugin the plugin instance.
     * @param result     playerdata database result.
     * @param player     the given player.
     */
    default void postLoad(SyncPlugin syncPlugin, PlayerData result, Player player) {
        //Call loadData method with Bukkit Main thread.
        Bukkit.getScheduler().runTask(syncPlugin, () -> {
            result.loadData();
            //Player data loaded
            syncPlugin.getCommunicationHandler().getCache().removeLoadingPlayer(player.getUniqueId());

            PlayerPostLoadEvent playerPostLoadEvent = new PlayerPostLoadEvent(player);
            Bukkit.getPluginManager().callEvent(playerPostLoadEvent);
        });
    }

    /**
     * This function is called after the database implementation
     * saved correctly all the data to disk.
     * This function also notify all the connected servers
     * if the player is transfering to a new ServerZone.
     * If the player is just logging out the message will not be sent.
     *
     * @param syncPlugin the plugin instance.
     * @param player     the given player.
     */
    default void postSave(SyncPlugin syncPlugin, Player player) {
        Optional<ServerZone> playerZone = syncPlugin.getZoneHandler().getPlayerZone(player);
        ServerZone currentZone = syncPlugin.getZoneHandler().getCurrentZone();
        syncPlugin.getCommunicationHandler().getCache().removeLoadingPlayer(player.getUniqueId());

        PlayerPostSaveEvent.Reason reason = PlayerPostSaveEvent.Reason.QUIT;
        if (playerZone.isPresent()) {
            ServerZone pZone = playerZone.get();
            String from = currentZone.getServerId();
            String to = pZone.getServerId();
            //Check if user is loggin out or changing server.
            if (!currentZone.getServerId().equals(pZone.getServerId())) {
                syncPlugin.getCommunicationHandler().sendMessage(new LoadPlayerMessage(from, to, player.getUniqueId()));
                reason = PlayerPostSaveEvent.Reason.SERVER_SWITCH;
            }else {
                //Player is logging out so we clear cache
                syncPlugin.getCommunicationHandler().getCache().removePlayer(player.getUniqueId());
            }

            PlayerPostSaveEvent playerPostSaveEvent = new PlayerPostSaveEvent(player, reason, from, to);
            Bukkit.getPluginManager().callEvent(playerPostSaveEvent);
        }
    }
}
