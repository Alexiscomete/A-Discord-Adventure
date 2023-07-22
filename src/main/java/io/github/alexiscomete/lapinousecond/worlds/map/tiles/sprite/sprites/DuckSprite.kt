package io.github.alexiscomete.lapinousecond.worlds.map.tiles.sprite.sprites

import io.github.alexiscomete.lapinousecond.worlds.map.tiles.render.canvas.WorldCanvas
import io.github.alexiscomete.lapinousecond.worlds.map.tiles.sprite.SpriteWithIA
import io.github.alexiscomete.lapinousecond.worlds.map.tiles.textures.TexturesForSprites
import io.github.alexiscomete.lapinousecond.worlds.map.tiles.types.Tile
import java.awt.Color
import java.awt.image.BufferedImage

class DuckSprite(override var tile: Tile) : SpriteWithIA {
    override fun useIA() {
        TODO("Not yet implemented")
    }

    override fun render(canvas: WorldCanvas, xToUse: Int, yToUse: Int, distance: Int) {
        canvas.drawSprite(this, xToUse, yToUse, 5)
    }

    override fun isRendered(): Boolean {
        return tile.isRendered()
    }

    override fun color(): Color {
        return Color(255, 219, 0)
    }

    override fun letter(): Char {
        return 'D'
    }

    override fun texture(): BufferedImage {
        return TexturesForSprites.DUCK.image
    }

}
