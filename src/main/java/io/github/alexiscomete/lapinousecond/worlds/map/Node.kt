package io.github.alexiscomete.lapinousecond.worlds.map;

import java.awt.image.BufferedImage;

public class Node extends Pixel {

    double cost, heuristic;
    Node parent = null;

    public Node(int x, int y, int xMax, int yMax, BufferedImage image, double cost, double heuristic) {
        super(x, y, xMax, yMax, image);
        this.cost = cost;
        this.heuristic = heuristic;
    }

    public int compareTo(Object o) {
        Node node = (Node) o;
        return Double.compare(this.heuristic, node.heuristic);
    }


    public void setParent(Node current) {
        this.parent = current;
    }

    public Node getParent() {
        return parent;
    }
}
