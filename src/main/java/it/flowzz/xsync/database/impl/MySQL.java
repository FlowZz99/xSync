package it.flowzz.xsync.database.impl;

import com.google.common.base.Preconditions;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import it.flowzz.xsync.SyncPlugin;
import it.flowzz.xsync.database.IDatabase;
import it.flowzz.xsync.database.credentials.Credentials;
import it.flowzz.xsync.models.PlayerData;
import it.flowzz.xsync.utils.ExperienceUtil;
import it.flowzz.xsync.utils.InventorySerializer;
import it.flowzz.xsync.utils.LocationSerializer;
import it.flowzz.xsync.utils.PotionSerializer;
import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class MySQL implements IDatabase {

    private final SyncPlugin syncPlugin;
    private HikariDataSource dataSource;

    public MySQL(SyncPlugin syncPlugin) {
        this.syncPlugin = syncPlugin;
    }

    private final static String SAVE_QUERY =
            "INSERT INTO xplayers (uuid, location, inventory, enderchest, effects, health, food, exp)" +
                    " VALUES (?,?,?,?,?,?,?,?) ON DUPLICATE KEY UPDATE" +
                    " uuid=VALUES(uuid), location=VALUES(location), inventory=VALUES(inventory), enderchest=VALUES(enderchest)," +
                    " effects=VALUES(effects), health=VALUES(health), food=VALUES(food), exp=VALUES(exp)";
    private final static String LOAD_QUERY =
            "SELECT * FROM xplayers where uuid=?";
    private final static String CREATE_TABLE_QUERY =
            "CREATE TABLE IF NOT EXISTS xplayers (uuid varchar(36), location text, inventory text, enderchest text, effects text, health double, food int, exp int, PRIMARY KEY (uuid))";

    @Override
    public void connect(Credentials credentials) {
        Preconditions.checkNotNull(credentials, "Credentials cannot be null.");
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:mysql://" + credentials.getHostname() + ":" + credentials.getPort() + "/" + credentials.getDatabase());
        config.setDriverClassName("com.mysql.cj.jdbc.Driver");
        config.setUsername(credentials.getUsername());
        config.setPassword(credentials.getPassword());
        dataSource = new HikariDataSource(config);
        createTable();
    }

    private void createTable() {
        try (Connection connection = dataSource.getConnection()) {
            connection.createStatement().execute(CREATE_TABLE_QUERY);
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
    }

    @Override
    public void disconnect() {
        if (dataSource != null) {
            dataSource.close();
        }
    }

    @Override
    public void savePlayer(Player player, boolean async) {
        if (async)
            CompletableFuture.runAsync(() -> performSaveSQL(player)).whenComplete((unused, throwable) -> postSave(syncPlugin, player));
        else performSaveSQL(player);
    }

    private void performSaveSQL(Player player) {
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement saveStatement = connection.prepareStatement(SAVE_QUERY);
            saveStatement.setString(1, player.getUniqueId().toString());
            saveStatement.setString(2, LocationSerializer.serializeFull(player.getLocation()));
            saveStatement.setString(3, InventorySerializer.inventoryToBase64(player.getInventory()));
            saveStatement.setString(4, InventorySerializer.inventoryToBase64(player.getEnderChest()));
            saveStatement.setString(5, PotionSerializer.potionToBase64(player.getActivePotionEffects()));
            saveStatement.setDouble(6, player.getHealth());
            saveStatement.setInt(7, player.getFoodLevel());
            saveStatement.setInt(8, ExperienceUtil.getPlayerExp(player));
            saveStatement.executeUpdate();
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
    }

    @Override
    public void loadPlayer(Player player) {
        CompletableFuture.supplyAsync(() -> {
            PlayerData playerData = null;
            try (Connection connection = dataSource.getConnection()) {
                PreparedStatement loadStatement = connection.prepareStatement(LOAD_QUERY);
                loadStatement.setString(1, player.getUniqueId().toString());
                ResultSet result = loadStatement.executeQuery();
                playerData = !result.next() ? new PlayerData(player) : new PlayerData(
                        UUID.fromString(result.getString(1)),
                        result.getString(2),
                        result.getString(3),
                        result.getString(4),
                        result.getString(5),
                        result.getDouble(6),
                        result.getInt(7),
                        result.getInt(8)
                );
            } catch (SQLException sqlException) {
                sqlException.printStackTrace();
            }
            return playerData;
        }).whenComplete((result, throwable) -> postLoad(syncPlugin, result, player));
    }
}
