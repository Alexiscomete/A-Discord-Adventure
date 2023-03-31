package io.github.alexiscomete.lapinousecond.worlds.map.tiles.sprite

import io.github.alexiscomete.lapinousecond.worlds.map.tiles.Tile

interface Sprite {
    fun loadOn(tile: Tile)
}