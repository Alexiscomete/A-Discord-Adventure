package io.github.alexiscomete.lapinousecond.worlds.map.tiles.sprite.sprites

import io.github.alexiscomete.lapinousecond.worlds.map.tiles.render.canvas.WorldCanvas
import io.github.alexiscomete.lapinousecond.worlds.map.tiles.sprite.Sprite

abstract class BaseSprite : Sprite {
    override fun render(canvas: WorldCanvas, xToUse: Int, yToUse: Int, distance: Int) {
        canvas.drawSprite(this, xToUse, yToUse, 5)
    }

    override fun mustBeRemoved(): Boolean {
        return false
    }
}
