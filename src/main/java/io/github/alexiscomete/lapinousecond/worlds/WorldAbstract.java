package io.github.alexiscomete.lapinousecond.worlds;

import java.util.Optional;

public class WorldAbstract extends World {

    public WorldAbstract(int travelPrice, String name, String nameRP, String progName, String desc) {
        super(travelPrice, name, nameRP, progName, desc);
    }

    @Override
    public double getPriceForDistance(double distance, boolean place) {
        return distance*travelPrice/100;
    }

    @Override
    public double getDistance(Place place1, Place place2) {
        Optional<Long> idPl1 = place1.getServerID(), idPl2 = place2.getServerID();
        if (idPl1.isPresent() && idPl2.isPresent()) {
            return Math.sqrt(Math.abs(idPl1.get()-idPl2.get())) / 1000000;
        } else {
            return 500;
        }
    }
}
