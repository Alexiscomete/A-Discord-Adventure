package io.github.alexiscomete.lapinousecond.worlds;

import java.util.Optional;

public class Place {
    private Long serverID;
    private ServerBot serverBot;
    private World world;
    private Integer x;
    private Integer y;


    public Place(ServerBot serverBot, World world, Integer x, Integer y) {
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
}
