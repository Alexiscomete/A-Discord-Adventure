package io.github.alexiscomete.lapinousecond.worlds.map;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;

public class Map {
    private static BufferedImage map;
    public static final int MAP_WIDTH = 510;
    public static final int MAP_HEIGHT = 350;

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
        return new Pixel(x, y, MAP_WIDTH, MAP_HEIGHT, map);
    }

    // if pixel is Dirt
    public static boolean isDirt(int x, int y) {
        return getPixel(x, y).isDirt();
    }

    // zoom and return a BufferedImage
    public static BufferedImage zoom(int x, int y, int width, int height) {
        return map.getSubimage(x * map.getWidth(null) / MAP_WIDTH, y * map.getHeight(null) / MAP_HEIGHT, width, height);
    }

    // zoom on coordinates (x, y) and return a BufferedImage
    public static BufferedImage zoom(int x, int y, int zoom) {
        return zoom(x-zoom*2, y-zoom, zoom*4, zoom*2);
    }
}
