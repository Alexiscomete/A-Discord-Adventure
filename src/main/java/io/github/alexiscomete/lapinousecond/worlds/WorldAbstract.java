package io.github.alexiscomete.lapinousecond.worlds;

public class WorldAbstract extends World {

    public WorldAbstract(int travelPrice, String name, String nameRP, String progName, String desc) {
        super(travelPrice, name, nameRP, progName, desc);
    }

    @Override
    public int getPriceForDistance(int distance, boolean place) {
        return distance*travelPrice/100;
    }

    @Override
    public int getDistance(Place place1, Place place2) {
        return 0;
    }
}
