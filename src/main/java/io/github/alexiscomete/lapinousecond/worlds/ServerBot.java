package io.github.alexiscomete.lapinousecond.worlds;

import io.github.alexiscomete.lapinousecond.Main;
import io.github.alexiscomete.lapinousecond.save.SaveManager;
import io.github.alexiscomete.lapinousecond.save.Tables;

import java.util.ArrayList;

public class ServerBot {

    private int x, y, z;
    private final long id;
    private String description, name, in, out;
    private ArrayList<Long> travel;
    private final SaveManager sv = Main.getSaveManager();

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
        sv.setValue(Tables.SERVERS.getTable(), id, "y", String.valueOf(y));
    }

    public int getZ() {
        return z;
    }

    public void setZ(int z) {
        this.z = z;
        sv.setValue(Tables.SERVERS.getTable(), id, "z", String.valueOf(z));
    }

    public long getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
        sv.setValue(Tables.SERVERS.getTable(), id, "descr", description);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        sv.setValue(Tables.SERVERS.getTable(), id, "namerp", name);
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

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
        sv.setValue(Tables.SERVERS.getTable(), id, "x", String.valueOf(x));
    }

    public String getIn() {
        return in;
    }

    public void setIn(String in) { // train = travel in
        this.in = in;
        sv.setValue(Tables.SERVERS.getTable(), id, "train", in);
    }

    public String getOut() {
        return out;
    }

    public void setOut(String out) {
        this.out = out;
        sv.setValue(Tables.SERVERS.getTable(), id, "traout", out);
    }

    public ServerBot(int x, int y, int z, long id, String description, String name, ArrayList<Long> travel, String in, String out) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.id = id;
        this.description = description;
        this.name = name;
        this.travel = travel;
        this.in = in;
        this.out = out;
    }
}
