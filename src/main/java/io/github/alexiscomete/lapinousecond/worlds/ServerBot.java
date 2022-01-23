package io.github.alexiscomete.lapinousecond.worlds;

import io.github.alexiscomete.lapinousecond.Main;
import io.github.alexiscomete.lapinousecond.save.CacheValue;
import io.github.alexiscomete.lapinousecond.save.SaveManager;
import io.github.alexiscomete.lapinousecond.save.Tables;

import java.util.HashMap;

public class ServerBot {

    private final long id;
    private final SaveManager sv = Main.getSaveManager();
    private final HashMap<String, CacheValue> cache = new HashMap<>();

    public long getId() {
        return id;
    }

    public ServerBot(long id) {
        this.id = id;
    }

    public String getString(String row) {
        if (cache.containsKey(row)) {
            return cache.get(row).getString();
        } else {
            String str = sv.getString(Tables.SERVERS.getTable(), row, "TEXT", id);
            cache.put(row, new CacheValue(str));
            return str;
        }
    }

    public String[] getArray(String row) {
        String str = getString(row);
        return str.split(";");
    }

    public void set(String row, String value) {
        if (cache.containsKey(row)) {
            cache.get(row).set(value);
        } else {
            cache.put(row, new CacheValue(value));
        }
        sv.setValue(Tables.SERVERS.getTable(), id, row, value, "TEXT");
    }
}
