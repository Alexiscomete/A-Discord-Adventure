package io.github.alexiscomete.lapinousecond.worlds.buildings.evolution;

import io.github.alexiscomete.lapinousecond.worlds.buildings.BuildMethods;
import io.github.alexiscomete.lapinousecond.worlds.buildings.Building;
import io.github.alexiscomete.lapinousecond.worlds.buildings.BuildingInteraction;
import io.github.alexiscomete.lapinousecond.worlds.buildings.Buildings;
import org.json.JSONObject;

public class Evolution {
    private final Buildings evolutionTarget;
    private final double cost;

    public Evolution(Buildings evolutionTarget, double cost) {
        this.evolutionTarget = evolutionTarget;
        this.cost = cost;
    }

    public Evolution(JSONObject jsonObject) {
        this.evolutionTarget = Buildings.valueOf(jsonObject.getString("name").toUpperCase());
        this.cost = jsonObject.getDouble("cost");
    }

    public Buildings getEvolutionTarget() {
        return evolutionTarget;
    }

    public double getCost() {
        return cost;
    }

    public void evolute(Building building) {
        BuildingInteraction buildMethods = evolutionTarget.get(building);
        building.evolute(buildMethods);
        building.set("build_status", "evolute");
        building.set("collect_value", "0.0");
        building.set("collect_target", "0.0");
    }
}
