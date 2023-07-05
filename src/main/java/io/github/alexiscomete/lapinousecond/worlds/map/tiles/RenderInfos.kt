package io.github.alexiscomete.lapinousecond.worlds.map.tiles

import io.github.alexiscomete.lapinousecond.worlds.map.tiles.render.WorldCanvas

data class RenderInfos(
    val tile: Tile,
    val xToUse: Int,
    val yToUse: Int,
    val distance: Int
) {
    fun render(worldCanvas: WorldCanvas) : RenderingType {
        return tile.render(xToUse, yToUse, distance, worldCanvas)
    }
}