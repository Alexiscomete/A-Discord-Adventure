package io.github.alexiscomete.lapinousecond;

public class ServerBot {

    private int x, y, z;
    private long id;
    private String description, name;
    private long[] travel;

    public ServerBot(int x, int y, int z, long id, String description, String name, long[] travel) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.id = id;
        this.description = description;
        this.name = name;
        this.travel = travel;
    }
}
