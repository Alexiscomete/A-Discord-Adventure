package io.github.alexiscomete.lapinousecond.worlds.map.tiles.textures

import io.github.alexiscomete.lapinousecond.worlds.map.tiles.types.TILE_HEIGHT
import io.github.alexiscomete.lapinousecond.worlds.map.tiles.types.TILE_WIDTH
import java.awt.image.BufferedImage
import javax.imageio.ImageIO

enum class TexturesForSprites(val path: String) {
    NULL("textures/tiles/null.png"),
    PLAYER_V1("textures/sprites/playerV1.png"),
    CHEST("textures/sprites/chest.png"),
    CHEST_OPEN("textures/sprites/chestopen.png");

    val image: BufferedImage = run {
        val stream = TexturesForSprites::class.java.classLoader.getResourceAsStream(path)
            ?: throw IllegalArgumentException("Texture $path not found")

        val bufferedImage = ImageIO.read(stream)

        if (bufferedImage.width != TILE_WIDTH || bufferedImage.height != TILE_HEIGHT) {
            throw IllegalArgumentException("Texture $path is not 16x16")
        }

        stream.close()

        bufferedImage
    }
}