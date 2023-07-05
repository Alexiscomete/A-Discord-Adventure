package io.github.alexiscomete.lapinousecond.worlds.map.tiles.textures

import io.github.alexiscomete.lapinousecond.worlds.map.tiles.types.TILE_HEIGHT
import io.github.alexiscomete.lapinousecond.worlds.map.tiles.types.TILE_WIDTH
import java.awt.Color
import javax.imageio.ImageIO

enum class Textures(val path: String) {
    NULL("textures/tiles/null.png");

    val pixels: Array<Array<Color>> = Array(TILE_HEIGHT) {
        Array(TILE_WIDTH) {
            Color(0, 0, 0)
        }
    }

    init {
        val stream = Textures::class.java.classLoader.getResourceAsStream(path)
            ?: throw IllegalArgumentException("Texture $path not found")

        val bufferedImage = ImageIO.read(stream)

        if (bufferedImage.width < TILE_WIDTH || bufferedImage.height < TILE_HEIGHT) {
            throw IllegalArgumentException("Texture $path is not 16x16")
        }

        for (x in 0 until TILE_WIDTH) {
            for (y in 0 until TILE_HEIGHT) {
                pixels[y][x] = Color(bufferedImage.getRGB(x, y))
            }
        }
    }
}