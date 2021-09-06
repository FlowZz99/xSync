package it.flowzz.xsync.api;

import com.glyart.ermes.messages.IMessageHandler;
import it.flowzz.xsync.messages.AbstractMessage;

import java.util.UUID;

public interface SyncAPI {

    /**
     * Sends the message to all connected subscribers.
     * You can choose which server will receive the message
     * by specifing the receiver-id when you create the message.
     * If you want all servers to read the message you can set the
     * receiver-id to "Broadcast".
     *
     * @param message the message that extends AbstractMessage
     */
    void sendMessage(AbstractMessage... message);

    /**
     * Registers the message withing the internal MessageRegistry.
     *
     * @param clazz the message class.
     * @param <T>   the message type.
     * @return the generated id for the packet.
     */
    <T extends AbstractMessage> int registerMessage(Class<T> clazz);

    /**
     * Registers the message handler using a functional
     * interface implementation as handler.
     *
     * @param clazz          the message class.
     * @param messageHandler the functional interface.
     * @param <T>            the message type.
     */
    <T extends AbstractMessage> void registerMessageHandler(Class<T> clazz, IMessageHandler<T> messageHandler);

    /**
     * Return the server-id of the current server
     *
     * @return the server-id.
     */
    String getCurrentServerId();

    /**
     * Return the server-id of the server that the
     * player is connected to else if the player is
     * not connected it will return "nil"
     *
     * @param uuid the user's uuid.
     * @return player's server-id if online else "nil"
     */
    String getPlayerServerId(UUID uuid);

    /**
     * Returns true if the player is online on any of the linked servers.
     *
     * @param uuid the user's uuid.
     * @return true if the player is online in any of the linked servers.
     */
    boolean isOnline(UUID uuid);

    /**
     * Returns if the player is currently loading data.
     *
     * @param uuid the user's uuid.
     * @return true if the player is still load its data.
     */
    boolean isLoadingData(UUID uuid);

    /**
     * Returns the API implementation.
     *
     * @return API implementation.
     */
    static SyncAPI getInstance() {
        return DefaultImplementation.getImplementation();
    }

}
