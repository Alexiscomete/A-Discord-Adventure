package io.github.alexiscomete.lapinousecond.worlds.map.tiles.multitiles.templating.tiles

import io.github.alexiscomete.lapinousecond.worlds.map.tiles.textures.TexturesInCode
import java.awt.Color

class WhiteWallClosedTTile(x: Int, y: Int) : BaseClosedTemplatedTile(x, y) {
    override fun letter(): Char {
        return 'W'
    }

    override fun color(): Color {
        return Color(255, 255, 255)
    }

    override fun texture(): Array<Array<Color>> {
        return TexturesInCode.WHITE.texture
    }
}