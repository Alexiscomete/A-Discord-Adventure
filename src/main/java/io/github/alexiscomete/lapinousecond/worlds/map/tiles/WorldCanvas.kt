package io.github.alexiscomete.lapinousecond.worlds.map.tiles

import io.github.alexiscomete.lapinousecond.worlds.map.tiles.sprite.Sprite

interface WorldCanvas {
    fun drawTile(tile: Tile, x: Int, y: Int)
    fun drawSprite(sprite: Sprite, x: Int, y: Int)
    fun resetCanvas()
}