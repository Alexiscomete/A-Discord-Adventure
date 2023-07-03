package io.github.alexiscomete.lapinousecond.worlds.map.tiles.textures

import io.github.alexiscomete.lapinousecond.worlds.map.tiles.PIXEL_HEIGHT
import io.github.alexiscomete.lapinousecond.worlds.map.tiles.PIXEL_WIDTH
import java.awt.Color

enum class TexturesInCode(
    val texture: Array<Array<Color>>
) {
    PATH(Array(PIXEL_HEIGHT) { Array(PIXEL_WIDTH) { Color(255, 178, 79) } }),
    FOLIAGE(Array(PIXEL_HEIGHT) { Array(PIXEL_WIDTH) { Color(100, 200, 0) } }),
    TRUNK(Array(PIXEL_HEIGHT) { Array(PIXEL_WIDTH) { Color(100, 50, 0) } }),
    WHITE(Array(PIXEL_HEIGHT) { Array(PIXEL_WIDTH) { Color(255, 255, 255) } }),
    WHITE_FLOOR(Array(PIXEL_HEIGHT) { Array(PIXEL_WIDTH) { Color(190, 190, 190) } }),
}