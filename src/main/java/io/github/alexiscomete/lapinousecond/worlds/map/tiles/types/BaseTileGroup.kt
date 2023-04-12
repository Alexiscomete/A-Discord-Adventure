package io.github.alexiscomete.lapinousecond.worlds.map.tiles.types

import io.github.alexiscomete.lapinousecond.worlds.map.tiles.Tile
import io.github.alexiscomete.lapinousecond.worlds.map.tiles.WorldRenderScene

abstract class BaseTileGroup(
    val priority: Int = 0,
    private val stepsDecreasing: Boolean = true
) : Tile {
    override fun delete(worldRenderScene: WorldRenderScene) {
        val value = worldRenderScene.dicoTiles[Pair(x, y)]
        if (value == this) {
            worldRenderScene.dicoTiles.remove(Pair(x, y))
        }
        up?.delete(worldRenderScene)
        down?.delete(worldRenderScene)
        left?.delete(worldRenderScene)
        right?.delete(worldRenderScene)
        up = null
        down = null
        left = null
        right = null
    }

    var currentState: Int = 0
        private set

    override fun renderRecursive(remainingSteps: Int, worldRenderScene: WorldRenderScene, xToUse: Int, yToUse: Int) {
        if (remainingSteps <= currentState) return
        currentState = remainingSteps
        val nextSteps = if (stepsDecreasing) remainingSteps - 1 else remainingSteps
        up?.renderRecursive(
            nextSteps,
            worldRenderScene,
            xToUse,
            yToUse - 1
        )
        down?.renderRecursive(
            nextSteps,
            worldRenderScene,
            xToUse,
            yToUse + 1
        )
        left?.renderRecursive(
            nextSteps,
            worldRenderScene,
            xToUse - 1,
            yToUse
        )
        right?.renderRecursive(
            nextSteps,
            worldRenderScene,
            xToUse + 1,
            yToUse
        )
        worldRenderScene.canvas.drawTile(this, xToUse, yToUse, priority)
    }

    fun resetRecursive() {
        if (currentState == 0) return
        currentState = 0
        up?.also {
            if (it is BaseTileGroup) {
                it.resetRecursive()
            }
        }
        down?.also {
            if (it is BaseTileGroup) {
                it.resetRecursive()
            }
        }
        left?.also {
            if (it is BaseTileGroup) {
                it.resetRecursive()
            }
        }
        right?.also {
            if (it is BaseTileGroup) {
                it.resetRecursive()
            }
        }
    }

    override fun render(worldRenderScene: WorldRenderScene, x: Int, y: Int) {
        renderRecursive(15, worldRenderScene, x, y)
    }
}