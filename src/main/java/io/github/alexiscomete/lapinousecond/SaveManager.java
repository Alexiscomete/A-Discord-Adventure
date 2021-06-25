package io.github.alexiscomete.lapinousecond;

import java.sql.*;
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
                    p = new Player(Long.parseLong(resultSet.getString("id")), Long.parseLong(resultSet.getString("bal")), Long.parseLong(resultSet.getString("serv")), Short.parseShort(resultSet.getString("tuto")), Short.parseShort(resultSet.getString("sec")), Long.parseLong(resultSet.getString("wt")));
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
                ResultSet resultSet = st.executeQuery("SELECT * FROM servers WHERE id = " + l);
                if (resultSet.next()) {
                    String[] str = resultSet.getString("travel").split(";");
                    long[] arr = new long[str.length];
                    for (int i = 0; i < str.length; i++) {
                        arr[i] = Long.parseLong(str[i]);
                    }
                    serverBot = new ServerBot(Integer.parseInt(resultSet.getString("x")), Integer.parseInt(resultSet.getString("y")), Integer.parseInt(resultSet.getString("z")), Long.parseLong(resultSet.getString("id")), resultSet.getString("desc"), resultSet.getString("name"), arr);
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
}
