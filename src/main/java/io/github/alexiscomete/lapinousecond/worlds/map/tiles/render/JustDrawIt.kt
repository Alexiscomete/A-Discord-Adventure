package io.github.alexiscomete.lapinousecond.worlds.map.tiles.render

import java.awt.Color

const val HASH_CODE_MULTIPLIER = 31

data class JustDrawIt(
    val letter: Char,
    val color: Color,
    val texture: Array<Array<Color>>
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as JustDrawIt

        if (letter != other.letter) return false
        if (color != other.color) return false
        return texture.contentDeepEquals(other.texture)
    }

    override fun hashCode(): Int {
        var result = letter.hashCode()
        result = HASH_CODE_MULTIPLIER * result + color.hashCode()
        result = HASH_CODE_MULTIPLIER * result + texture.contentDeepHashCode()
        return result
    }
}