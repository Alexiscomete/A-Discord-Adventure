package io.github.alexiscomete.lapinousecond.worlds.map.tiles.render

import io.github.alexiscomete.lapinousecond.worlds.map.tiles.types.Tile

interface WorldRenderer {
    fun renderAll(from: Tile)

    val xSource: Int
    val ySource: Int
}