package io.github.alexiscomete.lapinousecond.worlds.map.tiles.textures

import java.awt.Color
import javax.imageio.ImageIO

enum class Textures(val path: String) {
    NULL("textures/tiles/null.png");

    val pixels: Array<Array<Color>> = Array(16) {
        Array(16) {
            Color(0, 0, 0)
        }
    }

    init {
        val stream = Textures::class.java.classLoader.getResourceAsStream(path)
            ?: throw IllegalArgumentException("Texture $path not found")

        val bufferedImage = ImageIO.read(stream)

        if (bufferedImage.width < 16 || bufferedImage.height < 16) {
            throw IllegalArgumentException("Texture $path is not 16x16")
        }

        for (x in 0 until 16) {
            for (y in 0 until 16) {
                pixels[x][y] = Color(bufferedImage.getRGB(x, y))
            }
        }
    }
}