package io.github.alexiscomete.lapinousecond.worlds.map.tiles

import io.github.alexiscomete.lapinousecond.worlds.WorldManager
import io.github.alexiscomete.lapinousecond.worlds.Zooms
import io.github.alexiscomete.lapinousecond.worlds.map.tiles.multitiles.MultiTilesManager
import io.github.alexiscomete.lapinousecond.worlds.map.tiles.multitiles.templating.contexts.TemplateWorld
import io.github.alexiscomete.lapinousecond.worlds.map.tiles.multitiles.templating.managers.EmptyRoom
import io.github.alexiscomete.lapinousecond.worlds.map.tiles.render.WorldCanvas
import io.github.alexiscomete.lapinousecond.worlds.map.tiles.types.MapTile
import io.github.alexiscomete.lapinousecond.worlds.map.tiles.types.TreeTrunk
import java.util.*

class WorldRenderScene(
    val canvas: WorldCanvas, x: Int, y: Int, private val zoomLevel: Zooms, val world: WorldManager
) {
    val size = 21
    val xReset = (size / 2 + 1)
    val yReset = (size / 2 + 1)

    // permet de d'aller de plus en plus loin
    var renderQueue: Queue<RenderInfos> = LinkedList()
        private set

    private val multiTilesManagers = mutableListOf<MultiTilesManager>()

    var dicoTiles = mutableMapOf<Pair<Int, Int>, Tile>()
        private set
    private var currentTile = getOrGenerateTileAt(x, y)

    fun renderAll() {
        canvas.resetCanvas(size, size)
        currentTile.render(this, xReset, yReset, 0)
        while (!renderQueue.isEmpty()) {
            val tile = renderQueue.poll()
            tile.render(this)
        }
        val toDelete = mutableListOf<Tile>()
        for (tile in dicoTiles.values) {
            if (tile.isRendered()) {
                tile.resetRender()
            } else {
                toDelete.add(tile)
            }
        }
        toDelete.forEach { it.delete(this) }
        val toDelete2 = mutableListOf<MultiTilesManager>()
        multiTilesManagers.forEach {
            if (it.canBeRemoved()) {
                it.delete(this)
                toDelete2.add(it)
            }
            it.resetIAmLoaded()
        }
        toDelete2.forEach { multiTilesManagers.remove(it) }
    }

    // ATTENTION : les cases vides d'une pièce nous sortent des pièces

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

    var isShowed: Boolean = false

    fun getOrGenerateTileAt(x: Int, y: Int): Tile {
        var tile: Tile? = null
        multiTilesManagers.forEach { if (it.hasTileAt(x, y)) tile = it.baseTileAt(x, y) }
        if (tile != null) return tile!!
        return dicoTiles.getOrPut(Pair(x, y)) {
            if (zoomLevel == Zooms.ZOOM_IN) {
                when ((0..30).random()) {
                    0 -> {
                        if (
                            world.getHeight(x, y, zoomLevel) > 0.5
                            && world.pathLevel(x.toDouble(), y.toDouble()) > 0.8
                            && world.riverLevel(x.toDouble(), y.toDouble()) > 0.8
                        ) {
                            return TreeTrunk(x, y, 2)
                        }
                    }

                    1 -> {
                        if (!isShowed) {
                            val manager = EmptyRoom(6, TemplateWorld.WHITE, x, y)
                            multiTilesManagers.add(manager)
                            manager.load()
                            isShowed = true
                            return manager.baseTileAt(x, y)
                        }
                    }
                }
                MapTile(
                    x,
                    y,
                    world.getHeight(x, y, zoomLevel),
                    world.isPath(x.toDouble(), y.toDouble()),
                    world.isRiver(x.toDouble(), y.toDouble())
                )
            } else {
                MapTile(x, y, world.getHeight(x, y, zoomLevel))
            }
        }
    }
}