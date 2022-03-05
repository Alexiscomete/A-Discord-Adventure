package io.github.alexiscomete.lapinousecond.worlds.buildings.autorisations;

import org.json.JSONArray;

import java.util.ArrayList;

public class BuildingAutorisations {
    private final ArrayList<BuildingAutorisation> buildingAutorisationArrayList;

    public BuildingAutorisations(ArrayList<BuildingAutorisation> buildingAutorisationArrayList) {
        this.buildingAutorisationArrayList = buildingAutorisationArrayList;
    }

    public BuildingAutorisations(JSONArray jsonArray) {
        ArrayList<BuildingAutorisation> buildingAutorisations = new ArrayList<>();
        this.buildingAutorisationArrayList = buildingAutorisations;
    }
}
