package io.github.alexiscomete.lapinousecond.worlds.map.tiles.types

import io.github.alexiscomete.lapinousecond.worlds.map.tiles.render.RenderingType
import io.github.alexiscomete.lapinousecond.worlds.map.tiles.render.canvas.WorldCanvas
import java.awt.Color

const val TILE_HEIGHT = 17
const val TILE_WIDTH = 17

interface Tile {

    val x: Int
    val y: Int
    var up: Tile?
    var down: Tile?
    var left: Tile?
    var right: Tile?

    fun delete()

    fun render(xToUse: Int, yToUse: Int, distance: Int, canvas: WorldCanvas)
    fun renderingType(xToUse: Int, yToUse: Int, distance: Int, canvas: WorldCanvas): RenderingType

    fun isWalkable(): Boolean

    fun resetRender()

    fun isRendered(): Boolean

    // RENDERING - CANVAS

    fun letter(): Char

    fun color(): Color

    fun texture(): Array<Array<Color>>
}