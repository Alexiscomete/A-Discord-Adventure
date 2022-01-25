package io.github.alexiscomete.lapinousecond.worlds;

import io.github.alexiscomete.lapinousecond.save.*;

import java.util.Optional;

public class Place extends CacheGetSet {
    private Long serverID;
    private ServerBot serverBot;
    private World world;
    private Integer x;
    private Integer y;
    private long[] connections;

    public Place() {
        super(SaveLocation.generateUniqueID(), Tables.PLACES.getTable());
    }

    public Place(ServerBot serverBot, World world, Integer x, Integer y, long id) {
        super(id, Tables.PLACES.getTable());
        this.serverBot = serverBot;
        this.world = world;
        this.x = x;
        this.y = y;
        if (serverBot == null) {
            this.serverID = null;
        } else {
            this.serverID = serverBot.getId();
        }
    }

    public Place(ServerBot serverBot, World world, Integer x, Integer y, long[] connections) {
        super(SaveLocation.generateUniqueID(), Tables.PLACES.getTable());
        this.serverBot = serverBot;
        this.world = world;
        this.x = x;
        this.y = y;
        this.connections = connections;
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

    public long[] getConnections() {
        return connections;
    }

    public void setConnections(long[] connections) {
        this.connections = connections;
    }

    public Place setAndGet(String row, String value) {
        super.set(row, value);
        return this;
    }
}
