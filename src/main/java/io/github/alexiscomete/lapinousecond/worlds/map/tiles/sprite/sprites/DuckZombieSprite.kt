package io.github.alexiscomete.lapinousecond.worlds.map.tiles.sprite.sprites

import io.github.alexiscomete.lapinousecond.worlds.map.tiles.textures.TexturesForSprites
import io.github.alexiscomete.lapinousecond.worlds.map.tiles.textures.mirrorImage
import io.github.alexiscomete.lapinousecond.worlds.map.tiles.types.Tile
import java.awt.Color
import java.awt.image.BufferedImage

class DuckZombieSprite(override var tile: Tile) : BaseMonsterSprite() {
    override fun color(): Color {
        return Color(170, 255, 0)
    }

    override fun letter(): Char {
        return 'Z'
    }

    override fun texture(): BufferedImage {
        if ((1..2).random() == 1) {
            return TexturesForSprites.DUCK_ZOMBIE.image.mirrorImage()
        }
        return TexturesForSprites.DUCK_ZOMBIE.image
    }
}