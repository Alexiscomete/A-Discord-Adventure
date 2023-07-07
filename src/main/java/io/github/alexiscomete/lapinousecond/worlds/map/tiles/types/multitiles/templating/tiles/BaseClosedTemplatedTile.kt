package io.github.alexiscomete.lapinousecond.worlds.map.tiles.types.multitiles.templating.tiles

import io.github.alexiscomete.lapinousecond.worlds.map.tiles.render.RenderingType
import io.github.alexiscomete.lapinousecond.worlds.map.tiles.render.canvas.WorldCanvas
import io.github.alexiscomete.lapinousecond.worlds.map.tiles.types.RENDER_DISTANCE_DEFAULT
import io.github.alexiscomete.lapinousecond.worlds.map.tiles.types.Tile
import io.github.alexiscomete.lapinousecond.worlds.map.tiles.types.multitiles.MultiTilesManager

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
    ) {
        canvas.drawTile(this, xToUse, yToUse, 5)
    }

    override fun renderingType(xToUse: Int, yToUse: Int, distance: Int, canvas: WorldCanvas): RenderingType {
        if (rendered) return RenderingType.NOTHING
        rendered = true
        multiTilesManager.iAmLoaded()
        if (distance > RENDER_DISTANCE_DEFAULT) return RenderingType.NOTHING
        return RenderingType.NO_RENDER_REC
    }

    override fun resetRender() {
        rendered = false
        inQueue = false
    }

    override fun isWalkable(): Boolean {
        return false
    }
}