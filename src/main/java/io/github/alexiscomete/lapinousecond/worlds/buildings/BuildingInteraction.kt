package io.github.alexiscomete.lapinousecond.worlds.buildings;

public abstract class BuildingInteraction implements BuildMethods {

    protected final Building building;

    public BuildingInteraction(Building building) {this.building = building;}
}
