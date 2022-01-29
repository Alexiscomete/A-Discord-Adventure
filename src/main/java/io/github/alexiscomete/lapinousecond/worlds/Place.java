package io.github.alexiscomete.lapinousecond.worlds;

import io.github.alexiscomete.lapinousecond.Main;
import io.github.alexiscomete.lapinousecond.save.*;
import org.javacord.api.entity.message.embed.EmbedBuilder;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
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
        HashMap<String, String> h = new HashMap<>();
        h.put("id", String.valueOf(getID()));
        Main.getSaveManager().insert("places", h);
    }

    public Place(long id) {
        super(id, Tables.PLACES.getTable());
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

    public EmbedBuilder getPlaceEmbed() {
        return new EmbedBuilder()
                .setAuthor(String.valueOf(id))
                .setTitle(getString("name"))
                .setColor(Color.green)
                .setDescription(getString("descr"))
                .addField("World", getString("world"), true)
                .addField("Type", getString("type"), true);
    }

    public static ArrayList<Place> toPlaces(String places) {
        String[] str = places.split(";");
        ArrayList<Place> places1 = new ArrayList<>();
        for (String s :
                str) {
            places1.add(new Place(Long.parseLong(s)));
        }
        return places1;
    }
}
