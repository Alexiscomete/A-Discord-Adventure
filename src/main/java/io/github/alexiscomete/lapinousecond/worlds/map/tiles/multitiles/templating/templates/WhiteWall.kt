package io.github.alexiscomete.lapinousecond.worlds.map.tiles.multitiles.templating.templates

import io.github.alexiscomete.lapinousecond.worlds.map.tiles.multitiles.MultiTilesManager
import io.github.alexiscomete.lapinousecond.worlds.map.tiles.multitiles.templating.tiles.TemplatedTile
import io.github.alexiscomete.lapinousecond.worlds.map.tiles.multitiles.templating.tiles.WhiteWallClosedTTile

class WhiteWall : TemplatePart {
    override fun getTile(x: Int, y: Int, multiTilesManager: MultiTilesManager): TemplatedTile {
        return WhiteWallClosedTTile(x, y, multiTilesManager)
    }
}
