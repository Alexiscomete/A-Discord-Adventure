package io.github.alexiscomete.lapinousecond;

public class Player {

    long id;
    long bal;
    long server;
    short tuto;
    short security;

    public long getWorkTime() {
        return workTime;
    }

    public void setWorkTime(long workTime) {
        this.workTime = workTime;
    }

    long workTime;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getBal() {
        return bal;
    }

    public void setBal(long bal) {
        this.bal = bal;
    }

    public long getServer() {
        return server;
    }

    public void setServer(long server) {
        this.server = server;
    }

    public short getTuto() {
        return tuto;
    }

    public void setTuto(short tuto) {
        this.tuto = tuto;
    }

    public short getSecurity() {
        return security;
    }

    public void setSecurity(short security) {
        this.security = security;
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
