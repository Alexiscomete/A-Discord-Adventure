package io.github.alexiscomete.lapinousecond.worlds.map.tiles

import io.github.alexiscomete.lapinousecond.worlds.Zooms
import io.github.alexiscomete.lapinousecond.worlds.map.tiles.multitiles.MultiTilesManager
import io.github.alexiscomete.lapinousecond.worlds.map.tiles.sprite.InteractionSpriteManager
import kotlin.concurrent.thread

class WorldRenderScene(
    val canvas: WorldCanvas,
    private var x: Int,
    private var y: Int,
    private val zoomLevel: Zooms
) {

    private val spritesManagers = mutableListOf<InteractionSpriteManager>()
    private val multiTilesManagers = mutableListOf<MultiTilesManager>()
    private var currentTile = getOrGenerateTileAt(x, y)

    fun renderAll() {
        canvas.resetCanvas()
        currentTile.render(this)
        //spritesManagers.forEach { it.getAllElements().forEach { sprite -> sprite.drawOn(image) } }
        thread {
            spritesManagers.forEach { it.updateAfter() }
        }
    }

    fun getOrGenerateTileAt(x: Int, y: Int): Tile {
        var tile: Tile? = null
        multiTilesManagers.forEach { if (it.hasTileAt(x, y)) tile = it.baseTileAt(x, y) }
        TODO()
    }
}