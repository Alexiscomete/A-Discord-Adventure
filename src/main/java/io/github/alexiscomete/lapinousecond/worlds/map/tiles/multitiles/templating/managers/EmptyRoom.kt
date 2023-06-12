package io.github.alexiscomete.lapinousecond.worlds.map.tiles.multitiles.templating.managers

import io.github.alexiscomete.lapinousecond.worlds.map.tiles.multitiles.ComplexTile
import io.github.alexiscomete.lapinousecond.worlds.map.tiles.multitiles.MultiTilesManager
import io.github.alexiscomete.lapinousecond.worlds.map.tiles.multitiles.templating.contexts.TemplateWorld

class EmptyRoom(
    val size: Int,
    val templateWorld: TemplateWorld
) : MultiTilesManager {

    override fun load() {
        TODO("Not yet implemented")
    }

    override fun baseTileAt(x: Int, y: Int): ComplexTile {
        TODO("Not yet implemented")
    }

    override fun hasTileAt(x: Int, y: Int): Boolean {
        TODO("Not yet implemented")
    }

    override fun canBeRemoved(): Boolean {
        TODO("Not yet implemented")
    }

    override fun removeAllTiles() {
        TODO("Not yet implemented")
    }

    override fun iAmLoaded() {
        TODO("Not yet implemented")
    }

    override fun delete() {
        TODO("Not yet implemented")
    }

    override fun unload() {
        TODO("Not yet implemented")
    }
}