package io.github.alexiscomete.lapinousecond.worlds.map;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
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

    // ------------------
    // PATH FINDING with A*
    // ------------------

    // return the path from one pixel to another
    public static ArrayList<Pixel> findPath(Node start, Node end) {
        ArrayList<Node> closedList = new ArrayList<>();
        ArrayList<Node> openList = new ArrayList<>();
        openList.add(start);

        while (!openList.isEmpty()) {
            openList.sort(Node::compareTo);
            Node current = openList.get(0);
            if (current.equals(end)) {
                ArrayList<Pixel> path = new ArrayList<>();
                while (current.getParent() != null) {
                    path.add(current);
                    current = current.getParent();
                }
                Collections.reverse(path);
                return path;
            }
            openList.remove(current);
            getConnectedNodes(current);
            for (Node n :
                    getConnectedNodes(current)) {
                if (!openList.contains(n)) {
                    n.setParent(current);
                }
                if (!((openList.contains(n) && openList.get(openList.indexOf(n)).heuristic < current.heuristic) || closedList.contains(n))) {
                    n.cost = current.cost + 1;
                    n.heuristic = n.cost + distance(n, end);
                    n.setParent(current);
                    openList.add(n);
                }
            }
            closedList.add(current);
        }
        return null;
    }

    // return connected pixels
    public static ArrayList<Node> getConnectedNodes(Node pixel) {
        ArrayList<Node> nodes = new ArrayList<>();
        for (int x = pixel.getX() - 1; x <= pixel.getX() + 1; x++) {
            for (int y = pixel.getY() - 1; y <= pixel.getY() + 1; y++) {
                if (x >= 0 && x < MAP_WIDTH && y >= 0 && y < MAP_HEIGHT) {
                    Node p = getNode(x, y);
                    if (p.isDirt() == pixel.isDirt()) {
                        nodes.add(p);
                    }
                }
            }
        }
        return nodes;
    }

    public static Node getNode(int x, int y) {
        return new Node(x, y, MAP_WIDTH, MAP_HEIGHT, map, 0, 0);
    }

    // distance between two pixels
    public static double distance(Pixel a, Pixel b) {
        return Math.sqrt(Math.pow(a.getX() - b.getX(), 2) + Math.pow(a.getY() - b.getY(), 2));
    }

    public static BufferedImage drawPath(ArrayList<Pixel> path) {
        BufferedImage img = new BufferedImage(map.getWidth(), map.getHeight(), BufferedImage.TYPE_INT_ARGB);
        img.setData(map.getData());
        for (Pixel p :
                path) {
            img.setRGB(p.getXImage(), p.getYImage(), Color.RED.getRGB());
        }
        return img;
    }
}
