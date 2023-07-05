package io.github.alexiscomete.lapinousecond.worlds.map.tiles.textures

import io.github.alexiscomete.lapinousecond.worlds.map.tiles.types.TILE_HEIGHT
import io.github.alexiscomete.lapinousecond.worlds.map.tiles.types.TILE_WIDTH
import java.awt.Color

enum class TexturesInCode(
    val texture: Array<Array<Color>>
) {
    PATH(Array(TILE_HEIGHT) { Array(TILE_WIDTH) { Color(255, 178, 79) } }),
    FOLIAGE(Array(TILE_HEIGHT) { Array(TILE_WIDTH) { Color(100, 200, 0) } }),
    TRUNK(Array(TILE_HEIGHT) { Array(TILE_WIDTH) { Color(100, 50, 0) } }),
    WHITE(Array(TILE_HEIGHT) { Array(TILE_WIDTH) { Color(255, 255, 255) } }),
    WHITE_FLOOR(Array(TILE_HEIGHT) { Array(TILE_WIDTH) { Color(190, 190, 190) } }),
}