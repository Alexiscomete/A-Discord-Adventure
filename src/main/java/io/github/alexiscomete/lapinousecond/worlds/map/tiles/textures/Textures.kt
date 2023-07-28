package io.github.alexiscomete.lapinousecond.worlds.map.tiles.textures

import io.github.alexiscomete.lapinousecond.worlds.map.tiles.types.TILE_HEIGHT
import io.github.alexiscomete.lapinousecond.worlds.map.tiles.types.TILE_WIDTH
import java.awt.Color
import javax.imageio.ImageIO

enum class Textures(val path: String) {
    NULL("textures/tiles/null.png"),
    BASE_GRASS("textures/tiles/base_grass.png");

    val pixels: Array<Array<Color>> = Array(TILE_HEIGHT) {
        Array(TILE_WIDTH) {
            Color(0, 0, 0)
        }
    }

    init {
        val stream = Textures::class.java.classLoader.getResourceAsStream(path)
            ?: throw IllegalArgumentException("Texture $path not found")

        val bufferedImage = ImageIO.read(stream)

        if (bufferedImage.width != TILE_WIDTH || bufferedImage.height != TILE_HEIGHT) {
            println("WARNING -- Texture $path is not ${TILE_WIDTH}x${TILE_HEIGHT} -- PLEASE FIX IT")
        }

        for (x in 0 until TILE_WIDTH) {
            for (y in 0 until TILE_HEIGHT) {
                // si on dépasse de l'image, prendre un pixel noir
                try {
                    pixels[y][x] = Color(bufferedImage.getRGB(x, y))
                } catch (_: ArrayIndexOutOfBoundsException) {
                    println("WARNING again")
                }
            }
        }
    }

    private val filters = HashMap<Pair<Color, Double>, Array<Array<Color>>>()

    fun colorFilterFor(color: Color, intensity: Double): Array<Array<Color>> {
        val key = Pair(color, intensity)
        if (filters.containsKey(key)) {
            return filters[key]!!
        }

        val filteredPixels = Array(TILE_HEIGHT) { y ->
            Array(TILE_WIDTH) { x ->
                val originalColor = pixels[y][x]
                val red = (originalColor.red * color.red * intensity / 255).toInt().coerceIn(0, 255)
                val green = (originalColor.green * color.green * intensity / 255).toInt().coerceIn(0, 255)
                val blue = (originalColor.blue * color.blue * intensity / 255).toInt().coerceIn(0, 255)

                Color(red, green, blue)
            }
        }

        filters[key] = filteredPixels
        return filteredPixels
    }
}