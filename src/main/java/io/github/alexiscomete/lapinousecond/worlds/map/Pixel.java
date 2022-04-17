package io.github.alexiscomete.lapinousecond.worlds.map;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Objects;

public class Pixel {
    private final int x, xImage, y, yImage;
    private final int xMax, yMax;
    private final Color color;
    private final boolean isDirt;

    public Pixel(int x, int y, int xMax, int yMax, BufferedImage image) {
        this.x = x;
        this.y = y;
        this.yMax = yMax;
        this.xMax = xMax;
        this.xImage = x * image.getWidth(null) / xMax;
        this.yImage = y * image.getHeight(null) / yMax;
        // permet de récupérer la couleur du pixel sur l'image
        this.color = new Color(image.getRGB(xImage, yImage));
        // permet de savoir si le pixel est un sol ou non
        this.isDirt = color.getBlue() <= (int) ((float) color.getRed() + (float) color.getGreen()) / 1.5;
    }

    public boolean isDirt() {
        return isDirt;
    }

    public Color getColor() {
        return color;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getXImage() {
        return xImage;
    }

    public int getYImage() {
        return yImage;
    }

    public int getXMax() {
        return xMax;
    }

    public int getYMax() {
        return yMax;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Pixel pixel = (Pixel) o;
        return x == pixel.x && y == pixel.y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    @Override
    public String toString() {
        return "Pixel{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }
}
