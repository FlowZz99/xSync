package it.flowzz.xsync.api.events;


import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * Event called before a player attempt to connect to another zone.
 */
@Getter
@Setter
public class PlayerChangeZoneEvent extends Event implements Cancellable {

    private static final HandlerList HANDLERS_LIST = new HandlerList();

    private final Player player;
    private final String from;
    private String destination;
    private boolean cancelled = false;


    public PlayerChangeZoneEvent(Player player, String serverId, String destination) {
        this.player = player;
        this.from = serverId;
        this.destination = destination;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS_LIST;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS_LIST;
    }

}
