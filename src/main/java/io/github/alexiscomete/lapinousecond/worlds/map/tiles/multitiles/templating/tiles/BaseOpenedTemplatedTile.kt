package io.github.alexiscomete.lapinousecond.worlds.map.tiles.multitiles.templating.tiles

import io.github.alexiscomete.lapinousecond.worlds.map.tiles.RenderingType
import io.github.alexiscomete.lapinousecond.worlds.map.tiles.Tile
import io.github.alexiscomete.lapinousecond.worlds.map.tiles.multitiles.MultiTilesManager
import io.github.alexiscomete.lapinousecond.worlds.map.tiles.render.WorldCanvas

const val DEFAULT_TEMPLATE_PRIORITY = 2

abstract class BaseOpenedTemplatedTile(
    override val x: Int,
    override val y: Int,
    private val multiTilesManager: MultiTilesManager,
    private val threshold: Int
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

    override fun render(
        xToUse: Int,
        yToUse: Int,
        distance: Int,
        canvas: WorldCanvas
    ): RenderingType {
        if (rendered) return RenderingType.NO_RENDER
        rendered = true
        multiTilesManager.iAmLoaded()
        canvas.drawTile(this, xToUse, yToUse, DEFAULT_TEMPLATE_PRIORITY)
        if (distance <= threshold) {
            return RenderingType.ONLY_IF_EXIST
        }
        return RenderingType.NO_RENDER
    }

    override fun resetRender() {
        rendered = false
        inQueue = false
    }

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
}