package io.github.alexiscomete.lapinousecond;

import java.sql.SQLException;
import java.util.ArrayList;

public class ServerBot {

    private int x, y, z;
    private long id;
    private String description, name, in, out;
    private ArrayList<Long> travel;
    short sec;

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
        try {
            SaveManager.st.executeUpdate("UPDATE guilds SET y = " + y + " WHERE id = " + id);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public int getZ() {
        return z;
    }

    public void setZ(int z) {
        this.z = z;
        try {
            SaveManager.st.executeUpdate("UPDATE guilds SET z = " + z + " WHERE id = " + id);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
        try {
            SaveManager.st.executeUpdate("UPDATE guilds SET id = " + id + " WHERE id = " + id);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
        try {
            SaveManager.st.executeUpdate("UPDATE guilds SET descr = '" + description + "' WHERE id = " + id);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        try {
            SaveManager.st.executeUpdate("UPDATE guilds SET namerp = '" + name + "' WHERE id = " + id);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public ArrayList<Long> getTravel() {
        return travel;
    }

    public short getSec() {
        return sec;
    }

    public void setSec(short sec) {
        this.sec = sec;
        try {
            SaveManager.st.executeUpdate("UPDATE guilds SET sec = " + sec + " WHERE id = " + id);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public void setTravel(ArrayList<Long> travel) {
        this.travel = travel;
        StringBuilder answer = new StringBuilder();
        for (long l : travel) {
            answer.append(l);
            answer.append(";");
        }
        try {
            SaveManager.st.executeUpdate("UPDATE guilds SET travel = '" + answer + "' WHERE id = " + id);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
        try {
            SaveManager.st.executeUpdate("UPDATE guilds SET x = " + x + " WHERE id = " + id);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public String getIn() {
        return in;
    }

    public void setIn(String in) {
        this.in = in;
        try {
            SaveManager.st.executeUpdate("UPDATE guilds SET train = '" + in + "' WHERE id = " + id);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public String getOut() {
        return out;
    }

    public void setOut(String out) {
        this.out = out;
        try {
            SaveManager.st.executeUpdate("UPDATE guilds SET traout = '" + out + "' WHERE id = " + id);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public ServerBot(int x, int y, int z, long id, String description, String name, ArrayList<Long> travel, short sec, String in, String out) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.id = id;
        this.description = description;
        this.name = name;
        this.travel = travel;
        this.sec = sec;
        this.in = in;
        this.out = out;
    }
}
