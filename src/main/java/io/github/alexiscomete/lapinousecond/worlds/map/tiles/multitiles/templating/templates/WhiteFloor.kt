package io.github.alexiscomete.lapinousecond.worlds.map.tiles.multitiles.templating.templates

import io.github.alexiscomete.lapinousecond.worlds.map.tiles.multitiles.templating.tiles.TemplatedTile
import io.github.alexiscomete.lapinousecond.worlds.map.tiles.multitiles.templating.tiles.WhiteFloorOpenedTTile

class WhiteFloor : TemplatePart {
    override fun getTile(x: Int, y: Int): TemplatedTile {
        return WhiteFloorOpenedTTile(x, y)
    }
}