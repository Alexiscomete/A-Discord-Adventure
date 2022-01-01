package io.github.alexiscomete.lapinousecond.worlds;

public class WorldWithCoos extends World {

    public WorldWithCoos(int travelPrice, String name, String nameRP, String progName, String desc) {
        super(travelPrice, name, nameRP, progName, desc);
    }

    @Override
    public double getPriceForDistance(double distance, boolean place) {
        return 0;
    }

    @Override
    public double getDistance(Place place1, Place place2) {
        return 0;
    }
}
