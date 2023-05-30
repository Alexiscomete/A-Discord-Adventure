package io.github.alexiscomete.lapinousecond.worlds.map.tiles.multitiles.templating.contexts

import io.github.alexiscomete.lapinousecond.worlds.map.tiles.multitiles.templating.templates.TemplatePart
import io.github.alexiscomete.lapinousecond.worlds.map.tiles.multitiles.templating.tiles.TemplatedTile

enum class TemplateWorld(
    private val templatePart: TemplatePart
) {
    ;

    fun getTile() : TemplatedTile {
        return templatePart.getTile(0, 0)
    }
}