package io.github.alexiscomete.lapinousecond.worlds.buildings;

import io.github.alexiscomete.lapinousecond.save.CacheGetSet;
import io.github.alexiscomete.lapinousecond.save.Tables;

public abstract class BuildingInteraction extends CacheGetSet {

    public BuildingInteraction(long id) {
        super(id, Tables.BUILDINGS.getTable());
    }

    abstract void configBuilding();
}
