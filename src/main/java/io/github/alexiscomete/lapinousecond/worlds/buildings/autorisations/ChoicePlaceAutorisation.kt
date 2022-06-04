package io.github.alexiscomete.lapinousecond.worlds.buildings.autorisations;

import org.json.JSONArray;

public class ChoicePlaceAutorisation extends AutList {

    public ChoicePlaceAutorisation(JSONArray jsonArray) {
        for (int i = 0; i < jsonArray.length(); i++) {
            this.buildingAutorisations.add(BuildingAutorisations.toAutorisation(jsonArray.getString(i)));
        }
    }
}
