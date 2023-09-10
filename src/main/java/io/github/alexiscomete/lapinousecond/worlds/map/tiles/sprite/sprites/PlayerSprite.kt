package io.github.alexiscomete.lapinousecond.worlds.map.tiles.sprite.sprites

import io.github.alexiscomete.lapinousecond.worlds.map.tiles.render.canvas.WorldCanvas
import io.github.alexiscomete.lapinousecond.worlds.map.tiles.sprite.Sprite
import io.github.alexiscomete.lapinousecond.worlds.map.tiles.textures.TexturesForSprites
import io.github.alexiscomete.lapinousecond.worlds.map.tiles.types.Tile
import java.awt.Color
import java.awt.image.BufferedImage


class PlayerSprite(override var tile: Tile) : Sprite {

    override fun render(canvas: WorldCanvas, xToUse: Int, yToUse: Int, distance: Int) {
        canvas.drawSprite(this, xToUse, yToUse, 5)
    }

    override fun mustBeRemoved(): Boolean {
        return false
    }

    override fun color(): Color {
        return Color(255, 0, 0)
    }

    override fun letter(): Char {
        return 'P'
    }

    override fun texture(): BufferedImage {
        return TexturesForSprites.PLAYER_V1.image
    }
}
