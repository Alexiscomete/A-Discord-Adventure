package io.github.alexiscomete.lapinousecond;

import java.sql.SQLException;
import java.util.ArrayList;

public class Player {

    private final long id;
    private long bal;
    private long server;
    private short tuto;
    private short security;
    private long workTime;
    private final ArrayList<Item> items = new ArrayList<>();

    public long getWorkTime() {
        return workTime;
    }

    public void updateWorkTime() {
        this.workTime = System.currentTimeMillis();
    }

    public long getId() {
        return id;
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
        SaveManager.setValue("players", String.valueOf(this.id), "items", itemsList.toString());
    }

    public long getBal() {
        return bal;
    }

    public void setBal(long bal) {
        this.bal = bal;
        SaveManager.setValue("players", String.valueOf(id), "bal", String.valueOf(bal));
    }

    public long getServer() {
        return server;
    }

    public void setServer(long server) {
        this.server = server;
        SaveManager.setValue("players", String.valueOf(id), "serv", String.valueOf(server));
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
        SaveManager.setValue("players", String.valueOf(id), "sec", String.valueOf(security));
    }

    public Player(long id, long bal, long server, short tuto, short security) {
        this.id = id;
        this.bal = bal;
        this.server = server;
        this.tuto = tuto;
        this.security = security;
        this.workTime = 0;
    }
}
