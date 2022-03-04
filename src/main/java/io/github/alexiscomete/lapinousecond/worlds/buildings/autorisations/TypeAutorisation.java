package io.github.alexiscomete.lapinousecond.worlds.buildings.autorisations;

import io.github.alexiscomete.lapinousecond.entity.Owner;

public class TypeAutorisation<U> implements BuildingAutorisation {


    @Override
    public boolean isAutorise(Owner owner) {
        if (owner instanceof U) {
            return true;
        }
        return false;
    }
}
