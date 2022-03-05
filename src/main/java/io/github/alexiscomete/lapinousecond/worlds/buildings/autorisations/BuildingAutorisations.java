package io.github.alexiscomete.lapinousecond.worlds.buildings.autorisations;

import io.github.alexiscomete.lapinousecond.entity.Company;
import io.github.alexiscomete.lapinousecond.entity.Player;
import io.github.alexiscomete.lapinousecond.worlds.Place;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class BuildingAutorisations {
    private final ArrayList<BuildingAutorisation> buildingAutorisationArrayList;

    public BuildingAutorisations(ArrayList<BuildingAutorisation> buildingAutorisationArrayList) {
        this.buildingAutorisationArrayList = buildingAutorisationArrayList;
    }

    public BuildingAutorisations(JSONArray jsonArray) {
        ArrayList<BuildingAutorisation> buildingAutorisations = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            buildingAutorisations.add(toAutorisation(jsonArray.getJSONObject(i)));
        }
        this.buildingAutorisationArrayList = buildingAutorisations;
    }

    BuildingAutorisation toAutorisation(JSONObject jsonObject) {
        switch (jsonObject.getString("name")) {
            case "joueurs":
                return new TypeAutorisation<>(Player.class);
            case "entreprise":
                return new TypeAutorisation<>(Company.class);
            case "ville":
                return new TypeAutorisation<>(Place.class);
            default: // and "all"
                return new AllAutorisation();
        }
    }
}
