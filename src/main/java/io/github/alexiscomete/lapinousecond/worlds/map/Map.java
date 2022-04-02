package io.github.alexiscomete.lapinousecond.worlds.map;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;

public class Map {
    private static BufferedImage map;

    static {
        // charge la map à partir du fichier base.png inclu dans le jar
        try {
            map = ImageIO.read(Objects.requireNonNull(Map.class.getClassLoader().getResourceAsStream("base.png")));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static BufferedImage getMap() {
        return map;
    }

    // pixel at (x, y) with Pixel
    public static Pixel getPixel(int x, int y) {
        return new Pixel(x, y, map.getWidth(), map.getHeight(), map);
    }

    // if pixel is Dirt
    public static boolean isDirt(int x, int y) {
        return getPixel(x, y).isDirt();
    }
}
