package io.github.alexiscomete.lapinousecond.worlds.buildings;

import io.github.alexiscomete.lapinousecond.save.CacheGetSet;
import io.github.alexiscomete.lapinousecond.save.Tables;

public abstract class Building extends CacheGetSet {

    String type, name, description;

    public Building(long id, String type, String name, String description) {
        super(id, Tables.BUILDINGS.getTable());
        this.type = type;
        this.name = name;
        this.description = description;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();

        return stringBuilder.toString();
    }
}
