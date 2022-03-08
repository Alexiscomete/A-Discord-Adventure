package io.github.alexiscomete.lapinousecond.worlds.buildings;

import io.github.alexiscomete.lapinousecond.Main;
import io.github.alexiscomete.lapinousecond.worlds.buildings.autorisations.BuildingAutorisations;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.function.Function;

public enum Buildings {

    ;

    public static final JSONObject jsonObject;

    static {
        InputStream inputStream = Buildings.class.getResourceAsStream("buildings-config.json");
        assert inputStream != null;
        Scanner sc = new Scanner(inputStream);
        StringBuilder stringBuilder = new StringBuilder();
        sc.forEachRemaining(stringBuilder::append);
        jsonObject = new JSONObject(stringBuilder);
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

    private final Function<Long, Building> getBuilding;
    private final String name;

    private double basePrice = 0.0;
    private final ArrayList<Evolution> evol = new ArrayList<>();
    private boolean build = true;
    private BuildingAutorisations buildingAutorisations;

    Buildings(Function<Long, Building> getBuilding, String name) {
        this.getBuilding = getBuilding;
        this.name = name;
    }

    public void setModelWithJson(JSONObject jsonObject) {
        buildingAutorisations = new BuildingAutorisations(jsonObject.getJSONArray("autorisation"));
        basePrice = jsonObject.getDouble("cost");
    }

    public Building get(long id) {
        return getBuilding.apply(id);
    }

    public double getBasePrice() {
        return basePrice;
    }

    public String getName() {
        return name;
    }

    public ArrayList<Evolution> getEvol() {
        return evol;
    }

    public BuildingAutorisations getBuildingAutorisations() {
        return buildingAutorisations;
    }
}
