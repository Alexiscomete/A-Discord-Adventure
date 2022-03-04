package io.github.alexiscomete.lapinousecond.worlds.buildings.autorisations;

import io.github.alexiscomete.lapinousecond.entity.Owner;

@FunctionalInterface
public interface BuildingAutorisation {
    boolean isAutorise(Owner owner);
}
