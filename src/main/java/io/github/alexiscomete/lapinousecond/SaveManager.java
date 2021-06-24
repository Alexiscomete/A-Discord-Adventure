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
    static Statement st = null;

    public static void connection() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
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
                    serverBot = new ServerBot();
                    servers.put(l, serverBot);
                }
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
        return serverBot;
    }
}
