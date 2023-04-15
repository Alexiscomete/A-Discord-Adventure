package io.github.alexiscomete.lapinousecond.worlds.map.tiles.sprite

import io.github.alexiscomete.lapinousecond.worlds.map.tiles.Tile
import io.github.alexiscomete.lapinousecond.worlds.map.tiles.WorldRenderScene
import java.awt.Color

interface Sprite {

    var tile: Tile

    fun initialLoadOn(tile: Tile)
    fun render(worldRenderScene: WorldRenderScene, x: Int, y: Int)
    fun delete(tile: Tile)
    fun color(): Color
    fun letter(): Char
    fun texture(): Array<Array<Color>>
    fun transparentMap(): Array<Array<Boolean>>
}