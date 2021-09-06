package it.flowzz.xsync.cache;

import java.util.UUID;

public interface ICache {


    /**
     * Returns if a player is still loading the Data.
     *
     * @param uuid the player's uuid.ù
     * @return true if the player its data.
     */
    boolean isPlayerLoading(UUID uuid);

    /**
     * Remove the player from the online set.
     * @param uuid the player's uuid.
     */
    void removePlayer(UUID uuid);

    /**
     * Add the player to loading set.
     *
     * @param uuid the player's uuid.
     * @param serverId the player's server id.
     */
    void addLoadingPlayer(UUID uuid, String serverId);

    /**
     * Remove the player from loading set.
     *
     * @param uuid the player's uuid.
     */
    void removeLoadingPlayer(UUID uuid);

    /**
     * Return the player current server-id given an uuid
     * @param uuid the player's uuid.
     * @return the plauer's server-id
     */
    String getPlayerServer(UUID uuid);

    /**
     * Returns if the player is online in any of the linked servers.
     * @param uuid the player's uuid.
     * @return true if the player is online.
     */
    boolean isPlayerOnline(UUID uuid);
}
