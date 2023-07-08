package io.github.alexiscomete.lapinousecond.worlds.map.tiles.sprite

import io.github.alexiscomete.lapinousecond.worlds.map.tiles.types.Tile

class SpritesManager {
    val sprites = mutableListOf<Sprite>()

    fun spritesOnTile(tile: Tile) = sprites.filter { it.tile == tile }
}