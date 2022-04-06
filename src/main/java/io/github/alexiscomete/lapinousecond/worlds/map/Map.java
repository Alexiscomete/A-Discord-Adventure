package io.github.alexiscomete.lapinousecond.worlds.map;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;

public class Map {
    private static BufferedImage map;
    public static final int MAP_WIDTH = 528;
    public static final int MAP_HEIGHT = 272;

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
        return map.getSubimage(x * map.getWidth(null) / MAP_WIDTH, y * map.getHeight(null) / MAP_HEIGHT, width * map.getWidth(null) / MAP_WIDTH, height * map.getHeight(null) / MAP_HEIGHT);
    }

    // zoom on coordinates (x, y) and return a BufferedImage
    public static BufferedImage zoom(int x, int y, int zoom) {
        return zoom(x-zoom*2, y-zoom, zoom*4, zoom*2);
    }

    //return a bigger BufferedImage and ask size
    public static BufferedImage bigger(BufferedImage image, int sizeMultiplier) {
        return toBufferedImage(image.getScaledInstance(image.getWidth() * sizeMultiplier, image.getHeight() * sizeMultiplier, BufferedImage.SCALE_SMOOTH));
    }

    /**
     * Converts a given Image into a BufferedImage
     * thanks to https://stackoverflow.com/questions/13605248/java-converting-image-to-bufferedimage
     * @param img The Image to be converted
     * @return The converted BufferedImage
     */
    public static BufferedImage toBufferedImage(Image img)
    {
        if (img instanceof BufferedImage)
        {
            return (BufferedImage) img;
        }

        // Create a buffered image with transparency
        BufferedImage bimage = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);

        // Draw the image on to the buffered image
        Graphics2D bGr = bimage.createGraphics();
        bGr.drawImage(img, 0, 0, null);
        bGr.dispose();

        // Return the buffered image
        return bimage;
    }
}
