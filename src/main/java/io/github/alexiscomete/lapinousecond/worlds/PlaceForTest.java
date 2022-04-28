package io.github.alexiscomete.lapinousecond.worlds;

public class PlaceForTest extends Place{

    String name;
    int x, y;
    long id;

    public PlaceForTest(String name, long id, int x, int y) {
        this.name = name;
        this.id = id;
        this.x = x;
        this.y = y;
    }

    @Override
    public long getId() {
        return id;
    }

    @Override
    public String getString(String key) {
        switch (key) {
            case "name":
                return name;
            case "x":
                return String.valueOf(x);
            case "y":
                return String.valueOf(y);
            default:
                return "";
        }
    }
}
