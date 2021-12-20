package io.github.alexiscomete.lapinousecond.worlds;

public class WorldWithCoos extends World {

    public WorldWithCoos(int travelPrice, String name, String nameRP, String progName, String desc) {
        super(travelPrice, name, nameRP, progName, desc);
    }

    @Override
    public int getPriceForDistance(int distance) {
        return 0;
    }
}
