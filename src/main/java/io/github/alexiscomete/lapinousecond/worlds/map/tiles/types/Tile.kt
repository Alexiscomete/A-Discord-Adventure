package io.github.alexiscomete.lapinousecond.worlds.map.tiles.types

import io.github.alexiscomete.lapinousecond.worlds.map.tiles.RenderingType
import io.github.alexiscomete.lapinousecond.worlds.map.tiles.render.canvas.WorldCanvas
import io.github.alexiscomete.lapinousecond.worlds.map.tiles.sprite.Sprite
import java.awt.Color

const val TILE_HEIGHT = 16
const val TILE_WIDTH = 16

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
    fun removeSprite(sprite: Sprite)

    fun resetRender()

    fun isRendered(): Boolean

    // RENDERING - CANVAS

    fun letter(): Char

    fun color(): Color

    fun texture(): Array<Array<Color>>
}