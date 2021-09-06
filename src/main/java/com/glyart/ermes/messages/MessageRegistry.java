package com.glyart.ermes.messages;

import com.glyart.ermes.utils.ErmesDataInput;
import it.flowzz.xsync.messages.AbstractMessage;

import java.util.HashMap;
import java.util.Map;

/**
 * Replaced Message interface with AbstractMessage
 */
public class MessageRegistry {

    private static final Map<Integer, Class<? extends AbstractMessage>> ID_TO_MESSAGE = new HashMap<>();
    private static final Map<Class<? extends AbstractMessage>, Integer> MESSAGE_TO_ID = new HashMap<>();

    public static int getMessageID(Class<? extends AbstractMessage> clazz) {
        return MESSAGE_TO_ID.get(clazz);
    }

    public static Class<? extends AbstractMessage> getMessageClass(int id) {
        return ID_TO_MESSAGE.get(id);
    }

    public static <T extends AbstractMessage> T createMessage(ErmesDataInput input) throws Exception {
        int id = input.readInt();
        Class<T> clazz = (Class<T>) getMessageClass(id);
        T message = clazz.getConstructor().newInstance();
        message.readIds(input);
        message.read(input);
        return message;
    }

    public static <T extends AbstractMessage> int register(Class<T> clazz) {
        int id = clazz.getName().hashCode();
        if (ID_TO_MESSAGE.containsKey(id))
            throw new RuntimeException("There is already a Message with ID: " + id + ", " + clazz.getName());

        ID_TO_MESSAGE.put(id, clazz);
        MESSAGE_TO_ID.put(clazz, id);
        return id;
    }

}
