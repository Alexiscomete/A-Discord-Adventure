package io.github.alexiscomete.lapinousecond.worlds.buildings;

import io.github.alexiscomete.lapinousecond.save.CacheGetSet;
import io.github.alexiscomete.lapinousecond.save.Tables;

public abstract class Building extends CacheGetSet {

    public Building(long id) {
        super(id, Tables.BUILDINGS.getTable());
    }

}
