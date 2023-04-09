package io.github.alexiscomete.lapinousecond.worlds.map.tiles

import io.github.alexiscomete.lapinousecond.worlds.WorldManager
import io.github.alexiscomete.lapinousecond.worlds.Zooms
import io.github.alexiscomete.lapinousecond.worlds.map.tiles.multitiles.MultiTilesManager
import io.github.alexiscomete.lapinousecond.worlds.map.tiles.render.WorldCanvas
import io.github.alexiscomete.lapinousecond.worlds.map.tiles.types.BaseTileGroup
import io.github.alexiscomete.lapinousecond.worlds.map.tiles.types.MapTile
import io.github.alexiscomete.lapinousecond.worlds.map.tiles.types.TreeTrunk

class WorldRenderScene(
    val canvas: WorldCanvas,
    x: Int,
    y: Int,
    private val zoomLevel: Zooms,
    val world: WorldManager
) {

    //private val spritesManagers = mutableListOf<InteractionSpriteManager>()
    private val multiTilesManagers = mutableListOf<MultiTilesManager>()

    var dicoTiles = mutableMapOf<Pair<Int, Int>, Tile>()
        private set
    private var currentTile = getOrGenerateTileAt(x, y)

    fun renderAll() {
        canvas.resetCanvas(Pair(51, 51))
        currentTile.render(this, 25, 25)
        val toDelete = mutableListOf<Tile>()
        for (tile in dicoTiles.values) {
            if (tile is BaseTileGroup) {
                if (tile.currentState == 0) {
                    toDelete.add(tile)
                } else {
                    tile.resetRecursive()
                }
            } else if (tile is MapTile) {
                if (tile.currentState == 0) {
                    toDelete.add(tile)
                } else {
                    tile.resetRender()
                }
            }
        }
        toDelete.forEach { it.delete(this) }
        //spritesManagers.forEach { it.getAllElements().forEach { sprite -> sprite.drawOn(image) } }
        //thread {
        //    spritesManagers.forEach { it.updateAfter() }
        //}
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
            if ((0..20).random() == 0 && world.getHeight(
                    x,
                    y,
                    zoomLevel
                ) > 0.5 && zoomLevel == Zooms.ZOOM_IN && world.pathLevel(x.toDouble(), y.toDouble()) > 0.8
            ) {
                TreeTrunk(x, y, 2)
            } else {
                if (zoomLevel == Zooms.ZOOM_IN) {
                    MapTile(x, y, world.getHeight(x, y, zoomLevel), world.isPath(x.toDouble(), y.toDouble()))
                } else {
                    MapTile(x, y, world.getHeight(x, y, zoomLevel))
                }
            }
        }
    }
}