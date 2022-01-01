package io.github.alexiscomete.lapinousecond.worlds;

public abstract class World {
    private int travelPrice;
    private final String name, nameRP, progName, desc;

    public World(int travelPrice, String name, String nameRP, String progName, String desc) {
        this.travelPrice = travelPrice;
        this.name = name;
        this.nameRP = nameRP;
        this.progName = progName;
        this.desc = desc;
    }

    public abstract double getPriceForDistance(double distance, boolean place);
    public abstract double getDistance(Place place1, Place place2);

    public int getTravelPrice() {
        return travelPrice;
    }

    public void setTravelPrice(int travelPrice) {
        this.travelPrice = travelPrice;
    }

    public String getName() {
        return name;
    }

    public String getNameRP() {
        return nameRP;
    }

    public String getProgName() {
        return progName;
    }

    public String getDesc() {
        return desc;
    }
}
