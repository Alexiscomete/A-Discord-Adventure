package io.github.alexiscomete.lapinousecond.worlds.buildings.autorisations;

import io.github.alexiscomete.lapinousecond.entity.Company;
import io.github.alexiscomete.lapinousecond.entity.Player;
import io.github.alexiscomete.lapinousecond.worlds.Place;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class BuildingAutorisations extends AutList {

    public BuildingAutorisations(ArrayList<BuildingAutorisation> buildingAutorisationArrayList) {
        this.buildingAutorisations = buildingAutorisationArrayList;
    }

    public BuildingAutorisations(JSONArray jsonArray) {
        ArrayList<BuildingAutorisation> buildingAutorisations = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            buildingAutorisations.add(toAutorisation(jsonArray.getJSONObject(i)));
        }
        this.buildingAutorisations = buildingAutorisations;
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
                return new ChoicePlaceAutorisation(jsonObject.getJSONArray("possibilites"));
            default: // and "all"
                return new AllAutorisation();
        }
    }

    public static BuildingAutorisation toAutorisation(String string) {
        switch (string) {
            case "joueurs":
                return new TypeAutorisation<>(Player.class);
            case "entreprise":
                return new TypeAutorisation<>(Company.class);
            case "ville":
                return new TypeAutorisation<>(Place.class);
            case "habitants":
                return new Inhabitants();
            default: // and "all"
                return new AllAutorisation();
        }
    }
}
