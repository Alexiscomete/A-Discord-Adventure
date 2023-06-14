package io.github.alexiscomete.lapinousecond.worlds.map.tiles.multitiles.templating.templates

import io.github.alexiscomete.lapinousecond.worlds.map.tiles.multitiles.MultiTilesManager
import io.github.alexiscomete.lapinousecond.worlds.map.tiles.multitiles.templating.tiles.TemplatedTile

interface TemplatePart {
    fun getTile(x: Int, y: Int, multiTilesManager: MultiTilesManager): TemplatedTile
}