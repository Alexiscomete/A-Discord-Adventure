package io.github.alexiscomete.lapinousecond.worlds.map.tiles.sprite

import io.github.alexiscomete.lapinousecond.worlds.map.tiles.Tile
import io.github.alexiscomete.lapinousecond.worlds.map.tiles.WorldRenderScene

interface Sprite {
    fun loadOn(tile: Tile)
    fun render(worldRenderScene: WorldRenderScene, x: Int, y: Int)
}