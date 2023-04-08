package io.github.alexiscomete.lapinousecond.worlds.map.tiles

import io.github.alexiscomete.lapinousecond.worlds.WorldManager
import io.github.alexiscomete.lapinousecond.worlds.Zooms
import io.github.alexiscomete.lapinousecond.worlds.map.tiles.multitiles.MultiTilesManager
import io.github.alexiscomete.lapinousecond.worlds.map.tiles.render.WorldCanvas
import io.github.alexiscomete.lapinousecond.worlds.map.tiles.sprite.InteractionSpriteManager
import io.github.alexiscomete.lapinousecond.worlds.map.tiles.types.MapTile
import kotlin.concurrent.thread

class WorldRenderScene(
    val canvas: WorldCanvas,
    x: Int,
    y: Int,
    private val zoomLevel: Zooms,
    val world: WorldManager
) {

    private val spritesManagers = mutableListOf<InteractionSpriteManager>()
    private val multiTilesManagers = mutableListOf<MultiTilesManager>()

    var dicoTiles = mutableMapOf<Pair<Int, Int>, Tile>()
        private set
    private var currentTile = getOrGenerateTileAt(x, y)

    fun renderAll() {
        canvas.resetCanvas(Pair(31, 31))
        currentTile.render(this, 15, 15)
        //spritesManagers.forEach { it.getAllElements().forEach { sprite -> sprite.drawOn(image) } }
        thread {
            spritesManagers.forEach { it.updateAfter() }
        }
    }

    fun moveUp() {
        val next = currentTile.up ?: getOrGenerateTileAt(currentTile.x, currentTile.y - 1)
        if (next.isWalkable()) currentTile = next
    }

    fun moveDown() {
        val next = currentTile.down ?: getOrGenerateTileAt(currentTile.x, currentTile.y + 1)
        if (next.isWalkable()) currentTile = next
    }

    fun moveLeft() {
        val next = currentTile.left ?: getOrGenerateTileAt(currentTile.x - 1, currentTile.y)
        if (next.isWalkable()) currentTile = next
    }

    fun moveRight() {
        val next = currentTile.right ?: getOrGenerateTileAt(currentTile.x + 1, currentTile.y)
        if (next.isWalkable()) currentTile = next
    }

    fun getOrGenerateTileAt(x: Int, y: Int): Tile {
        var tile: Tile? = null
        multiTilesManagers.forEach { if (it.hasTileAt(x, y)) tile = it.baseTileAt(x, y) }
        if (tile != null) return tile!!
        return dicoTiles.getOrPut(Pair(x, y)) {
            if (zoomLevel == Zooms.ZOOM_IN) {
                MapTile(x, y, world.getHeight(x, y, zoomLevel), world.isPath(x.toDouble(), y.toDouble()))
            } else {
                MapTile(x, y, world.getHeight(x, y, zoomLevel))
            }
        }
    }
}