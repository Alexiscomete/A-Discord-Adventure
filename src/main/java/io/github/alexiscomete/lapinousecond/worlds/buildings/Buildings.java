package io.github.alexiscomete.lapinousecond.worlds.buildings;

import io.github.alexiscomete.lapinousecond.Main;
import io.github.alexiscomete.lapinousecond.save.SaveManager;

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

    public static Building load(String save) {
        if (save == null || save.equals("")) {
            return null;
        }
        try {
            String type = Main.getSaveManager().getBuildingType(Long.parseLong(save));
            if (type == null) {
                return null;
            }
            if (Objects.equals(type, "project")) {
                return new BuildProject(Long.parseLong(save));
            }
            Buildings buildings = valueOf(type);
            return buildings.get(Long.parseLong(save));
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    public static ArrayList<Building> loadBuildings(String str) {
        ArrayList<Building> buildings = new ArrayList<>();
        String[] strings = str.split(";");

        for (String s :
                strings) {
            Building b = load(s);
            if (b != null) {
                buildings.add(b);
            }
        }

        return buildings;
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
