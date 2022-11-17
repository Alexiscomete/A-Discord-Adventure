package io.github.alexiscomete.lapinousecond.worlds.map

import java.awt.Color
import java.awt.image.BufferedImage
import java.util.*

open class Pixel(val x: Int, val y: Int, xMax: Int, yMax: Int, image: BufferedImage) {
    val isDirt: Boolean

    init {
        val xImage = x * image.getWidth(null) / xMax
        val yImage = y * image.getHeight(null) / yMax
        // permet de récupérer la couleur du pixel sur l'image
        val color = Color(image.getRGB(xImage, yImage))
        // permet de savoir si le pixel est un sol ou non
        isDirt = color.blue <= (color.red.toFloat() + color.green.toFloat()).toInt() / 1.5
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        val pixel = other as Pixel
        return x == pixel.x && y == pixel.y
    }

    override fun hashCode(): Int {
        return Objects.hash(x, y)
    }

    override fun toString(): String {
        return "Pixel{x=${x}, y=${y}}"
    }
}