package com.glyart.ermes.channels;

import com.glyart.ermes.connections.IConnection;
import com.glyart.ermes.messages.IMessageHandler;
import it.flowzz.xsync.messages.AbstractMessage;

public interface IMessagingChannel<T extends IConnection<? extends IMessagingChannel<?>, ?>> {

    /**
     * Connects the IMessagingChannel to the specified IConnection.
     * @param connection The IConnection implementation
     */
    void connect(T connection) throws Exception;

    /**
     * Disconnects the IMessagingChannel.
     */
    void disconnect() throws Exception;

    /**
     * Sends the specified Message(s) in this IMessagingChannel
     * @param messages The Message(s) to send
     */
    void sendMessages(AbstractMessage... messages) throws Exception;

    /**
     * Registers a new IMessageHandler for a specified Message
     * @param messageClazz The Message
     * @param handler The IMessageHandler to register
     */
    <V extends AbstractMessage> void registerHandler(Class<V> messageClazz, IMessageHandler<V> handler);

}
