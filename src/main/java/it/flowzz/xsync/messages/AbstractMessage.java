package it.flowzz.xsync.messages;

import com.glyart.ermes.utils.ErmesDataInput;
import com.glyart.ermes.utils.ErmesDataOutput;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public abstract class AbstractMessage {

    protected String senderId;
    protected String receiverId;

    public abstract void read(ErmesDataInput input);

    public abstract void write(ErmesDataOutput output);

    /**
     * Reads server-ids the input.
     *
     * @param input the input data.
     */
    public void readIds(ErmesDataInput input) {
        this.senderId = input.readUTF();
        this.receiverId = input.readUTF();
        read(input);
    }

    /**
     * Writes the server-ids output data
     *
     * @param output the output data.
     */
    public void writeIds(ErmesDataOutput output) {
        output.writeUTF(senderId);
        output.writeUTF(receiverId);
        write(output);
    }

    /**
     * Check if the server is the correct receiver for the message.
     *
     * @param serverId the serverId
     * @return if the server can read the packet or not.
     */
    public boolean canReceive(String serverId) {
        if (serverId.equals(senderId)) {
            return false;
        }
        return serverId.equals(receiverId) || receiverId.equals("Broadcast");
    }

}
