package io.github.alexiscomete.lapinousecond.worlds.map.tiles.render

import io.github.alexiscomete.lapinousecond.worlds.map.tiles.Tile
import io.github.alexiscomete.lapinousecond.worlds.map.tiles.sprite.Sprite

interface WorldCanvas {
    fun drawTile(tile: Tile, x: Int, y: Int, priority: Int = 0): Boolean
    fun drawSprite(sprite: Sprite, x: Int, y: Int, priority: Int)
    fun justDrawThisOver(justDrawIt: JustDrawIt, x: Int, y: Int)
    fun resetCanvas(newW: Int, newH: Int)
    fun onCanvas(x: Int, y: Int): Boolean
}