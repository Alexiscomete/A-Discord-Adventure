package io.github.alexiscomete.lapinousecond.worlds.map.tiles.multitiles.templating.templates

import io.github.alexiscomete.lapinousecond.worlds.map.tiles.multitiles.MultiTilesManager
import io.github.alexiscomete.lapinousecond.worlds.map.tiles.multitiles.templating.contexts.TemplateWorld
import io.github.alexiscomete.lapinousecond.worlds.map.tiles.multitiles.templating.tiles.TemplatedTile

interface Template {

    /**
     * Return a multi tile manager. Then you can use it to get a position
     *
     * @param templateWorld a world to fill blank
     * @return the manager
     */
    fun generate(templateWorld: TemplateWorld, minX: Int, minY: Int) : MultiTilesManager
}