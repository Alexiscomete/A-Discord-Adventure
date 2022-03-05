package io.github.alexiscomete.lapinousecond.worlds.buildings.autorisations;

import io.github.alexiscomete.lapinousecond.entity.Owner;
import org.json.JSONArray;

import java.util.ArrayList;

public class ChoicePlaceAutorisation implements BuildingAutorisation {

    private final ArrayList<BuildingAutorisation> buildingAutorisations = new ArrayList<>();

    public ChoicePlaceAutorisation(JSONArray jsonArray) {
        for (int i = 0; i < jsonArray.length(); i++) {
            this.buildingAutorisations.add(BuildingAutorisations.toAutorisation(jsonArray.getJSONObject(i)));
        }
    }

    @Override
    public boolean isAutorise(Owner owner) {
        return false;
    }
}
