package io.github.alexiscomete.lapinousecond.worlds.map.tiles.interactions

import io.github.alexiscomete.lapinousecond.worlds.map.tiles.Tile

interface Sprite {
    fun loadOn(tile: Tile)
}