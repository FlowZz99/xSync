package it.flowzz.xsync.listeners;

import it.flowzz.xsync.SyncPlugin;
import it.flowzz.xsync.api.events.PlayerPostLoadEvent;
import it.flowzz.xsync.lang.Lang;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.*;

@RequiredArgsConstructor
public final class PlayerListener implements Listener {

    private final SyncPlugin syncPlugin;

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (!syncPlugin.getCommunicationHandler().getCache().isPlayerLoading(player.getUniqueId())) {
            syncPlugin.getCommunicationHandler().getCache().addLoadingPlayer(player.getUniqueId(), syncPlugin.getCommunicationHandler().getServerId());
            syncPlugin.getDatabase().loadPlayer(player);
        }
        player.sendMessage(Lang.PLAYER_LOADING.getTranslation());
    }

    @EventHandler
    public void onLoad(PlayerPostLoadEvent event){
        event.getPlayer().sendMessage(Lang.PLAYER_LOADED.getTranslation());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        syncPlugin.getDatabase().savePlayer(event.getPlayer(), true);
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent event) {
        checkLoading(event, event.getPlayer());
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player player) {
            checkLoading(event, player);
        }
    }

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent event) {
        checkLoading(event, event.getPlayer());
    }

    public <T extends Cancellable> void checkLoading(T event, Player player) {
        if (syncPlugin.getCommunicationHandler().getCache().isPlayerLoading(player.getUniqueId())) {
            event.setCancelled(true);
        }
    }

}
