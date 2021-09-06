package it.flowzz.xsync.handlers;

import com.glyart.ermes.channels.impl.ZSTDDataCompressor;
import com.glyart.ermes.messages.IMessageHandler;
import com.glyart.ermes.messages.MessageRegistry;
import com.glyart.ermes.redis.RedisConnection;
import com.glyart.ermes.redis.RedisCredentials;
import com.glyart.ermes.redis.RedisMessagingChannel;
import it.flowzz.xsync.SyncPlugin;
import it.flowzz.xsync.cache.ICache;
import it.flowzz.xsync.cache.impl.RedisCache;
import it.flowzz.xsync.messages.AbstractMessage;
import it.flowzz.xsync.messages.impl.LoadPlayerMessage;
import lombok.Getter;

import java.util.logging.Level;

public class CommunicationHandler {

    private RedisConnection connection;
    private RedisMessagingChannel messagingChannel;
    private final SyncPlugin syncPlugin;

    @Getter
    private final String serverId;
    @Getter
    private ICache cache;

    public CommunicationHandler(SyncPlugin syncPlugin) {
        this.syncPlugin = syncPlugin;
        this.serverId = syncPlugin.getConfig().getString("settings.server-id");
        setup();
    }

    /**
     * Send the messages to the connected subscribers
     *
     * @param message the given messages
     */
    public void sendMessage(AbstractMessage... message) {
        try {
            messagingChannel.sendMessages(message);
        } catch (Exception ex) {
            syncPlugin.getLogger().log(Level.INFO, "Cannot send Communication-Message: " + ex.getMessage());
        }
    }

    /**
     * Registers the message handler for a given message class.
     *
     * @param clazz          the Message class
     * @param messageHandler the message handler.
     * @param <T>            the message
     */
    public <T extends AbstractMessage> void registerMessageHandler(Class<T> clazz, IMessageHandler<T> messageHandler) {
        messagingChannel.registerHandler(clazz, messageHandler);
    }

    /**
     * Setup all Messaging System.
     */
    private void setup() {
        connection = RedisConnection.create(RedisCredentials.Builder.newBuilder()
                .withHostname(syncPlugin.getConfig().getString("redis.hostname"))
                .withPassword(syncPlugin.getConfig().getString("redis.password"))
                .withPort(syncPlugin.getConfig().getInt("redis.port"))
                .build());
        try {
            connection.connect();
            cache = new RedisCache(connection.getConnection());
            messagingChannel = connection.createChannel("xSync-data", new ZSTDDataCompressor(), true, 512);
            registerMessages(
                    LoadPlayerMessage.class
            );
        } catch (Exception ex) {
            syncPlugin.getLogger().log(Level.INFO, "Cannot create Communication-Channel: " + ex.getMessage());
        }

    }

    /**
     * Registers Messages
     */
    private void registerMessages(Class<? extends AbstractMessage>... messages) {
        for (Class<? extends AbstractMessage> message : messages) {
            int id = MessageRegistry.register(message);
            syncPlugin.getLogger().log(Level.INFO, String.format("Registered packet %s with ID: %d", message.getName(), id));
        }
    }

    /**
     * Close the messaging channel and connection
     */
    public void shutdown() {
        try {
            messagingChannel.disconnect();
            connection.disconnect();
        } catch (Exception ex) {
            syncPlugin.getLogger().log(Level.INFO, "Cannot disconnect Communication: " + ex.getMessage());
        }
    }
}
