package io.github.alexiscomete.lapinousecond;

public class Player {

    long id;
    long bal;
    long server;
    short tuto;
    short security;

    public Player(long id, long bal, long server, short tuto, short security) {
        this.id = id;
        this.bal = bal;
        this.server = server;
        this.tuto = tuto;
        this.security = security;
    }
}
