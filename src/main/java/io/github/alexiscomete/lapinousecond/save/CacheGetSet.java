package io.github.alexiscomete.lapinousecond.save;

import io.github.alexiscomete.lapinousecond.Main;

import java.util.HashMap;
import java.util.Objects;

public class CacheGetSet {
    private final HashMap<String, CacheValue> cache = new HashMap<>();
    protected final long id;
    private final Table table;

    public CacheGetSet(long id, Table table) {
        this.id = id;
        this.table = table;
    }

    public String getString(String row) {
        if (cache.containsKey(row)) {
            return cache.get(row).getString();
        } else {
            String str = Main.getSaveManager().getString(table, row, "TEXT", id);
            cache.put(row, new CacheValue(str));
            return str;
        }
    }

    public void set(String row, String value) {
        if (cache.containsKey(row)) {
            cache.get(row).set(value);
        } else {
            cache.put(row, new CacheValue(value));
        }
        Main.getSaveManager().setValue(table, id, row, value, "TEXT");
    }

    public String[] getArray(String row) {
        String str = getString(row);
        if (str == null) {
            str = "";
        }
        return str.split(";");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CacheGetSet that = (CacheGetSet) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public long getId() {
        return id;
    }
}
