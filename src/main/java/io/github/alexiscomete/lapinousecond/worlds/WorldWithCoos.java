package io.github.alexiscomete.lapinousecond.worlds;

public class WorldWithCoos extends World {

    public WorldWithCoos(int travelPrice, String name, String nameRP, String progName, String desc) {
        super(travelPrice, name, nameRP, progName, desc);
    }

    @Override
    public int getPriceForDistance(int distance, boolean place) {
        return 0;
    }

    @Override
    public int getDistance(Place place1, Place place2) {
        return 0;
    }
}
