package it.flowzz.xsync.models;

import com.google.gson.Gson;
import it.flowzz.xsync.utils.ExperienceUtil;
import it.flowzz.xsync.utils.InventorySerializer;
import it.flowzz.xsync.utils.LocationSerializer;
import it.flowzz.xsync.utils.PotionSerializer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;

import java.io.IOException;
import java.util.UUID;

@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
public class PlayerData {

    private final UUID uuid;
    private String location;
    private String inventory;
    private String enderchest;
    private String effects;
    private double health;
    private int food;
    private int exp;

    public PlayerData(Player player) {
        this.uuid = player.getUniqueId();
        this.location = LocationSerializer.serializeFull(player.getLocation());
        this.inventory = InventorySerializer.inventoryToBase64(player.getInventory());
        this.enderchest = InventorySerializer.inventoryToBase64(player.getEnderChest());
        this.effects = PotionSerializer.potionToBase64(player.getActivePotionEffects());
        this.health = player.getHealth();
        this.exp = ExperienceUtil.getPlayerExp(player);
        this.food = player.getFoodLevel();
    }

    /**
     * Converts the playerdata to JSON
     *
     * @return JSON string representing playerdata.
     */
    public String toJson() {
        return new Gson().toJson(this);
    }

    /**
     * Loads basic players data and then the extra CustomData.
     */
    public void loadData() {
        Player player = Bukkit.getPlayer(uuid);
        if (player != null) {
            player.teleport(LocationSerializer.deserializeFull(location));
            try {
                player.getInventory().setContents(InventorySerializer.inventoryFromBase64(inventory, InventoryType.PLAYER));
                player.getEnderChest().setContents(InventorySerializer.inventoryFromBase64(enderchest, InventoryType.ENDER_CHEST));
                player.addPotionEffects(PotionSerializer.potionFromBase64(effects));
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            player.setHealth(health);
            player.setFoodLevel(food);
            player.setLevel(0);
            player.setExp(0);
            player.giveExp(exp);
        }
    }
}
