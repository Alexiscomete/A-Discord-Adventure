package io.github.alexiscomete.lapinousecond;

import java.util.HashMap;

public class SaveManager {

    public static final String path = "zzz";
    public static final String user = "root";
    static final String mdp = "eqfq";

    public static HashMap<Long, Player> players = new HashMap<>();
    public static HashMap<Long, ServerBot> servers = new HashMap<>();

    public static Player getPlayer(long l) {
        Player p = players.get(l);
        if (p == null) {
            System.out.println("new player");
        }
        return p;

    }

    public static ServerBot getServer(long l) {
        ServerBot serverBot = servers.get(l);
        if (serverBot == null) {
            System.out.println("new Server");
        }
        return serverBot;
    }
}
