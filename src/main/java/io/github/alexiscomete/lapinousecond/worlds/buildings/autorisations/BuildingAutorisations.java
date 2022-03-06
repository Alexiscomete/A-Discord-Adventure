package io.github.alexiscomete.lapinousecond.worlds.buildings.autorisations;

import io.github.alexiscomete.lapinousecond.entity.Company;
import io.github.alexiscomete.lapinousecond.entity.Owner;
import io.github.alexiscomete.lapinousecond.entity.Player;
import io.github.alexiscomete.lapinousecond.worlds.Place;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class BuildingAutorisations implements BuildingAutorisation {
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

    public static BuildingAutorisation toAutorisation(JSONObject jsonObject) {
        switch (jsonObject.getString("name")) {
            case "joueurs":
                return new TypeAutorisation<>(Player.class);
            case "entreprise":
                return new TypeAutorisation<>(Company.class);
            case "ville":
                return new TypeAutorisation<>(Place.class);
            case "habitants":
                return new Inhabitants();
            case "choix_ville":
                return new ChoicePlaceAutorisation(jsonObject.getJSONArray("possibilités"));
            default: // and "all"
                return new AllAutorisation();
        }
    }

    @Override
    public boolean isAutorise(Owner owner) {
        for (BuildingAutorisation buildingAutorisation : buildingAutorisationArrayList) {
            if (buildingAutorisation.isAutorise(owner)) {
                return true;
            }
        }
        return false;
    }
}
