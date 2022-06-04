package io.github.alexiscomete.lapinousecond.worlds.buildings.autorisations;

import io.github.alexiscomete.lapinousecond.entity.Owner;

public class TypeAutorisation<U> implements BuildingAutorisation {

    final Class<U> uClass;

    public TypeAutorisation(Class<U> uClass) {
        this.uClass = uClass;
    }

    @Override
    public boolean isAutorise(Owner owner) {
        return uClass.isInstance(owner);
    }
}
