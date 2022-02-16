package io.github.alexiscomete.lapinousecond.save;

import io.github.alexiscomete.lapinousecond.Main;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.function.Function;

public class CacheCustom<U> {

    private final HashMap<Long, U> hashMap = new HashMap<>();
    private final Table table;
    private final Function<Long, U> function;


    public CacheCustom(Table table, Function<Long, U> function) {
        this.table = table;
        this.function = function;
    }

    public HashMap<Long, U> getHashMap() {
        return hashMap;
    }

    public U get(long l) {
        U u = hashMap.get(l);
        if (u == null) {
            try {
                ResultSet resultSet = Main.getSaveManager().executeQuery("SELECT * FROM " + table.getName() + " WHERE id = " + l, true);
                if (resultSet.next()) {
                    u = function.apply(l);
                    hashMap.put(l, u);
                }
                resultSet.close();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
        return u;
    }

    public void add(long id) {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("id", String.valueOf(id));
        Main.getSaveManager().insert(table.getName(), hashMap);
    }
}
