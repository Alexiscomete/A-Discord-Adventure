package io.github.alexiscomete.lapinousecond.worlds.map.tiles.types

import io.github.alexiscomete.lapinousecond.worlds.map.tiles.Tile
import io.github.alexiscomete.lapinousecond.worlds.map.tiles.WorldRenderScene

abstract class BaseTileGroup : Tile {
    override fun delete(worldRenderScene: WorldRenderScene) {
        worldRenderScene.dicoTiles.remove(Pair(x, y))
        up?.delete(worldRenderScene)
        down?.delete(worldRenderScene)
        left?.delete(worldRenderScene)
        right?.delete(worldRenderScene)
        up = null
        down = null
        left = null
        right = null
    }

    private var currentState: Int = 0

    override fun renderRecursive(remainingSteps: Int, worldRenderScene: WorldRenderScene, xToUse: Int, yToUse: Int) {
        if (remainingSteps < currentState) return
        currentState = remainingSteps
        up?.renderRecursive(
            remainingSteps - 1,
            worldRenderScene,
            xToUse,
            yToUse - 1
        )
        down?.renderRecursive(
            remainingSteps - 1,
            worldRenderScene,
            xToUse,
            yToUse + 1
        )
        left?.renderRecursive(
            remainingSteps - 1,
            worldRenderScene,
            xToUse - 1,
            yToUse
        )
        right?.renderRecursive(
            remainingSteps - 1,
            worldRenderScene,
            xToUse + 1,
            yToUse
        )
        worldRenderScene.canvas.drawTile(this, xToUse, yToUse)
    }

    override fun resetRecursive(worldRenderScene: WorldRenderScene) {
        if (currentState == 0) return
        currentState = 0
        up?.resetRecursive(worldRenderScene)
        down?.resetRecursive(worldRenderScene)
        left?.resetRecursive(worldRenderScene)
        right?.resetRecursive(worldRenderScene)
    }

    override fun render(worldRenderScene: WorldRenderScene, x: Int, y: Int) {
        renderRecursive(15, worldRenderScene, x, y)
        resetRecursive(worldRenderScene)
    }
}