package io.github.alexiscomete.lapinousecond.worlds.map.tiles.textures

import java.awt.Color

enum class TexturesInCode(
    val texture: Array<Array<Color>>
) {
    PATH(Array(16) { Array(16) { Color(255, 178, 79) } }),
    FOLIAGE(Array(16) { Array(16) { Color(100, 200, 0) } }),
    TRUNK(Array(16) { Array(16) { Color(100, 50, 0) } }),
}