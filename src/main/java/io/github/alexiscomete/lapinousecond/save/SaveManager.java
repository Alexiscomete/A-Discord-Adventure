package io.github.alexiscomete.lapinousecond.save;

import io.github.alexiscomete.lapinousecond.Player;
import io.github.alexiscomete.lapinousecond.UserPerms;
import io.github.alexiscomete.lapinousecond.worlds.Place;
import io.github.alexiscomete.lapinousecond.worlds.ServerBot;
import io.github.alexiscomete.lapinousecond.worlds.buildings.Building;
import io.github.alexiscomete.lapinousecond.worlds.buildings.Buildings;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;

public class SaveManager {

    private final String path;

    private final HashMap<Long, Player> players = new HashMap<>();

    public HashMap<Long, Player> getPlayers() {
        return players;
    }

    public HashMap<Long, ServerBot> getServers() {
        return servers;
    }

    private final HashMap<Long, ServerBot> servers = new HashMap<>();
    private final HashMap<Long, Place> places = new HashMap<>();

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

    public Player getPlayer(long l) {
        Player p = players.get(l);
        if (p == null) {
            try {
                ResultSet resultSet = st.executeQuery("SELECT * FROM players WHERE id = " + l);
                if (resultSet.next()) {
                    p = new Player(resultSet.getLong("id"), resultSet.getDouble("bal"), resultSet.getLong("serv"), resultSet.getShort("tuto"), resultSet.getBoolean("is_verify"), resultSet.getBoolean("has_account"), resultSet.getInt("x"), resultSet.getInt("y"), resultSet.getString("roles"), resultSet.getString("resources"));
                    players.put(l, p);
                }
                resultSet.close();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
        return p;

    }

    public ServerBot getServer(long l) {
        ServerBot serverBot = servers.get(l);
        if (serverBot == null) {
            try {
                ResultSet resultSet = st.executeQuery("SELECT * FROM guilds WHERE id = " + l);
                if (resultSet.next()) {
                    serverBot = new ServerBot(l);
                    servers.put(l, serverBot);
                }
                resultSet.close();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
        return serverBot;
    }

    public Place getPlace(long id) {
        Place p = places.get(id);
        if (p == null) {
            try {
                ResultSet resultSet = st.executeQuery("SELECT * FROM places WHERE id = " + id);
                if (resultSet.next()) {
                    p = new Place(id);
                    places.put(id, p);
                }
                resultSet.close();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
        return p;
    }

    public String getBuildingType(long id) {
        try {
            ResultSet resultSet = st.executeQuery("SELECT * FROM " + Tables.BUILDINGS.getTable().getName() + " WHERE id = " + id);
            if (resultSet.next()) {
                return new CacheGetSet(id, Tables.BUILDINGS.getTable()).getString("type");
            }
            resultSet.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return null;
    }

    public void addServer(long id) {
        try {
            st.executeUpdate("INSERT INTO guilds (id) VALUES (" + id + ")");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
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
