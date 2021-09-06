package it.flowzz.xsync.handlers;

import com.google.common.collect.Sets;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import it.flowzz.xsync.SyncPlugin;
import it.flowzz.xsync.api.events.PlayerChangeZoneEvent;
import it.flowzz.xsync.zones.ServerZone;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldBorder;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Level;


public class ZoneHandler {

    private final SyncPlugin syncPlugin;
    private final Set<ServerZone> serverZones;
    @Getter
    private ServerZone currentZone;

    public ZoneHandler(SyncPlugin syncPlugin) {
        this.syncPlugin = syncPlugin;
        this.serverZones = Sets.newHashSet();
        //Load Zones from config
        ConfigurationSection zoneSection = syncPlugin.getConfig().getConfigurationSection("zones");
        if (zoneSection != null) {
            zoneSection.getKeys(false).stream()
                    .map(zoneSection::getConfigurationSection)
                    .filter(Objects::nonNull)
                    .map(ServerZone::new)
                    .forEach(serverZones::add);
        }
        //Lookup the current server zone
        for (ServerZone serverZone : serverZones) {
            if (serverZone.getServerId().equals(syncPlugin.getCommunicationHandler().getServerId())) {
                currentZone = serverZone;
            }
        }
        if (syncPlugin.getConfig().getBoolean("settings.worldborder")) {
            createBorders();
        }
        startZoneUpdater();
    }

    private void createBorders() {
        World world = Bukkit.getWorld(currentZone.getWorld());
        if (world == null) {
            return;
        }
        WorldBorder worldBorder = world.getWorldBorder();
        worldBorder.setSize(((currentZone.getXMax() - currentZone.getXMin()) / 2.0) + 2.0);
        worldBorder.setCenter(currentZone.getCenter());
        worldBorder.setDamageAmount(0);

    }

    /**
     * Start the Zone Updater Task
     */
    private void startZoneUpdater() {
        Bukkit.getScheduler().runTaskTimer(syncPlugin, () -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (!currentZone.isInside(player)) {
                    transferPlayer(player);
                }
            }
        }, 20, 20);
    }

    /**
     * Returns an optional containing the player Zone(Server)
     * if present
     *
     * @param player the player.
     * @return the player's zone if present.
     */
    public Optional<ServerZone> getPlayerZone(Player player) {
        for (ServerZone serverZone : serverZones) {
            if (serverZone.isInside(player)) {
                return Optional.of(serverZone);
            }
        }
        syncPlugin.getLogger().log(Level.WARNING, String.format("Cannot find ServerZone for player %s, please check your zone configuration!", player.getName()));
        return Optional.empty();
    }

    /**
     * Transfer a player from one Zone(Server) to another.
     *
     * @param player the player.
     */
    @SuppressWarnings("UnstableApiUsage")
    private void transferPlayer(Player player) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        getPlayerZone(player).ifPresent(serverZone -> {
            String destination = serverZone.getServerId();

            PlayerChangeZoneEvent playerChangeZoneEvent = new PlayerChangeZoneEvent(player, currentZone.getServerId(), destination);
            Bukkit.getPluginManager().callEvent(playerChangeZoneEvent);
            //Don't send player if the event is cancelled
            if (playerChangeZoneEvent.isCancelled()) {
                return;
            }

            out.writeUTF("Connect");
            out.writeUTF(destination);
            syncPlugin.getCommunicationHandler().getCache().addLoadingPlayer(player.getUniqueId(), destination);
            player.sendPluginMessage(syncPlugin, "BungeeCord", out.toByteArray());
        });
    }

    public void shutdown() {
        serverZones.clear();
    }

}
