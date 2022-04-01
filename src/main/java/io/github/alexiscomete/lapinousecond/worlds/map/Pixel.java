package io.github.alexiscomete.lapinousecond.worlds.map;

import java.awt.*;
import java.awt.image.BufferedImage;

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
        this.isDirt = color.getBlue() <= color.getRed() + color.getGreen();
    }

    public boolean isDirt() {
        return isDirt;
    }

    public Color getColor() {
        return color;
    }
}
