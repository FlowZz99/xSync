package it.flowzz.xsync.database.impl;

import com.google.common.base.Preconditions;
import com.google.gson.Gson;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoCredential;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Indexes;
import com.mongodb.client.model.ReplaceOptions;
import it.flowzz.xsync.SyncPlugin;
import it.flowzz.xsync.database.IDatabase;
import it.flowzz.xsync.database.credentials.Credentials;
import it.flowzz.xsync.models.PlayerData;
import org.bson.Document;
import org.bson.UuidRepresentation;
import org.bukkit.entity.Player;

import java.util.concurrent.CompletableFuture;

public class MongoDB implements IDatabase {

    private final SyncPlugin syncPlugin;

    private MongoClient client;
    private MongoDatabase database;

    public MongoDB(SyncPlugin syncPlugin) {
        this.syncPlugin = syncPlugin;
    }

    @Override
    public void connect(Credentials credentials) {
        Preconditions.checkNotNull(credentials, "Credentials cannot be null.");
        client = MongoClients.create(MongoClientSettings.builder()
                .uuidRepresentation(UuidRepresentation.STANDARD)
                .credential(MongoCredential.createCredential(
                        credentials.getUsername(),
                        "xSync",
                        credentials.getPassword().toCharArray()))
                .build());
        database = client.getDatabase("xSync");
        //Create index base on UUID
        IndexOptions indexOptions = new IndexOptions().unique(true);
        database.getCollection("player-data").createIndex(Indexes.ascending("uuid"), indexOptions);
    }

    @Override
    public void disconnect() {
        client.close();
    }

    @Override
    public void savePlayer(Player player, boolean async) {
        if (async)
            CompletableFuture.runAsync(() -> performSaveMongoDB(player)).whenComplete((unused, throwable) -> postSave(syncPlugin, player));
        else performSaveMongoDB(player);
    }

    private void performSaveMongoDB(Player player) {
        Document document = Document.parse(new PlayerData(player).toJson());
        database.getCollection("player-data").replaceOne(Filters.eq("uuid", player.getUniqueId().toString()), document, new ReplaceOptions().upsert(true));
    }

    @Override
    public void loadPlayer(Player player) {
        CompletableFuture.supplyAsync(() -> database.getCollection("player-data")
                        .find(Filters.eq("uuid", player.getUniqueId().toString())).first())
                .whenComplete((document, throwable) -> {
                    PlayerData playerData = document == null || document.isEmpty() ? new PlayerData(player) : new Gson().fromJson(document.toJson(), PlayerData.class);
                    postLoad(syncPlugin, playerData, player);
                });
    }
}
