package io.github.alexiscomete.lapinousecond.worlds.buildings.autorisations;

import io.github.alexiscomete.lapinousecond.entity.Owner;

public class AllAutorisation implements BuildingAutorisation {
    @Override
    public boolean isAutorise(Owner owner) {
        return true;
    }
}