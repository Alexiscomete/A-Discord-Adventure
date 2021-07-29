package io.github.alexiscomete.lapinousecond;

import java.sql.SQLException;
import java.util.ArrayList;

public class Player {

    private long id;
    private long bal;
    private long server;
    private short tuto;
    private short security;
    private long workTime;
    private final ArrayList<Item> items = new ArrayList<>();

    public long getWorkTime() {
        return workTime;
    }

    public void setWorkTime(long workTime) {
        this.workTime = workTime;
        try {
            SaveManager.st.executeUpdate("UPDATE players SET wt = " + workTime + " WHERE id = " + id);
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
            SaveManager.st.executeUpdate("UPDATE players SET id = " + id + " WHERE id = " + this.id);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public ArrayList<Item> getItems() {
        return items;
    }

    public void updateItems() {
        StringBuilder itemsList = new StringBuilder();
        for (int i = 0; i < items.size(); i++) {
            Item item = items.get(i);
            itemsList.append(item.jname);
            if (i != items.size()-1) {
                itemsList.append(";");
            }
        }
        try {
            SaveManager.st.executeUpdate("UPDATE players SET items = " + itemsList + " WHERE id = " + this.id);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public long getBal() {
        return bal;
    }

    public void setBal(long bal) {
        this.bal = bal;
        try {
            SaveManager.st.executeUpdate("UPDATE players SET bal = " + bal + " WHERE id = " + id);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public long getServer() {
        return server;
    }

    public void setServer(long server) {
        this.server = server;
        try {
            SaveManager.st.executeUpdate("UPDATE players SET serv = " + server + " WHERE id = " + id);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public short getTuto() {
        return tuto;
    }

    public void setTuto(short tuto) {
        this.tuto = tuto;
        try {
            SaveManager.st.executeUpdate("UPDATE players SET tuto = " + tuto + " WHERE id = " + id);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public short getSecurity() {
        return security;
    }

    public void setSecurity(short security) {
        this.security = security;
        try {
            SaveManager.st.executeUpdate("UPDATE players SET sec = " + security + " WHERE id = " + id);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public Player(long id, long bal, long server, short tuto, short security, long workTime) {
        this.id = id;
        this.bal = bal;
        this.server = server;
        this.tuto = tuto;
        this.security = security;
        this.workTime = workTime;
    }
}
