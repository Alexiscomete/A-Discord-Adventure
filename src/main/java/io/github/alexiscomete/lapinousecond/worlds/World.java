package io.github.alexiscomete.lapinousecond.worlds;

public abstract class World {
    int travelPrice;
    String name, nameRP, progName, desc;

    public World(int travelPrice, String name, String nameRP, String progName, String desc) {
        this.travelPrice = travelPrice;
        this.name = name;
        this.nameRP = nameRP;
        this.progName = progName;
        this.desc = desc;
    }

    public abstract int getPriceForDistance(int distance);
}
