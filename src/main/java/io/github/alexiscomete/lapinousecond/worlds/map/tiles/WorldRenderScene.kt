package io.github.alexiscomete.lapinousecond.worlds.map.tiles

import io.github.alexiscomete.lapinousecond.worlds.WorldManager
import io.github.alexiscomete.lapinousecond.worlds.Zooms
import io.github.alexiscomete.lapinousecond.worlds.map.tiles.multitiles.MultiTilesManager
import io.github.alexiscomete.lapinousecond.worlds.map.tiles.render.WorldCanvas
import io.github.alexiscomete.lapinousecond.worlds.map.tiles.sprite.InteractionSpriteManager
import kotlin.concurrent.thread

class WorldRenderScene(
    val canvas: WorldCanvas,
    private var x: Int,
    private var y: Int,
    private val zoomLevel: Zooms,
    val world: WorldManager
) {

    private val spritesManagers = mutableListOf<InteractionSpriteManager>()
    private val multiTilesManagers = mutableListOf<MultiTilesManager>()

    var dicoTiles = mutableMapOf<Pair<Int, Int>, Tile>()
        private set
    private var currentTile = getOrGenerateTileAt(x, y)

    fun renderAll() {
        canvas.resetCanvas(Pair(61, 31))
        currentTile.render(this, 30, 15)
        //spritesManagers.forEach { it.getAllElements().forEach { sprite -> sprite.drawOn(image) } }
        thread {
            spritesManagers.forEach { it.updateAfter() }
        }
    }

    fun moveUp() {
        val next = currentTile.up ?: getOrGenerateTileAt(x, y - 1)
        if (next.isWalkable()) currentTile = next
    }

    fun moveDown() {
        val next = currentTile.down ?: getOrGenerateTileAt(x, y + 1)
        if (next.isWalkable()) currentTile = next
    }

    fun moveLeft() {
        val next = currentTile.left ?: getOrGenerateTileAt(x - 1, y)
        if (next.isWalkable()) currentTile = next
    }

    fun moveRight() {
        val next = currentTile.right ?: getOrGenerateTileAt(x + 1, y)
        if (next.isWalkable()) currentTile = next
    }

    fun getOrGenerateTileAt(x: Int, y: Int): Tile {
        var tile: Tile? = null
        multiTilesManagers.forEach { if (it.hasTileAt(x, y)) tile = it.baseTileAt(x, y) }
        if (tile != null) return tile!!
        return dicoTiles.getOrPut(Pair(x, y)) {
            if (zoomLevel == Zooms.ZOOM_IN) {
                val coos = zoomLevel.zoomOutTo(Zooms.ZOOM_OUT, x.toDouble(), y.toDouble())
                MapTile(x, y, world.getHeight(x, y, zoomLevel), world.isPath(coos.first, coos.second))
            } else {
                MapTile(x, y, world.getHeight(x, y, zoomLevel))
            }
        }
    }
}