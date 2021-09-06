package it.flowzz.xsync.api;

import com.glyart.ermes.messages.IMessageHandler;
import com.glyart.ermes.messages.MessageRegistry;
import it.flowzz.xsync.SyncPlugin;
import it.flowzz.xsync.messages.AbstractMessage;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@RequiredArgsConstructor
public final class DefaultImplementation implements SyncAPI {

    @Setter
    private static SyncAPI implementation;
    private final SyncPlugin syncPlugin;

    static SyncAPI getImplementation() {
        if (implementation == null)
            throw new IllegalStateException("No API implementation set. xSync didn't load correctly");
        return implementation;
    }

    @Override
    public void sendMessage(AbstractMessage... message) {
        syncPlugin.getCommunicationHandler().sendMessage(message);
    }

    @Override
    public <T extends AbstractMessage> int registerMessage(Class<T> clazz) {
        return MessageRegistry.register(clazz);
    }

    @Override
    public <T extends AbstractMessage> void registerMessageHandler(Class<T> clazz, IMessageHandler<T> messageHandler) {
        syncPlugin.getCommunicationHandler().registerMessageHandler(clazz, messageHandler);
    }

    @Override
    public String getCurrentServerId() {
        return syncPlugin.getCommunicationHandler().getServerId();
    }

    @Override
    public String getPlayerServerId(UUID uuid) {
        return syncPlugin.getCommunicationHandler().getCache().getPlayerServer(uuid);
    }

    @Override
    public boolean isOnline(UUID uuid) {
        return syncPlugin.getCommunicationHandler().getCache().isPlayerOnline(uuid);
    }

    @Override
    public boolean isLoadingData(UUID uuid) {
        return syncPlugin.getCommunicationHandler().getCache().isPlayerLoading(uuid);
    }
}
