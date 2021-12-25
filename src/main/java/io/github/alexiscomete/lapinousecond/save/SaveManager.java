package io.github.alexiscomete.lapinousecond.save;

import io.github.alexiscomete.lapinousecond.Player;
import io.github.alexiscomete.lapinousecond.UserPerms;
import io.github.alexiscomete.lapinousecond.worlds.ServerBot;

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
                    p = new Player(resultSet.getLong("id"), resultSet.getLong("bal"), resultSet.getLong("serv"), resultSet.getShort("tuto"), resultSet.getBoolean("is_verify"), resultSet.getBoolean("has_account"), resultSet.getInt("x"), resultSet.getInt("y"));
                    players.put(l, p);
                }
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
                    String[] str = resultSet.getString("travel").split(";");
                    ArrayList<Long> arr = new ArrayList<>();
                    for (String s : str) {
                        try {
                            arr.add(Long.parseLong(s));
                        } catch (NumberFormatException ignored) {

                        }
                    }
                    serverBot = new ServerBot(Integer.parseInt(resultSet.getString("x")), Integer.parseInt(resultSet.getString("y")), Integer.parseInt(resultSet.getString("z")), Long.parseLong(resultSet.getString("id")), resultSet.getString("descr"), resultSet.getString("namerp"), arr, resultSet.getString("train"), resultSet.getString("traout"));
                    servers.put(l, serverBot);
                }
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
        return serverBot;
    }

    public void addServer(int x, int y, int z, long id, String description, String name, String travel, short sec) {
        try {
            st.executeUpdate("INSERT INTO guilds (x, y, z, id, descr, namerp, travel, sec) VALUES (" + x + ", " + y + ", " + z + ", " + id + ", '" + description + "', '" + name + "', '" + travel + "', " + sec + ")");
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
                return new UserPerms(toBoolean(resultSet.getInt("PLAY")), toBoolean(resultSet.getInt("CREATE_SERVER")), toBoolean(resultSet.getInt("MANAGE_PERMS")), false);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return new UserPerms(true, false, false, true);
    }

    public static boolean toBoolean(int s) {
        return s == 1;
    }

    public static String toBooleanString(boolean b) {
        return b ? "1" : "0";
    }

    private void setValue(String where, String which, String whichValue, String valueName, String value) {
        try {
            st.executeUpdate("UPDATE " + where + " SET " + valueName + " = '" + value + "' WHERE " + which + " = " + whichValue);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public void setValue(Table table, TableRow row1, String value1, TableRow row2, String value2) {
        setValue(table.getName(), row1.getName(), value1, row2.getName(), value2);
    }

    public void setValue(Table table, long id, String row, String value) {
        setValue(table.getName(), "id", String.valueOf(id), row, value);
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

}
