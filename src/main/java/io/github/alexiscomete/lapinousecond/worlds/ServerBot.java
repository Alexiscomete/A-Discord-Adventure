package io.github.alexiscomete.lapinousecond.worlds;

import io.github.alexiscomete.lapinousecond.Main;
import io.github.alexiscomete.lapinousecond.save.CacheValue;
import io.github.alexiscomete.lapinousecond.save.SaveManager;
import io.github.alexiscomete.lapinousecond.save.Tables;

import java.util.ArrayList;
import java.util.HashMap;

public class ServerBot {

    private final long id;
    private ArrayList<Long> travel = new ArrayList<>();
    private final SaveManager sv = Main.getSaveManager();
    private final HashMap<String, CacheValue> cache = new HashMap<>();

    public long getId() {
        return id;
    }

    public ArrayList<Long> getTravel() {
        return travel;
    }

    public void setTravel(ArrayList<Long> travel) {
        this.travel = travel;
        StringBuilder answer = new StringBuilder();
        for (long l : travel) {
            answer.append(l);
            answer.append(";");
        }
        sv.setValue(Tables.SERVERS.getTable(), id, "travel", answer.toString());
    }

    public ServerBot(long id) {
        this.id = id;
    }

    public String getString(String row) {
        if (cache.containsKey(row)) {
            return cache.get(row).getString();
        } else {
            String str = Main.getSaveManager().getString(Tables.SERVERS.getTable(), row, "TEXT", id);
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
        Main.getSaveManager().setValue(Tables.SERVERS.getTable(), id, row, value, "TEXT");
    }
}
