package io.github.alexiscomete.lapinousecond.worlds;

/**
 * Une zone est un rectangle de pixels permettant de couper une région non rectangulaire en plusieurs zones pour la rendre plus facile à manipuler.
 * Exemple : savoir si un pixel est dans une zone.
 */
public class Zone {

    private int x1, y1;
    private int x2, y2;

    /**
     * Constructeur de la classe Zone.
     *
     * @param x1 x gauche (min)
     * @param y1 y haut (min)
     * @param x2 x droite (max)
     * @param y2 y bas (max)
     */
    public Zone(int x1, int y1, int x2, int y2) {
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
    }

    /**
     * Retourne si un pixel est dans la zone.
     *
     * @param x x du pixel
     * @param y y du pixel
     * @return true si le pixel est dans la zone, false sinon
     */
    public boolean contains(int x, int y) {
        return x >= x1 && x <= x2 && y >= y1 && y <= y2;
    }

    /**
     * Retourne la largeur de la zone.
     *
     * @return la largeur de la zone
     */
    public int getWidth() {
        return x2 - x1;
    }

    /**
     * Retourne la hauteur de la zone.
     *
     * @return la hauteur de la zone
     */
    public int getHeight() {
        return y2 - y1;
    }

    /**
     * Retourne la position x du coin en haut à gauche de la zone.
     *
     * @return la position x du coin en haut à gauche de la zone
     */
    public int getX1() {
        return x1;
    }

    /**
     * Retourne la position y du coin en haut à gauche de la zone.
     *
     * @return la position y du coin en haut à gauche de la zone
     */
    public int getY1() {
        return y1;
    }

    /**
     * Retourne la position x du coin en bas à droite de la zone.
     *
     * @return la position x du coin en bas à droite de la zone
     */
    public int getX2() {
        return x2;
    }

    /**
     * Retourne la position y du coin en bas à droite de la zone.
     *
     * @return la position y du coin en bas à droite de la zone
     */
    public int getY2() {
        return y2;
    }

    // setters

    /**
     * Change la position x du coin en haut à gauche de la zone.
     *
     * @param x1 permet de changer la position x du coin en haut à gauche de la zone
     */
    public void setX1(int x1) {
        this.x1 = x1;
    }

    /**
     * Change la position y du coin en haut à gauche de la zone.
     *
     * @param y1 permet de changer la position y du coin en haut à gauche de la zone
     */
    public void setY1(int y1) {
        this.y1 = y1;
    }

    /**
     * Change la position x du coin en bas à droite de la zone.
     *
     * @param x2 permet de changer la position x du coin en bas à droite de la zone
     */
    public void setX2(int x2) {
        this.x2 = x2;
    }

    /**
     * Change la position y du coin en bas à droite de la zone.
     *
     * @param y2 permet de changer la position y du coin en bas à droite de la zone
     */
    public void setY2(int y2) {
        this.y2 = y2;
    }

    // permet de récupérer la zone en string
    @Override
    public String toString() {
        return "{" + x1 + "," + y1 + "," + x2 + "," + y2 + "}";
    }

    /**
     * Permet de transformer un string en zone (pour la lecture de la base de données).
     * @param s string à transformer au format "{x1,y1,x2,y2}"
     * @return la zone correspondant au string
     */
    public static Zone fromString(String s) {
        String[] tab = s.substring(1, s.length() - 1).split(",");
        return new Zone(Integer.parseInt(tab[0]), Integer.parseInt(tab[1]), Integer.parseInt(tab[2]), Integer.parseInt(tab[3]));
    }
}
