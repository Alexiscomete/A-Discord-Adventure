package io.github.alexiscomete.lapinousecond.worlds;

import io.github.alexiscomete.lapinousecond.Main;
import io.github.alexiscomete.lapinousecond.save.*;

import java.util.HashMap;
import java.util.Optional;

public class Place {
    private Long serverID;
    private ServerBot serverBot;
    private World world;
    private Integer x;
    private Integer y;
    private String name;
    private String traIn;
    private String traOut;
    private long[] connections;
    private String type;
    private final long id;
    private final HashMap<String, CacheValue> cache = new HashMap<>();


    public Place(ServerBot serverBot, World world, Integer x, Integer y, long id) {
        this.serverBot = serverBot;
        this.world = world;
        this.x = x;
        this.y = y;
        this.id = id;
        if (serverBot == null) {
            this.serverID = null;
        } else {
            this.serverID = serverBot.getId();
        }
    }

    public Place(ServerBot serverBot, World world, Integer x, Integer y, String name, String traIn, String traOut, long[] connections, String type) {
        this.serverBot = serverBot;
        this.world = world;
        this.x = x;
        this.y = y;
        this.name = name;
        this.traIn = traIn;
        this.traOut = traOut;
        this.connections = connections;
        this.type = type;
        this.id = SaveLocation.generateUniqueID();
        if (serverBot == null) {
            this.serverID = null;
        } else {
            this.serverID = serverBot.getId();
        }
    }

    public Optional<ServerBot> getServerBot() {
        return Optional.ofNullable(serverBot);
    }

    public Optional<Long> getServerID() {
        return Optional.ofNullable(serverID);
    }

    public void setServerBot(ServerBot serverBot) {
        this.serverBot = serverBot;
    }

    public void setServerID(Long serverID) {
        this.serverID = serverID;
    }

    public World getWorld() {
        return world;
    }

    public void setWorld(World world) {
        this.world = world;
    }

    public Optional<Integer> getX() {
        return Optional.ofNullable(x);
    }

    public void setX(Integer x) {
        this.x = x;
    }

    public Optional<Integer> getY() {
        return Optional.ofNullable(y);
    }

    public void setY(Integer y) {
        this.y = y;
    }

    public long getID() {
        return id;
    }

    public String getString(String row) {
        if (cache.containsKey(row)) {
            return cache.get(row).getString();
        } else {
            String str = Main.getSaveManager().getString(Tables.PLACES.getTable(), row, "TEXT", id);
            cache.put(row, new CacheValue(str));
            return str;
        }
    }

    public void set(String row, String value) {
        if (cache.containsKey(row)) {
            cache.get(row).set(value);
        } else {
            cache.put(row, new CacheValue(value));
        }
        Main.getSaveManager().setValue(Tables.PLACES.getTable(), id, row, value, "TEXT");
    }
}
