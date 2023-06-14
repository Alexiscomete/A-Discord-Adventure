package io.github.alexiscomete.lapinousecond.worlds.map.tiles.multitiles.templating.tiles

import io.github.alexiscomete.lapinousecond.worlds.map.tiles.RenderInfos
import io.github.alexiscomete.lapinousecond.worlds.map.tiles.Tile
import io.github.alexiscomete.lapinousecond.worlds.map.tiles.WorldRenderScene
import io.github.alexiscomete.lapinousecond.worlds.map.tiles.multitiles.ComplexTile
import io.github.alexiscomete.lapinousecond.worlds.map.tiles.multitiles.MultiTilesManager

abstract class BaseOpenedTemplatedTile(
    override val x: Int,
    override val y: Int,
    private val multiTilesManager: MultiTilesManager
) : TemplatedTile {
    override var up: Tile? = null
    override var down: Tile? = null
    override var left: Tile? = null
    override var right: Tile? = null

    override fun isWalkable(): Boolean {
        return true
    }

    private var rendered: Boolean = false
    private var inQueue: Boolean = false

    override fun isRendered(): Boolean {
        return rendered
    }

    override fun render(worldRenderScene: WorldRenderScene, xToUse: Int, yToUse: Int, distance: Int) {
        if (rendered) return
        rendered = true
        multiTilesManager.iAmLoaded()
        if (distance > 50) return
        up?.addToRenderQueue(worldRenderScene, xToUse, yToUse - 1, distance + 1)
        down?.addToRenderQueue(worldRenderScene, xToUse, yToUse + 1, distance + 1)
        left?.addToRenderQueue(
            worldRenderScene, xToUse - 1, yToUse, distance + 1
        )
        right?.addToRenderQueue(
            worldRenderScene, xToUse + 1, yToUse, distance + 1
        )
        worldRenderScene.canvas.drawTile(this, xToUse, yToUse, 0)
    }

    override fun addToRenderQueue(worldRenderScene: WorldRenderScene, x: Int, y: Int, distance: Int) {
        if (inQueue) return
        worldRenderScene.renderQueue.add(RenderInfos(this, x, y, distance))
    }

    override fun resetRender() {
        rendered = false
        inQueue = false
    }

    override fun delete(worldRenderScene: WorldRenderScene) {
        up?.down = null
        down?.up = null
        left?.right = null
        right?.left = null
        up = null
        down = null
        left = null
        right = null
    }
}