package io.github.alexiscomete.lapinousecond.worlds.map.tiles

import io.github.alexiscomete.lapinousecond.worlds.map.tiles.types.Tile

interface TileGenerator {
    fun getOrGenerateTileAt(x: Int, y: Int): Tile
    fun updateCache()
}