package io.github.alexiscomete.lapinousecond.worlds.map.tiles.render

import io.github.alexiscomete.lapinousecond.worlds.map.tiles.RenderingType
import io.github.alexiscomete.lapinousecond.worlds.map.tiles.types.Tile
import io.github.alexiscomete.lapinousecond.worlds.map.tiles.render.canvas.WorldCanvas

data class RenderInfos(
    val tile: Tile,
    val xToUse: Int,
    val yToUse: Int,
    val distance: Int
) {
    fun render(worldCanvas: WorldCanvas) {
        tile.render(xToUse, yToUse, distance, worldCanvas)
    }

    fun renderingType(canvas: WorldCanvas): RenderingType {
        return tile.renderingType(xToUse, yToUse, distance, canvas)
    }
}