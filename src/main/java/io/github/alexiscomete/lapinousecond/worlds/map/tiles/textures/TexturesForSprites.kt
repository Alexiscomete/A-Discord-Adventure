package io.github.alexiscomete.lapinousecond.worlds.map.tiles.textures

import io.github.alexiscomete.lapinousecond.worlds.map.tiles.types.TILE_HEIGHT
import io.github.alexiscomete.lapinousecond.worlds.map.tiles.types.TILE_WIDTH
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
    DUCK_ZOMBIE("textures/sprites/duckZ.png"),;

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
}