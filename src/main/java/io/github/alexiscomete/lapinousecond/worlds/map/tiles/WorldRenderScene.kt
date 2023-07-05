package io.github.alexiscomete.lapinousecond.worlds.map.tiles

import io.github.alexiscomete.lapinousecond.worlds.THRESHOLD_PATH
import io.github.alexiscomete.lapinousecond.worlds.WorldManager
import io.github.alexiscomete.lapinousecond.worlds.Zooms
import io.github.alexiscomete.lapinousecond.worlds.map.tiles.render.WorldCanvas

const val DEFAULT_SIZE_RENDER = 21
const val TREES_DEFAULT_SIZE = 2
const val TREE_DISTANCE_WITH_ELEMENTS = 0.25
const val EMPTY_ROOM_DISTANCE_WITH_ELEMENTS = 0.1
const val EMPTY_ROOM_MAX_DISTANCE_WITH_PATH = 0.3
val EMPTY_ROOM_PATH_LEVEL =
    (THRESHOLD_PATH + EMPTY_ROOM_DISTANCE_WITH_ELEMENTS)..(THRESHOLD_PATH + EMPTY_ROOM_MAX_DISTANCE_WITH_PATH)

class WorldRenderScene(
    val canvas: WorldCanvas,
    x: Int,
    y: Int,
    zoomLevel: Zooms,
    val world: WorldManager,
    val size: Int = DEFAULT_SIZE_RENDER
) {

    // NEW

    val tileGenerator = BaseTileGenerator(zoomLevel, world)
    val worldRenderer = BaseWorldRenderer(size, canvas, tileGenerator)

    private var currentTile = tileGenerator.getOrGenerateTileAt(x, y)

    fun renderAll() {
        // RENDER

        worldRenderer.renderAll(currentTile)

        // UPDATE CACHE

        tileGenerator.updateCache()
    }

    // ATTENTION : les cases vides d'une pièce nous sortent des pièces

    fun moveUp() {
        val next = currentTile.up ?: tileGenerator.getOrGenerateTileAt(currentTile.x, currentTile.y - 1)
        if (next.isWalkable()) currentTile = next
    }

    fun moveDown() {
        val next = currentTile.down ?: tileGenerator.getOrGenerateTileAt(currentTile.x, currentTile.y + 1)
        if (next.isWalkable()) currentTile = next
    }

    fun moveLeft() {
        val next = currentTile.left ?: tileGenerator.getOrGenerateTileAt(currentTile.x - 1, currentTile.y)
        if (next.isWalkable()) currentTile = next
    }

    fun moveRight() {
        val next = currentTile.right ?: tileGenerator.getOrGenerateTileAt(currentTile.x + 1, currentTile.y)
        if (next.isWalkable()) currentTile = next
    }

    fun getX(): Int = currentTile.x
    fun getY(): Int = currentTile.y
}