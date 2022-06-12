package io.github.alexiscomete.lapinousecond.worlds

/**
 * Une zone est un rectangle de pixels permettant de couper une région non rectangulaire en plusieurs zones pour la rendre plus facile à manipuler.
 * Exemple : savoir si un pixel est dans une zone.
 * @param x1 x gauche (min)
 * @param y1 y haut (min)
 * @param x2 x droite (max)
 * @param y2 y bas (max)
 */
class Zone(var x1: Int, var y1: Int, var x2: Int, var y2: Int) {

    /**
     * Retourne si un pixel est dans la zone.
     *
     * @param x x du pixel
     * @param y y du pixel
     * @return true si le pixel est dans la zone, false sinon
     */
    fun contains(x: Int, y: Int): Boolean {
        return x in x1..x2 && y in y1..y2
    }

    /**
     * Retourne la largeur de la zone.
     *
     * @return la largeur de la zone
     */
    val width: Int
        get() = x2 - x1

    /**
     * Retourne la hauteur de la zone.
     *
     * @return la hauteur de la zone
     */
    val height: Int
        get() = y2 - y1

    // setters
    // permet de récupérer la zone en string
    override fun toString(): String {
        return "{$x1,$y1,$x2,$y2}"
    }

    companion object {
        /**
         * Permet de transformer un string en zone (pour la lecture de la base de données).
         * @param s string à transformer au format "{x1,y1,x2,y2}"
         * @return la zone correspondant au string
         */
        @JvmStatic
        fun fromString(s: String): Zone {
            val tab = s.substring(1, s.length - 1).split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            return Zone(tab[0].toInt(), tab[1].toInt(), tab[2].toInt(), tab[3].toInt())
        }
    }
}