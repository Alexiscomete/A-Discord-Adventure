package io.github.alexiscomete.lapinousecond.worlds.map.tiles

import io.github.alexiscomete.lapinousecond.worlds.map.tiles.render.BaseWorldRenderer
import io.github.alexiscomete.lapinousecond.worlds.map.tiles.render.WorldRenderer
import io.github.alexiscomete.lapinousecond.worlds.map.tiles.render.canvas.WorldCanvas

const val DEFAULT_SIZE_RENDER = 21

class WorldRenderScene(
    val canvas: WorldCanvas,
    x: Int,
    y: Int,
    private val tileGenerator: TileGenerator,
    val worldRenderer: WorldRenderer = BaseWorldRenderer(DEFAULT_SIZE_RENDER, canvas, tileGenerator)
) {
    private var currentTile = tileGenerator.getOrGenerateTileAt(x, y)

    fun renderAll() {
        worldRenderer.renderAll(currentTile)
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