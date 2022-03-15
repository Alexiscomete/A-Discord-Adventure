package io.github.alexiscomete.lapinousecond.worlds.buildings;

import io.github.alexiscomete.lapinousecond.Main;
import io.github.alexiscomete.lapinousecond.worlds.buildings.autorisations.BuildingAutorisations;
import io.github.alexiscomete.lapinousecond.worlds.buildings.evolution.Evolution;
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
        System.out.println(inputStream);
        if (inputStream == null) {
            System.out.println("eeee");
            jsonObject = new JSONObject("{}");
        } else {
            Scanner sc = new Scanner(inputStream);
            StringBuilder stringBuilder = new StringBuilder();
            sc.forEachRemaining(stringBuilder::append);
            jsonObject = new JSONObject(stringBuilder);
        }
    }

    public static Building load(String save) {
        if (save == null || !save.contains(":")) {
            return null;
        }
        String[] strings = save.split(":");
        try {
            String type = Main.getSaveManager().getBuildingType(Long.parseLong(strings[0]));
            if (type == null) {
                return null;
            }
            Buildings buildings = valueOf(type);
            return new Building(Long.parseLong(strings[1]), buildings.get(Long.parseLong(strings[1])));
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

    private final Function<Long, BuildingInteraction> getBuildingM;
    private final String name;

    private double basePrice = 0.0;
    private final ArrayList<Evolution> evol = new ArrayList<>();
    private boolean build = true;
    private BuildingAutorisations buildingAutorisations;

    Buildings(Function<Long, BuildingInteraction> getBuildingM, String name) {
        this.getBuildingM = getBuildingM;
        this.name = name;
    }

    public void setModelWithJson(JSONObject jsonObject) {
        buildingAutorisations = new BuildingAutorisations(jsonObject.getJSONArray("autorisation"));
        basePrice = jsonObject.getDouble("cost");
        build = jsonObject.getBoolean("build");
    }

    public BuildingInteraction get(long id) {
        return getBuildingM.apply(id);
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

    public boolean isBuild() {
        return build;
    }
}
