package io.github.alexiscomete.lapinousecond.worlds.map.tiles.sprite.sprites

import io.github.alexiscomete.lapinousecond.worlds.map.tiles.textures.TexturesForSprites
import io.github.alexiscomete.lapinousecond.worlds.map.tiles.textures.mirrorImage
import io.github.alexiscomete.lapinousecond.worlds.map.tiles.types.Tile
import java.awt.Color
import java.awt.image.BufferedImage

/**
 * Sprite for the yellow slime. The yellow slime attack the player with a special capacity.
 */
class YellowSlimeSprite(override var tile: Tile) : BaseMonsterSprite() {

    private val baseImage = TexturesForSprites.SLIME_V2.colorFilterFor(Color(200, 255, 0), 0.5)

    override fun color(): Color {
        return Color(200, 255, 0)
    }

    override fun letter(): Char {
        return 'S'
    }

    override fun texture(): BufferedImage {
        if ((1..2).random() == 1) return baseImage.mirrorImage()
        return baseImage
    }
}
