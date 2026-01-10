package net.astroalchemist.multirespawn.managers;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import net.astroalchemist.multirespawn.MultiRespawn;
import net.astroalchemist.multirespawn.data.DeathLocation;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.CompletableFuture;

public class DatabaseManager {
    private final MultiRespawn plugin;
    private HikariDataSource hikari;
    private JedisPool jedisPool;
    private final String redisKeyPrefix = "multirespawn:death:";

    public DatabaseManager(MultiRespawn plugin) {
        this.plugin = plugin;
    }

    public boolean connect() {
        try {
            ConfigManager config = plugin.getConfigManager();

            HikariConfig hikariConfig = new HikariConfig();
            hikariConfig.setJdbcUrl("jdbc:mysql://" + config.getMysqlHost() + ":" + config.getMysqlPort() + "/"
                    + config.getMysqlDatabase());
            hikariConfig.setUsername(config.getMysqlUsername());
            hikariConfig.setPassword(config.getMysqlPassword());
            hikariConfig.setMaximumPoolSize(10);
            hikariConfig.setConnectionTimeout(5000);
            hikariConfig.setPoolName("MultiRespawn-Pool");
            hikariConfig.addDataSourceProperty("cachePrepStmts", "true");
            hikariConfig.addDataSourceProperty("prepStmtCacheSize", "250");
            hikari = new HikariDataSource(hikariConfig);

            JedisPoolConfig jedisConfig = new JedisPoolConfig();
            jedisConfig.setMaxTotal(10);
            String redisPass = config.getRedisPassword();
            if (redisPass != null && !redisPass.isEmpty()) {
                jedisPool = new JedisPool(jedisConfig, config.getRedisHost(), config.getRedisPort(), 2000, redisPass);
            } else {
                jedisPool = new JedisPool(jedisConfig, config.getRedisHost(), config.getRedisPort());
            }

            createTables();
            plugin.getLogger().info("Database connected successfully");
            return true;
        } catch (Exception e) {
            plugin.getLogger().severe("Database connection failed: " + e.getMessage());
            return false;
        }
    }

    private void createTables() throws SQLException {
        String sql = """
                CREATE TABLE IF NOT EXISTS multirespawn_deaths (
                    uuid VARCHAR(36) PRIMARY KEY,
                    server VARCHAR(64) NOT NULL,
                    world VARCHAR(64) NOT NULL,
                    x DOUBLE NOT NULL,
                    y DOUBLE NOT NULL,
                    z DOUBLE NOT NULL,
                    yaw FLOAT NOT NULL,
                    pitch FLOAT NOT NULL,
                    timestamp BIGINT NOT NULL
                )
                """;
        try (Connection conn = hikari.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.executeUpdate();
        }
    }

    public void saveDeathLocation(DeathLocation loc) {
        CompletableFuture.runAsync(() -> {
            try (var jedis = jedisPool.getResource()) {
                jedis.setex(redisKeyPrefix + loc.getUuid(), 86400, loc.toJson());
            }

            String sql = """
                    INSERT INTO multirespawn_deaths (uuid, server, world, x, y, z, yaw, pitch, timestamp)
                    VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
                    ON DUPLICATE KEY UPDATE server=?, world=?, x=?, y=?, z=?, yaw=?, pitch=?, timestamp=?
                    """;
            try (Connection conn = hikari.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, loc.getUuid());
                ps.setString(2, loc.getServer());
                ps.setString(3, loc.getWorld());
                ps.setDouble(4, loc.getX());
                ps.setDouble(5, loc.getY());
                ps.setDouble(6, loc.getZ());
                ps.setFloat(7, loc.getYaw());
                ps.setFloat(8, loc.getPitch());
                ps.setLong(9, loc.getTimestamp());
                ps.setString(10, loc.getServer());
                ps.setString(11, loc.getWorld());
                ps.setDouble(12, loc.getX());
                ps.setDouble(13, loc.getY());
                ps.setDouble(14, loc.getZ());
                ps.setFloat(15, loc.getYaw());
                ps.setFloat(16, loc.getPitch());
                ps.setLong(17, loc.getTimestamp());
                ps.executeUpdate();
            } catch (SQLException e) {
                plugin.getLogger().warning("Failed to save death location: " + e.getMessage());
            }
        });
    }

    public DeathLocation getDeathLocation(String uuid) {
        try (var jedis = jedisPool.getResource()) {
            String json = jedis.get(redisKeyPrefix + uuid);
            if (json != null) {
                return DeathLocation.fromJson(uuid, json);
            }
        } catch (Exception ignored) {
        }

        String sql = "SELECT * FROM multirespawn_deaths WHERE uuid = ?";
        try (Connection conn = hikari.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, uuid);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new DeathLocation(
                        uuid,
                        rs.getString("server"),
                        rs.getString("world"),
                        rs.getDouble("x"),
                        rs.getDouble("y"),
                        rs.getDouble("z"),
                        rs.getFloat("yaw"),
                        rs.getFloat("pitch"),
                        rs.getLong("timestamp"));
            }
        } catch (SQLException e) {
            plugin.getLogger().warning("Failed to get death location: " + e.getMessage());
        }
        return null;
    }

    public void close() {
        if (hikari != null)
            hikari.close();
        if (jedisPool != null)
            jedisPool.close();
    }
}
