package io.github.alexiscomete.lapinousecond.worlds.map.tiles.types.multitiles.templating.templates

import io.github.alexiscomete.lapinousecond.worlds.map.tiles.types.multitiles.MultiTilesManager
import io.github.alexiscomete.lapinousecond.worlds.map.tiles.types.multitiles.templating.tiles.TemplatedTile

interface TemplatePart {
    fun getTile(x: Int, y: Int, multiTilesManager: MultiTilesManager): TemplatedTile
}