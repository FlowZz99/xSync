package it.flowzz.xsync.api.events;


import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * Event called once a player loaded its data after server switch or disconnecting.
 */
@Getter
@RequiredArgsConstructor
public class PlayerPostLoadEvent extends Event {

    private static final HandlerList HANDLERS_LIST = new HandlerList();

    private final Player player;

    public static HandlerList getHandlerList() {
        return HANDLERS_LIST;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS_LIST;
    }
}
