package net.astroalchemist.multirespawn.data;

public class DeathLocation {
    private final String uuid;
    private final String server;
    private final String world;
    private final double x;
    private final double y;
    private final double z;
    private final float yaw;
    private final float pitch;
    private final long timestamp;

    public DeathLocation(String uuid, String server, String world, double x, double y, double z, float yaw,
            float pitch) {
        this.uuid = uuid;
        this.server = server;
        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
        this.timestamp = System.currentTimeMillis();
    }

    public DeathLocation(String uuid, String server, String world, double x, double y, double z, float yaw, float pitch,
            long timestamp) {
        this.uuid = uuid;
        this.server = server;
        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
        this.timestamp = timestamp;
    }

    public String getUuid() {
        return uuid;
    }

    public String getServer() {
        return server;
    }

    public String getWorld() {
        return world;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    public float getYaw() {
        return yaw;
    }

    public float getPitch() {
        return pitch;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String toJson() {
        return String.format(
                "{\"server\":\"%s\",\"world\":\"%s\",\"x\":%.2f,\"y\":%.2f,\"z\":%.2f,\"yaw\":%.2f,\"pitch\":%.2f,\"timestamp\":%d}",
                server, world, x, y, z, yaw, pitch, timestamp);
    }

    public static DeathLocation fromJson(String uuid, String json) {
        try {
            json = json.replace("{", "").replace("}", "").replace("\"", "");
            String[] parts = json.split(",");
            String server = null, world = null;
            double x = 0, y = 0, z = 0;
            float yaw = 0, pitch = 0;
            long timestamp = 0;

            for (String part : parts) {
                String[] kv = part.split(":");
                if (kv.length != 2)
                    continue;
                switch (kv[0]) {
                    case "server" -> server = kv[1];
                    case "world" -> world = kv[1];
                    case "x" -> x = Double.parseDouble(kv[1]);
                    case "y" -> y = Double.parseDouble(kv[1]);
                    case "z" -> z = Double.parseDouble(kv[1]);
                    case "yaw" -> yaw = Float.parseFloat(kv[1]);
                    case "pitch" -> pitch = Float.parseFloat(kv[1]);
                    case "timestamp" -> timestamp = Long.parseLong(kv[1]);
                }
            }
            return new DeathLocation(uuid, server, world, x, y, z, yaw, pitch, timestamp);
        } catch (Exception e) {
            return null;
        }
    }
}
