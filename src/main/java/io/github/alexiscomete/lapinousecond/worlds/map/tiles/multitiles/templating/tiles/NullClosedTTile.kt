package io.github.alexiscomete.lapinousecond.worlds.map.tiles.multitiles.templating.tiles

import io.github.alexiscomete.lapinousecond.worlds.map.tiles.textures.Textures
import java.awt.Color

class NullClosedTTile(x: Int, y: Int) : BaseClosedTemplatedTile(x, y) {
    override fun letter(): Char {
        return 'N'
    }

    override fun color(): Color {
        return Color(255, 0, 0)
    }

    override fun texture(): Array<Array<Color>> {
        return Textures.NULL.pixels
    }
}