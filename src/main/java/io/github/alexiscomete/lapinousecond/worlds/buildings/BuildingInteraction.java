package io.github.alexiscomete.lapinousecond.worlds.buildings;

import io.github.alexiscomete.lapinousecond.Main;

public abstract class BuildingInteraction implements BuildMethods {

    protected Building building;

    public BuildingInteraction(Long l) {
        this.building = Main.getSaveManager().buildings.get(l);
    }
}
