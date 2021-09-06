package it.flowzz.xsync.api.events;


import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * Event called after a player saved its data after server switch or disconnecting.
 */
@Getter
@RequiredArgsConstructor
public class PlayerPostSaveEvent extends Event {

    private static final HandlerList HANDLERS_LIST = new HandlerList();

    private final Player player;
    private final Reason reason;
    private final String from;
    private final String to;

    public static HandlerList getHandlerList() {
        return HANDLERS_LIST;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS_LIST;
    }

    public enum Reason {
        SERVER_SWITCH,
        QUIT
    }
}
