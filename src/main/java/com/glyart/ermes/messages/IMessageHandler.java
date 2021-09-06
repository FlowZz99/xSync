package com.glyart.ermes.messages;

import it.flowzz.xsync.messages.AbstractMessage;

@FunctionalInterface
public interface IMessageHandler<T extends AbstractMessage> {

    void handle(T message);

}
