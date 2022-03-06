package io.github.alexiscomete.lapinousecond.worlds.buildings.autorisations;

import io.github.alexiscomete.lapinousecond.entity.Owner;

import java.util.ArrayList;

public class AutList implements BuildingAutorisation {

    private final ArrayList<BuildingAutorisation> buildingAutorisations = new ArrayList<>();

    @Override
    public boolean isAutorise(Owner owner) {
        for (BuildingAutorisation buildingAutorisation : buildingAutorisations) {
            if (buildingAutorisation.isAutorise(owner)) {
                return true;
            }
        }
        return false;
    }
}
