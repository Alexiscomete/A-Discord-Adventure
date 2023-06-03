package io.github.alexiscomete.lapinousecond.worlds.map.tiles.multitiles.templating.tiles

import io.github.alexiscomete.lapinousecond.worlds.map.tiles.RenderInfos
import io.github.alexiscomete.lapinousecond.worlds.map.tiles.Tile
import io.github.alexiscomete.lapinousecond.worlds.map.tiles.WorldRenderScene
import io.github.alexiscomete.lapinousecond.worlds.map.tiles.sprite.Sprite

abstract class BaseClosedTemplatedTile(
    override val x: Int,
    override val y: Int
) : TemplatedTile {
    override var up: Tile? = null
    override var down: Tile? = null
    override var left: Tile? = null
    override var right: Tile? = null

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

    var rendered: Boolean = false
        private set
    private var inQueue: Boolean = false

    override fun render(worldRenderScene: WorldRenderScene, xToUse: Int, yToUse: Int, distance: Int) {
        if (rendered) return
        rendered = true
        if (distance > 50) return
        worldRenderScene.canvas.drawTile(this, xToUse, yToUse, 5)
    }

    override fun addToRenderQueue(worldRenderScene: WorldRenderScene, x: Int, y: Int, distance: Int) {
        if (inQueue) return
        worldRenderScene.renderQueue.add(RenderInfos(this, x, y, distance))
    }

    override fun isWalkable(): Boolean {
        return false
    }

    override fun removeSprite(sprite: Sprite) {
        // no sprite
        return
    }

    override fun resetRender() {
        rendered = false
        inQueue = false
    }
}