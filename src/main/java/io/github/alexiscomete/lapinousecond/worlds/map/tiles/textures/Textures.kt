package io.github.alexiscomete.lapinousecond.worlds.map.tiles.textures

import io.github.alexiscomete.lapinousecond.worlds.map.tiles.PIXEL_HEIGHT
import io.github.alexiscomete.lapinousecond.worlds.map.tiles.PIXEL_WIDTH
import java.awt.Color
import javax.imageio.ImageIO

enum class Textures(val path: String) {
    NULL("textures/tiles/null.png");

    val pixels: Array<Array<Color>> = Array(PIXEL_HEIGHT) {
        Array(PIXEL_WIDTH) {
            Color(0, 0, 0)
        }
    }

    init {
        val stream = Textures::class.java.classLoader.getResourceAsStream(path)
            ?: throw IllegalArgumentException("Texture $path not found")

        val bufferedImage = ImageIO.read(stream)

        if (bufferedImage.width < PIXEL_WIDTH || bufferedImage.height < PIXEL_HEIGHT) {
            throw IllegalArgumentException("Texture $path is not 16x16")
        }

        for (x in 0 until PIXEL_WIDTH) {
            for (y in 0 until PIXEL_HEIGHT) {
                pixels[y][x] = Color(bufferedImage.getRGB(x, y))
            }
        }
    }
}