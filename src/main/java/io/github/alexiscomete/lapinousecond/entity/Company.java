package io.github.alexiscomete.lapinousecond.entity;

import io.github.alexiscomete.lapinousecond.save.CacheGetSet;
import io.github.alexiscomete.lapinousecond.save.Tables;

public class Company extends CacheGetSet implements Owner {

    public Company(long id) {
        super(id, Tables.COMPANY.getTable());
    }

    @Override
    public String getOwnerType() {
        return "company";
    }

    @Override
    public String getOwnerString() {
        return String.valueOf(id);
    }
}
