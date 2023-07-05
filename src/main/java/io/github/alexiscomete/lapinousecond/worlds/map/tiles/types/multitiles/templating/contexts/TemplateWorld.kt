package io.github.alexiscomete.lapinousecond.worlds.map.tiles.types.multitiles.templating.contexts

import io.github.alexiscomete.lapinousecond.worlds.map.tiles.types.multitiles.MultiTilesManager
import io.github.alexiscomete.lapinousecond.worlds.map.tiles.types.multitiles.templating.templates.NullWall
import io.github.alexiscomete.lapinousecond.worlds.map.tiles.types.multitiles.templating.templates.TemplatePart
import io.github.alexiscomete.lapinousecond.worlds.map.tiles.types.multitiles.templating.templates.WhiteWall
import io.github.alexiscomete.lapinousecond.worlds.map.tiles.types.multitiles.templating.tiles.TemplatedTile

/**
 * Permet de remplir le vide des templates à l'aide d'un patterne
 */
enum class TemplateWorld(
    private val templatePart: TemplatePart
) {
    NULL_WARNING(NullWall()),
    WHITE(WhiteWall());

    fun getTile(multiTilesManager: MultiTilesManager) : TemplatedTile {
        return templatePart.getTile(0, 0, multiTilesManager)
    }
}