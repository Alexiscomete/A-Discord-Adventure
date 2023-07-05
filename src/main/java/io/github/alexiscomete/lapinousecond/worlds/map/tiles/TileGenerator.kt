package io.github.alexiscomete.lapinousecond.worlds.map.tiles

interface TileGenerator {
    fun getOrGenerateTileAt(x: Int, y: Int): Tile
    fun updateCache()
}