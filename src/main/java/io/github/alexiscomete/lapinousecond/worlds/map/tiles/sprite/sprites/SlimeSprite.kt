package io.github.alexiscomete.lapinousecond.worlds.map.tiles.sprite.sprites

import io.github.alexiscomete.lapinousecond.worlds.map.tiles.textures.TexturesForSprites
import io.github.alexiscomete.lapinousecond.worlds.map.tiles.textures.mirrorImage
import io.github.alexiscomete.lapinousecond.worlds.map.tiles.textures.mirrorImageVertically
import io.github.alexiscomete.lapinousecond.worlds.map.tiles.types.Tile
import java.awt.Color
import java.awt.image.BufferedImage
import java.time.LocalDate

class SlimeSprite(override var tile: Tile) : BaseMonsterSprite() {

    private val day = LocalDate.now().dayOfMonth

    override fun color(): Color {
        return Color(0, 255, 0)
    }

    override fun letter(): Char {
        return 'S'
    }

    override fun texture(): BufferedImage {
        if (day == 30 && (1..50).random() == 1) {
            if ((1..2).random() == 1) return TexturesForSprites.SLIME_V2.image.mirrorImage().mirrorImageVertically()
            return TexturesForSprites.SLIME_V2.image.mirrorImage()
        }
        if ((1..2).random() == 1) return TexturesForSprites.SLIME_V2.image.mirrorImageVertically()
        return TexturesForSprites.SLIME_V2.image
    }
}