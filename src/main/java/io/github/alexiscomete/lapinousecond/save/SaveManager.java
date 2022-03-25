package io.github.alexiscomete.lapinousecond.save;

import io.github.alexiscomete.lapinousecond.UserPerms;
import io.github.alexiscomete.lapinousecond.entity.Company;
import io.github.alexiscomete.lapinousecond.entity.Player;
import io.github.alexiscomete.lapinousecond.worlds.Place;
import io.github.alexiscomete.lapinousecond.worlds.ServerBot;
import io.github.alexiscomete.lapinousecond.worlds.buildings.Building;
import io.github.alexiscomete.lapinousecond.worlds.buildings.Buildings;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;

public class SaveManager {

    public final CacheCustom<Player> players = new CacheCustom<>(Tables.PLAYERS.getTable(), Player::new);
    public final CacheCustom<ServerBot> servers = new CacheCustom<>(Tables.SERVERS.getTable(), ServerBot::new);
    public final CacheCustom<Place> places = new CacheCustom<>(Tables.PLACES.getTable(), Place::new);
    public final CacheCustom<Building> buildings = new CacheCustom<>(Tables.BUILDINGS.getTable(), aLong -> Buildings.load(String.valueOf(aLong)));
    public final CacheCustom<Company> companies = new CacheCustom<>(Tables.COMPANY.getTable(), Company::new);

    private final String path;

    private Connection co = null;
    private Statement st = null;

    public SaveManager(String path) {
        this.path = path;
        connection();
    }

    public void connection() {
        try {
            Class.forName("org.sqlite.JDBC");
            co = DriverManager.getConnection(path);
            st = co.createStatement();
        } catch (SQLException | ClassNotFoundException throwable) {
            throwable.printStackTrace();
            if (co != null) {
                try {
                    co.close();
                } catch (SQLException ignore) {
                }
            }
        }
    }

    public String getBuildingType(long id) {
        try {
            ResultSet resultSet = st.executeQuery("SELECT type FROM " + Tables.BUILDINGS.getTable().getName() + " WHERE id = " + id);
            if (resultSet.next()) {
                return resultSet.getString("type");
            }
            resultSet.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return null;
    }

    public ArrayList<Long> getTravels() {
        try {
            ResultSet resultSet = st.executeQuery("SELECT id FROM guilds ORDER BY RAND() LIMIT 6");
            ArrayList<Long> longs = new ArrayList<>();
            while (resultSet.next()) {
                longs.add(Long.valueOf(resultSet.getString("id")));
            }
            resultSet.close();
            return longs;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return new ArrayList<>();
        }
    }

    public UserPerms getPlayerPerms(long id) {
        try {
            ResultSet resultSet = st.executeQuery("SELECT * FROM perms WHERE id = " + id);
            if (resultSet.next()) {
                return new UserPerms(toBoolean(resultSet.getInt("play")), toBoolean(resultSet.getInt("create_server")), toBoolean(resultSet.getInt("manage_perms")), toBoolean(resultSet.getInt("manage_roles")), false);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return new UserPerms(true, false, false, false, true);
    }

    public static boolean toBoolean(int s) {
        return s == 1;
    }

    public static String toBooleanString(boolean b) {
        return b ? "1" : "0";
    }

    public void setValue(String where, String which, String whichValue, String valueName, String value) {
        try {
            st.executeUpdate("UPDATE " + where + " SET " + valueName + " = '" + value + "' WHERE " + which + " = " + whichValue);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public void setValue(Table table, long id, String row, String value) {
        setValue(table.getName(), "id", String.valueOf(id), row, value);
    }

    public void setValue(Table table, long id, String row, String value, String type) {
        execute("ALTER TABLE " + table.getName() + " ADD COLUMN " + row + " " + type, false);
        setValue(table, id, row, value);
    }

    public void insert(String where, HashMap<String, String> what) {
        StringBuilder values = new StringBuilder("("), keys = new StringBuilder("(");
        for (int i = 0; i < what.size(); i++) {
            String key = (String) what.keySet().toArray()[i];
            keys.append(key);
            values.append(what.get(key));
            if (i != what.size() - 1) {
                keys.append(", ");
                values.append(", ");
            }
        }
        values.append(")");
        keys.append(")");
        try {
            st.executeUpdate("INSERT INTO " + where + " " + keys + " VALUES " + values);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void execute(String ex) {
        execute(ex, true);
    }

    public void execute(String ex, boolean bo) {
        try {
            st.executeUpdate(ex);
        } catch (SQLException e) {
            if (bo) {
                e.printStackTrace();
            }
        }
    }

    public ResultSet executeQuery(String ex, boolean bo) {
        try {
            return st.executeQuery(ex);
        } catch (SQLException e) {
            if (bo) {
                e.printStackTrace();
            }
            return null;
        }
    }

    public String getString(Table table, String row, String type, long id) {
        execute("ALTER TABLE " + table.getName() + " ADD COLUMN " + row + " " + type, false);
        ResultSet resultSet;
        String str = "";
        try {
            resultSet = st.executeQuery("SELECT " + row + " FROM " + table.getName() + " WHERE id=" + id);
            str = resultSet.getString(row);
            resultSet.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return str;
    }
}
