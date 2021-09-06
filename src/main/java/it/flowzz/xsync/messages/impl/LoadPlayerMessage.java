package it.flowzz.xsync.messages.impl;

import com.glyart.ermes.utils.ErmesDataInput;
import com.glyart.ermes.utils.ErmesDataOutput;
import it.flowzz.xsync.messages.AbstractMessage;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Message used to LoadPlayer data after a player switched server.
 */
@Getter
@NoArgsConstructor
public class LoadPlayerMessage extends AbstractMessage {

    private UUID uuid;

    public LoadPlayerMessage(String senderId, String receiverId, UUID uuid) {
        super(senderId, receiverId);
        this.uuid = uuid;
    }

    @Override
    public void read(ErmesDataInput input) {
        this.uuid = input.readUUID();
    }

    @Override
    public void write(ErmesDataOutput output) {
        output.writeUUID(uuid);
    }

}
