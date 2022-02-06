package io.github.alexiscomete.lapinousecond.worlds.buildings;

import java.util.ArrayList;
import java.util.Objects;
import java.util.function.Function;

public enum Buildings {

    ;

    private final Function<Long, Building> getBuilding;
    private final String type;
    private final double basePrice;
    private final String name;
    private final String evol;

    Buildings(Function<Long, Building> getBuilding, String type, double basePrice, String name, String evol) {
        this.getBuilding = getBuilding;
        this.type = type;
        this.basePrice = basePrice;
        this.name = name;
        this.evol = evol;
    }

    public Building load(String save) {
        String[] str = save.split(":");
        if (str.length == 0) {
            return null;
        }
        if (str.length == 1 || Objects.equals(str[1], "project")) {
            return new BuildProject(Long.parseLong(str[0]));
        }
        return null;
    }

    public ArrayList<Building> loadBuildings() {
        return null;
    }

    public Building get(long id) {
        return getBuilding.apply(id);
    }

    public String getType() {
        return type;
    }

    public double getBasePrice() {
        return basePrice;
    }

    public String getName() {
        return name;
    }

    public String getEvol() {
        return evol;
    }
}
