package io.github.alexiscomete.lapinousecond.worlds;

import io.github.alexiscomete.lapinousecond.Main;
import io.github.alexiscomete.lapinousecond.save.*;

public class ServerBot extends CacheGetSet {

    private final SaveManager sv = Main.getSaveManager();

    public long getId() {
        return id;
    }

    public ServerBot(long id) {
        super(id, Tables.SERVERS.getTable());
    }

    public String testValueAndSet(int len, String message, String prog_name) {
        if (message.length() < len) {
            set(prog_name, message);
            return "";
        } else {
            return "Impossible : trop de caractères : " + message.length() + ", nombre autorisé : " + len;
        }
    }
}
