package io.github.alexiscomete.lapinousecond.worlds.map;

import io.github.alexiscomete.lapinousecond.worlds.Place;
import org.javacord.api.entity.channel.TextChannel;

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
        // charge la map à partir du fichier base.png inclus dans le jar
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
     * thanks to <a href="https://stackoverflow.com/questions/13605248/java-converting-image-to-bufferedimage">https://stackoverflow.com/questions/13605248/java-converting-image-to-bufferedimage</a>
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

    // return an image with the places' names on it
    public static void getMapWithNames(ArrayList<Place> places, int xStart, int yStart, int width, int height, BufferedImage image) {
        Graphics2D g = image.createGraphics();
        g.setColor(new Color(0, 255, 0));
        // set the text size
        g.setFont(new Font("Arial", Font.BOLD, image.getWidth() / 80));
        int size = image.getWidth() / 80;
        for (Place place : places) {
            if (place.getX().isPresent() && place.getY().isPresent()) {
                // coos on image (x, y) after resizing (x and y are not the same as the image's coos)
                int x = (place.getX().get() - xStart) * image.getWidth() / width;
                int y = (place.getY().get() - yStart) * image.getHeight() / height;
                // draw a point
                g.fillOval(x, y, (int) (size * 0.7), (int) (size * 0.7));
                // draw the name
                try {
                    g.drawString(place.getString("name"), (int) (x + 1.1 * size), y);
                } catch (Exception ignored) {

                }
                // draw the id
                try {
                    g.drawString(String.valueOf(place.getID()), (int) (x + 1.1 * size), (int) (y + 1.1 * size));
                } catch (Exception ignored) {

                }
            }
        }
    }

    // ------------------
    // PATH FINDING with A*
    // ------------------

    // return the path from one pixel to another
    public static ArrayList<Pixel> findPath(Node start, Node end, TextChannel channel) {

        // envoie un message d'attente
        channel.sendMessage("Calcul en cours...");

        if (start.isDirt() != end.isDirt()) {
            throw new IllegalArgumentException("start and end must be on the same type of tile");
        }

        ArrayList<Node> closedList = new ArrayList<>();
        ArrayList<Node> openList = new ArrayList<>();
        openList.add(start);

        while (!openList.isEmpty()) {
            Node current = openList.get(0);
            // je cherche le nœud avec la plus petite heuristique
            for (Node node : openList) {
                if (node.heuristic < current.heuristic) {
                    current = node;
                }
            }
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
            for (Node n :
                    getConnectedNodes(current, openList)) {
                if (!openList.contains(n)) {
                    n.setParent(current);
                }
                if (!(
                        (openList.contains(n) && n.heuristic < current.heuristic)
                        || closedList.contains(n))) {
                    n.cost = current.cost + 1;
                    n.heuristic = n.cost + distance(n, end);
                    n.setParent(current);
                    if (!openList.contains(n)) {
                        openList.add(n);
                    }
                }
            }
            closedList.add(current);
            if (openList.size() > 5000) {
                throw new IllegalArgumentException("Le chemin met trop de puissance à calculer, essayez en plusieurs fois");
            }
        }
        throw new IllegalArgumentException("Aucun chemin trouvé");
    }

    // return connected pixels
    public static ArrayList<Node> getConnectedNodes(Node pixel, ArrayList<Node> nodes) {
        ArrayList<Node> nodes2 = new ArrayList<>();
        // ajout des pixels voisins un par un
        if (isInMap(pixel.getX() -1, pixel.getY() -1)) nodes2.add(getNode(pixel.getX() - 1, pixel.getY() - 1, nodes));
        if (isInMap(pixel.getX(), pixel.getY() -1)) nodes2.add(getNode(pixel.getX(), pixel.getY() - 1, nodes));
        if (isInMap(pixel.getX() +1, pixel.getY() -1)) nodes2.add(getNode(pixel.getX() + 1, pixel.getY() - 1, nodes));
        if (isInMap(pixel.getX() -1, pixel.getY())) nodes2.add(getNode(pixel.getX() - 1, pixel.getY(), nodes));
        if (isInMap(pixel.getX() +1, pixel.getY())) nodes2.add(getNode(pixel.getX() + 1, pixel.getY(), nodes));
        if (isInMap(pixel.getX() -1, pixel.getY() +1)) nodes2.add(getNode(pixel.getX() - 1, pixel.getY() + 1, nodes));
        if (isInMap(pixel.getX(), pixel.getY() +1)) nodes2.add(getNode(pixel.getX(), pixel.getY() + 1, nodes));
        if (isInMap(pixel.getX() +1, pixel.getY() +1)) nodes2.add(getNode(pixel.getX() + 1, pixel.getY() + 1, nodes));
        // pour chaque pixel on l'enlève si ce n'est pas de le même type que le pixel courant
        nodes2.removeIf(n -> n.isDirt() != pixel.isDirt());
        return nodes2;
    }

    // vérifie si le pixel est dans la map
    public static boolean isInMap(int x, int y) {
        return x >= 0 && x < MAP_WIDTH && y >= 0 && y < MAP_HEIGHT;
    }

    public static Node getNode(int x, int y, ArrayList<Node> nodes) {
        Node n = new Node(x, y, MAP_WIDTH, MAP_HEIGHT, map, 0, 0);
        if (nodes.contains(n)) {
            return nodes.get(nodes.indexOf(n));
        }
        return n;
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
