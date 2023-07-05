package io.github.alexiscomete.lapinousecond.worlds.map.tiles.multitiles.templating.tiles

import io.github.alexiscomete.lapinousecond.worlds.map.tiles.RenderingType
import io.github.alexiscomete.lapinousecond.worlds.map.tiles.Tile
import io.github.alexiscomete.lapinousecond.worlds.map.tiles.multitiles.MultiTilesManager
import io.github.alexiscomete.lapinousecond.worlds.map.tiles.render.WorldCanvas
import io.github.alexiscomete.lapinousecond.worlds.map.tiles.sprite.Sprite
import io.github.alexiscomete.lapinousecond.worlds.map.tiles.types.RENDER_DISTANCE_DEFAULT

abstract class BaseClosedTemplatedTile(
    override val x: Int,
    override val y: Int,
    private val multiTilesManager: MultiTilesManager
) : TemplatedTile {
    override var up: Tile? = null
    override var down: Tile? = null
    override var left: Tile? = null
    override var right: Tile? = null

    override fun delete() {
        up?.down = null
        down?.up = null
        left?.right = null
        right?.left = null
        up = null
        down = null
        left = null
        right = null
    }

    private var rendered: Boolean = false
    private var inQueue: Boolean = false

    override fun isRendered(): Boolean {
        return rendered
    }

    override fun render(
        xToUse: Int,
        yToUse: Int,
        distance: Int,
        canvas: WorldCanvas
    ): RenderingType {
        if (rendered) return RenderingType.NO_RENDER
        rendered = true
        multiTilesManager.iAmLoaded()
        if (distance > RENDER_DISTANCE_DEFAULT) return RenderingType.NO_RENDER
        canvas.drawTile(this, xToUse, yToUse, 5)
        return RenderingType.NO_RENDER
    }

    override fun resetRender() {
        rendered = false
        inQueue = false
    }

    override fun isWalkable(): Boolean {
        return false
    }

    override fun removeSprite(sprite: Sprite) {
        // no sprite
        return
    }
}