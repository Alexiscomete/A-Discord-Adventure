package io.github.alexiscomete.lapinousecond.worlds.map.tiles.types

import io.github.alexiscomete.lapinousecond.worlds.map.tiles.Tile
import io.github.alexiscomete.lapinousecond.worlds.map.tiles.WorldRenderScene

abstract class BaseTileGroup(
    val priority: Int = 0
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

    var rendered: Boolean = false
        private set
    var inQueue: Boolean = false
        private set


    fun resetRecursive() {
        if (!rendered) return
        rendered = false
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

    override fun render(worldRenderScene: WorldRenderScene, x: Int, y: Int, distance: Int) {
        if (rendered) return
        rendered = true
        if (worldRenderScene.distance(xToUse, yToUse) > 50) return
        up?.addToRenderQueue(worldRenderScene, xToUse, yToUse - 1, distance + 1)
        down?.addToRenderQueue(worldRenderScene, xToUse, yToUse + 1, distance + 1)
        left?.addToRenderQueue(
            worldRenderScene, xToUse - 1, yToUse, distance + 1
        )
        right?.addToRenderQueue(
            worldRenderScene, xToUse + 1, yToUse, distance + 1
        )
        worldRenderScene.canvas.drawTile(this, xToUse, yToUse, priority)
    }

    override fun addToRenderQueue(worldRenderScene: WorldRenderScene, x: Int, y: Int, distance: Int) {
        TODO("Not yet implemented")
    }
}