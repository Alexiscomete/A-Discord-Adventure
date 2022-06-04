package io.github.alexiscomete.lapinousecond.worlds.buildings;

import io.github.alexiscomete.lapinousecond.Main;
import io.github.alexiscomete.lapinousecond.worlds.buildings.autorisations.BuildingAutorisations;
import io.github.alexiscomete.lapinousecond.worlds.buildings.evolution.Evolution;
import io.github.alexiscomete.lapinousecond.worlds.buildings.interactions.*;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.function.Function;

public enum Buildings {

    ARMURERIE(Armurerie::new, "armurerie"),
    ARRET_BUS(ArretBus::new, "arret_bus"),
    AUBERGE(Auberge::new, "auberge"),
    BANQUE(Banque::new, "banque"),
    BAR(Bar::new, "bar"),
    BIBLIOTHEQUE(Bibliotheque::new, "bibliotheque"),
    BOULANGERIE(Boulangerie::new, "boulangerie"),
    BOUTIQUE(Boutique::new, "boutique"),
    CASINO(Casino::new, "casino"),
    HOPITAL(Hopital::new, "hopital"),
    JOURNAL(Journal::new, "journal"),
    MAIRIE(Mairie::new, "mairie"),
    MAISON(Maison::new, "maison"),
    PHARMACIE(Pharmacie::new, "pharmacie");

    public static Building load(String save) {
        if (save == null) {
            return null;
        }
        if (save.contains(":")) {
            save = save.split(":")[1];
        }
        try {
            String type = Main.getSaveManager().getBuildingType(Long.parseLong(save));
            if (type == null) {
                System.out.println("null");
                return null;
            }
            Buildings buildings = valueOf(type.toUpperCase());
            return new Building(Long.parseLong(save), buildings);
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

    private final Function<Building, BuildingInteraction> getBuildingM;
    private final String name;

    private double basePrice = 0.0;
    private final ArrayList<Evolution> evol = new ArrayList<>();
    private boolean build = true;
    private BuildingAutorisations buildingAutorisations;

    Buildings(Function<Building, BuildingInteraction> getBuildingM, String name) {
        this.getBuildingM = getBuildingM;
        this.name = name;
        setModelWithJson(Building.jsonObject.getJSONObject(name));
    }

    public void setModelWithJson(JSONObject jsonObject) {
        buildingAutorisations = new BuildingAutorisations(jsonObject.getJSONArray("autorisation"));
        basePrice = jsonObject.getDouble("cost");
        build = jsonObject.getBoolean("build");
    }

    public BuildingInteraction get(Building building) {
        return getBuildingM.apply(building);
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
