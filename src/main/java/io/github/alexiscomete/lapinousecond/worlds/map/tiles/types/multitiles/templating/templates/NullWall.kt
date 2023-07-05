package io.github.alexiscomete.lapinousecond.worlds.map.tiles.types.multitiles.templating.templates

import io.github.alexiscomete.lapinousecond.worlds.map.tiles.types.multitiles.MultiTilesManager
import io.github.alexiscomete.lapinousecond.worlds.map.tiles.types.multitiles.templating.tiles.NullClosedTTile
import io.github.alexiscomete.lapinousecond.worlds.map.tiles.types.multitiles.templating.tiles.TemplatedTile

class NullWall : TemplatePart {
    override fun getTile(x: Int, y: Int, multiTilesManager: MultiTilesManager): TemplatedTile {
        return NullClosedTTile(x, y, multiTilesManager)
    }
}