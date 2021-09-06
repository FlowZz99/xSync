package it.flowzz.xsync.zones;


import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

@Getter
@RequiredArgsConstructor
public class ServerZone {

    private final String serverId;
    private final String world;
    private final double xMin;
    private final double xMax;
    private final double zMin;
    private final double zMax;

    public ServerZone(ConfigurationSection configurationSection) {
        serverId = configurationSection.getString("server-id");
        world = configurationSection.getString("world");
        xMin = configurationSection.getDouble("X-Min");
        xMax = configurationSection.getDouble("X-Max");
        zMin = configurationSection.getDouble("Z-Min");
        zMax = configurationSection.getDouble("Z-Max");
    }

    public Location getCenter(){
        return new Location(Bukkit.getWorld(world), ((xMax - xMin) / 2.0) + xMin, 0, ((zMax - zMin) / 2.0) + zMin);
    }

    public boolean isInside(Location loc) {
        return loc.getWorld().getName().equals(world) &&
                loc.getX() >= this.xMin &&
                loc.getX() <= this.xMax &&
                loc.getZ() >= this.zMin &&
                loc.getZ() <= this.zMax;
    }

    public boolean isInside(Player player) {
        return isInside(player.getLocation());
    }
}
