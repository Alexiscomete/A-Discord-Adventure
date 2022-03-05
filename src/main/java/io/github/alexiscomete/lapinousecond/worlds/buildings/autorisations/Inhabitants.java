package io.github.alexiscomete.lapinousecond.worlds.buildings.autorisations;

import io.github.alexiscomete.lapinousecond.entity.Owner;

public class Inhabitants implements BuildingAutorisation {
    @Override
    public boolean isAutorise(Owner owner) {
        return false;
    }
}
