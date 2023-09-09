package io.github.alexiscomete.lapinousecond.worlds.map.tiles.textures

import io.github.alexiscomete.lapinousecond.worlds.map.tiles.types.TILE_HEIGHT
import io.github.alexiscomete.lapinousecond.worlds.map.tiles.types.TILE_WIDTH
import java.awt.Color
import java.awt.image.BufferedImage
import javax.imageio.ImageIO

enum class TexturesForSprites(val path: String) {
    NULL("textures/tiles/null.png"),
    PLAYER_V1("textures/sprites/playerV1.png"),
    CHEST("textures/sprites/chest.png"),
    CHEST_CLOSED("textures/sprites/chest_closed.png"),
    CHEST_OPEN("textures/sprites/chestopen.png"),
    CHEST_OPENED("textures/sprites/chest_opened.png"),
    DUCK("textures/sprites/duck.png"),
    SLIME("textures/sprites/slime.png"),
    SLIME_V2("textures/sprites/slime_green.png"),
    DUCK_ZOMBIE("textures/sprites/duckZ.png"),
    SLIME_V2_DESATURE("textures/sprites/slime_desature.png"),
    ATTACK_V1("textures/sprites/attackV1.png"),;

    val image: BufferedImage = run {
        val stream = TexturesForSprites::class.java.classLoader.getResourceAsStream(path)
            ?: throw IllegalArgumentException("Texture $path not found")

        val bufferedImage = ImageIO.read(stream)

        if (bufferedImage.width != TILE_WIDTH || bufferedImage.height != TILE_HEIGHT) {
            println("WARNING -- Texture $path is not ${TILE_WIDTH}x${TILE_HEIGHT} -- PLEASE FIX IT")
        }

        stream.close()

        bufferedImage
    }

    private val filters = HashMap<Triple<Color, Double, Double>, BufferedImage>()

    fun colorFilterFor(color: Color, intensity: Double, opacity: Double = 0.5): BufferedImage {
        val key = Triple(color, intensity, opacity)
        if (filters.containsKey(key)) {
            return filters[key]!!
        }

        val filteredImage = BufferedImage(image.width, image.height, BufferedImage.TYPE_INT_ARGB)
        for (x in 0 until image.width) {
            for (y in 0 until image.height) {
                val originalColor = image.getRGB(x, y)
                val oldRed = originalColor shr 16 and 0xFF
                val oldGreen = originalColor shr 8 and 0xFF
                val oldBlue = originalColor and 0xFF

                val newColor = Color(
                    ((oldRed + color.red * intensity * opacity) / (1 + opacity)).toInt().coerceIn(0, 255),
                    ((oldGreen + color.green * intensity * opacity) / (1 + opacity)).toInt().coerceIn(0, 255),
                    ((oldBlue + color.blue * intensity * opacity) / (1 + opacity)).toInt().coerceIn(0, 255),
                    originalColor shr 24 and 0xFF
                )

                filteredImage.setRGB(x, y, newColor.rgb)
            }
        }

        filters[key] = filteredImage
        return filteredImage
    }
}
