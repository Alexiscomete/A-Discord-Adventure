package io.github.alexiscomete.lapinousecond.worlds.map.tiles.sprite

import io.github.alexiscomete.lapinousecond.worlds.map.tiles.types.Tile
import io.github.alexiscomete.lapinousecond.worlds.map.tiles.WorldRenderScene
import io.github.alexiscomete.lapinousecond.worlds.map.tiles.render.canvas.WorldCanvas
import java.awt.Color
import java.awt.image.BufferedImage

interface Sprite {

    var tile: Tile

    // MEMORY MANAGEMENT

    fun initialLoadOn(tile: Tile)
    fun delete(tile: Tile, worldRenderScene: WorldRenderScene)

    // RENDERING - WORLD

    fun render(canvas: WorldCanvas, xToUse: Int, yToUse: Int, distance: Int)
    fun resetRender()
    fun isRendered(): Boolean

    // RENDERING - CANVAS

    fun color(): Color
    fun letter(): Char
    fun texture(): BufferedImage
}