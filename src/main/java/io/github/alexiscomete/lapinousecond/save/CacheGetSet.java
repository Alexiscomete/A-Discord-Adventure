package io.github.alexiscomete.lapinousecond.save;

import io.github.alexiscomete.lapinousecond.Main;

import java.util.HashMap;

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
}
