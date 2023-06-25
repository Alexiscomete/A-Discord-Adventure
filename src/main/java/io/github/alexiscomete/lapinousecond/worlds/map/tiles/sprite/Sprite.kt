package io.github.alexiscomete.lapinousecond.worlds.map.tiles.sprite

import io.github.alexiscomete.lapinousecond.worlds.map.tiles.Tile
import io.github.alexiscomete.lapinousecond.worlds.map.tiles.WorldRenderScene
import java.awt.Color

interface Sprite {

    var tile: Tile

    // MEMORY MANAGEMENT

    fun initialLoadOn(tile: Tile)
    fun delete(tile: Tile, worldRenderScene: WorldRenderScene)

    // RENDERING - WORLD

    fun render(worldRenderScene: WorldRenderScene, xToUse: Int, yToUse: Int, distance: Int)
    fun resetRender()
    fun isRendered(): Boolean
    fun addToRenderQueue(worldRenderScene: WorldRenderScene, x: Int, y: Int, distance: Int)

    // RENDERING - CANVAS

    fun color(): Color
    fun letter(): Char
    fun texture(): Array<Array<Color>>
    fun transparentMap(): Array<Array<Boolean>>
}