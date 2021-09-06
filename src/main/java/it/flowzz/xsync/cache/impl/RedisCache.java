package it.flowzz.xsync.cache.impl;

import it.flowzz.xsync.cache.ICache;
import lombok.RequiredArgsConstructor;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.UUID;

@RequiredArgsConstructor
public class RedisCache implements ICache {

    private static final String PLAYERS = "xsync:players:";

    private final JedisPool jedis;

    @Override
    public boolean isPlayerLoading(UUID uuid) {
        try (Jedis connection = jedis.getResource()) {
            String result = connection.hget(PLAYERS + uuid, "loading");
            return result != null && result.equals("true");
        }
    }

    @Override
    public void removePlayer(UUID uuid) {
        try (Jedis connection = jedis.getResource()) {
            connection.del(PLAYERS + uuid);
        }
    }

    @Override
    public void addLoadingPlayer(UUID uuid, String serverId) {
        try (Jedis connection = jedis.getResource()) {
            connection.hset(PLAYERS + uuid, "loading", "true");
            connection.hset(PLAYERS + uuid, "server", serverId);
        }
    }

    @Override
    public void removeLoadingPlayer(UUID uuid) {
        try (Jedis connection = jedis.getResource()) {
            connection.hset(PLAYERS + uuid, "loading", "false");
        }
    }

    @Override
    public boolean isPlayerOnline(UUID uuid) {
        try (Jedis connection = jedis.getResource()) {
            return connection.exists(PLAYERS + uuid);
        }
    }

    @Override
    public String getPlayerServer(UUID uuid) {
        try (Jedis connection = jedis.getResource()) {
            return connection.hget(PLAYERS + uuid, "server");
        }
    }

}
