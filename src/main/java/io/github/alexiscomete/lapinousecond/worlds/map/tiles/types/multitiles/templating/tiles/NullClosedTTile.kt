package io.github.alexiscomete.lapinousecond.worlds.map.tiles.types.multitiles.templating.tiles

import io.github.alexiscomete.lapinousecond.worlds.map.tiles.types.multitiles.MultiTilesManager
import io.github.alexiscomete.lapinousecond.worlds.map.tiles.textures.Textures
import java.awt.Color

class NullClosedTTile(x: Int, y: Int, multiTilesManager: MultiTilesManager) : BaseClosedTemplatedTile(x, y,
    multiTilesManager
) {
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