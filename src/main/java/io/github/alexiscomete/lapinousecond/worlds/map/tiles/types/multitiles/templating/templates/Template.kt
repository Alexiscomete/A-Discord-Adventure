package io.github.alexiscomete.lapinousecond.worlds.map.tiles.types.multitiles.templating.templates

import io.github.alexiscomete.lapinousecond.worlds.map.tiles.types.multitiles.MultiTilesManager
import io.github.alexiscomete.lapinousecond.worlds.map.tiles.types.multitiles.templating.contexts.TemplateWorld

interface Template {

    /**
     * Return a multi tile manager. Then you can use it to get a position
     *
     * @param templateWorld a world to fill blank
     * @return the manager
     */
    fun generate(templateWorld: TemplateWorld, minX: Int, minY: Int) : MultiTilesManager
}