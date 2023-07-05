package io.github.alexiscomete.lapinousecond.worlds.map.tiles.types

import io.github.alexiscomete.lapinousecond.worlds.map.tiles.RenderingType
import io.github.alexiscomete.lapinousecond.worlds.map.tiles.Tile
import io.github.alexiscomete.lapinousecond.worlds.map.tiles.render.WorldCanvas

abstract class BaseTileGroup(
    val priority: Int = 0
) : Tile {
    override fun delete() {
        up?.delete()
        down?.delete()
        left?.delete()
        right?.delete()
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

    fun resetRecursive() {
        if (!rendered) return
        rendered = false
        inQueue = false
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

    override fun resetRender() {
        resetRecursive()
    }

    override fun render(
        xToUse: Int,
        yToUse: Int,
        distance: Int,
        canvas: WorldCanvas
    ): RenderingType {
        if (rendered) return RenderingType.NO_RENDER
        rendered = true
        if (distance > RENDER_DISTANCE_DEFAULT) return RenderingType.NO_RENDER
        canvas.drawTile(this, xToUse, yToUse, priority)
        return RenderingType.ONLY_IF_EXIST
    }

}