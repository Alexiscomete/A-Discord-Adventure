package io.github.alexiscomete.lapinousecond;

public class Player {

    private long id;
    private long bal;
    private long server;
    private short tuto;
    private short security;
    private long workTime;

    public long getWorkTime() {
        return workTime;
    }

    public void setWorkTime(long workTime) {
        this.workTime = workTime;
    }

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

    public short getSecurity(short s) {
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
