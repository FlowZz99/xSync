package it.flowzz.xsync.handlers;

import it.flowzz.xsync.SyncPlugin;
import it.flowzz.xsync.messages.impl.LoadPlayerMessage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class MessageHandler {

    private final SyncPlugin syncPlugin;

    public MessageHandler(SyncPlugin syncPlugin) {
        this.syncPlugin = syncPlugin;
        registerHandlers();
    }

    /**
     * Register all internal Message handlers.
     */
    private void registerHandlers() {
        CommunicationHandler communicationHandler = syncPlugin.getCommunicationHandler();
        //LoadPlayerMessageHandler
        communicationHandler.registerMessageHandler(LoadPlayerMessage.class, message -> {
            if (!message.canReceive(communicationHandler.getServerId())) {
                return;
            }
            Player player = Bukkit.getPlayer(message.getUuid());
            if (player != null) {
                syncPlugin.getDatabase().loadPlayer(player);
            }
        });
    }
}
