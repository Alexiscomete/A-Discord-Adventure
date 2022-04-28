package io.github.alexiscomete.lapinousecond.worlds;

import io.github.alexiscomete.lapinousecond.worlds.map.Map;

import java.util.ArrayList;

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

    // génère aléatoirement une place pour les tests, minx = 0, maxx = voir dans Map.java, miny = 0, maxy = voir dans Map.java
    public static PlaceForTest generateRandomPlace() {

        // génération des coordonnées aléatoires
        int x = (int) (Math.random() * Map.MAP_HEIGHT);
        int y = (int) (Math.random() * Map.MAP_HEIGHT);

        // génération du nom aléatoire
        StringBuilder name = new StringBuilder();
        int nb = (int) (Math.random() * 10);
        for (int i = 0; i < nb; i++) {
            name.append((char) (Math.random() * 26 + 'a'));
        }

        // génération de l'id aléatoire
        long id = (long) (Math.random() * Long.MAX_VALUE);

        return new PlaceForTest(name.toString(), id, x, y);
    }

    // génération de n places aléatoires
    public static ArrayList<PlaceForTest> generateRandomPlaces(int n) {
        ArrayList<PlaceForTest> places = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            places.add(generateRandomPlace());
        }
        return places;
    }

}
