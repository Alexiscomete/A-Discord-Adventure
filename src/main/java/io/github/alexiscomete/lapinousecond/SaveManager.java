package io.github.alexiscomete.lapinousecond;

import io.github.alexiscomete.lapinousecond.worlds.ServerBot;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;

public class SaveManager {

    public static String path = "";
    public static String user = "";
    public static String mdp = "";

    public static HashMap<Long, Player> players = new HashMap<>();
    public static HashMap<Long, ServerBot> servers = new HashMap<>();

    static Connection co = null;
    public static Statement st = null;

    public static void connection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            co = DriverManager.getConnection(path, user, mdp);
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

    public static Player getPlayer(long l) {
        Player p = players.get(l);
        if (p == null) {
            try {
                ResultSet resultSet = st.executeQuery("SELECT * FROM players WHERE id = " + l);
                if (resultSet.next()) {
                    p = new Player(Long.parseLong(resultSet.getString("id")), Long.parseLong(resultSet.getString("bal")), Long.parseLong(resultSet.getString("serv")), Short.parseShort(resultSet.getString("tuto")), Short.parseShort(resultSet.getString("sec")));
                    players.put(l, p);
                }
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
        return p;

    }

    public static ServerBot getServer(long l) {
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
                    serverBot = new ServerBot(Integer.parseInt(resultSet.getString("x")), Integer.parseInt(resultSet.getString("y")), Integer.parseInt(resultSet.getString("z")), Long.parseLong(resultSet.getString("id")), resultSet.getString("descr"), resultSet.getString("namerp"), arr, Short.parseShort(resultSet.getString("sec")), resultSet.getString("train"), resultSet.getString("traout"));
                    servers.put(l, serverBot);
                }
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
        return serverBot;
    }

    public static void addPlayer(long id, long bal, long server, short tuto, short security, long workTime) {
        try {
            st.executeUpdate("INSERT INTO players (id, bal, serv, tuto, sec, wt) VALUES (" + id + ", " + bal + ", " + server + ", " + tuto + ", " + security + ", " + workTime + ")");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public static void addServer(int x, int y, int z, long id, String description, String name, String travel, short sec) {
        try {
            st.executeUpdate("INSERT INTO guilds (x, y, z, id, descr, namerp, travel, sec) VALUES (" + x + ", " + y + ", " + z + ", " + id + ", '" + description + "', '" + name + "', '" + travel + "', " + sec + ")");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public static ArrayList<Long> getTravels() {
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

    public static UserPerms getPlayerPerms(long id) {
        try {
            ResultSet resultSet = st.executeQuery("SELECT * FROM perms WHERE id = " + id);
            if (resultSet.next()) {
                return new UserPerms(toBoolean(resultSet.getInt("PLAY")), toBoolean(resultSet.getInt("CREATE_SERVER")), toBoolean(resultSet.getInt("SET_SERVER_SEC")), toBoolean(resultSet.getInt("MANAGE_PERMS")), false);
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

    public static void setValue(String what, String which, String whichValue, String valueName, String value) {
        try {
            st.executeUpdate("UPDATE " + what + " SET " + valueName + " = " + value + " WHERE " + which + " = " + whichValue);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public static void setValue(String what, String id, String valueName, String value) {
        setValue(what, "id", id, valueName, value);
        try {
            st.executeUpdate("UPDATE " + what + " SET " + valueName + " = " + value + " WHERE id = " + id);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
}
