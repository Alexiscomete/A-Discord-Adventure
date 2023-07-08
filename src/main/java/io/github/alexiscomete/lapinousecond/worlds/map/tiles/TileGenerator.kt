package io.github.alexiscomete.lapinousecond.worlds.map.tiles

import io.github.alexiscomete.lapinousecond.worlds.map.tiles.sprite.SpritesManager
import io.github.alexiscomete.lapinousecond.worlds.map.tiles.types.Tile

interface TileGenerator {
    val spritesManager: SpritesManager
    fun getOrGenerateTileAt(x: Int, y: Int): Tile
    fun updateCache()
}