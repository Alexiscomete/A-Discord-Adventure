package io.github.alexiscomete.lapinousecond.worlds.map.tiles.types

import io.github.alexiscomete.lapinousecond.worlds.map.tiles.Tile
import java.awt.Color

class TreeFoliage(
    override val x: Int,
    override val y: Int
) : BaseTileGroup() {
    override var up: Tile? = run {
        // get a random number : 0, 1, 2
        val random = (0..2).random()
        if (random == 0) {
            null
        } else {
            TreeFoliage(x, y - 1)
        }
    }
    override var down: Tile? = run {
        // get a random number : 0, 1, 2
        val random = (0..2).random()
        if (random == 0) {
            null
        } else {
            TreeFoliage(x, y + 1)
        }
    }
    override var left: Tile? = run {
        // get a random number : 0, 1, 2
        val random = (0..2).random()
        if (random == 0) {
            null
        } else {
            TreeFoliage(x - 1, y)
        }
    }
    override var right: Tile? = run {
        // get a random number : 0, 1, 2
        val random = (0..2).random()
        if (random == 0) {
            null
        } else {
            TreeFoliage(x + 1, y)
        }
    }

    override fun letter(): Char {
        return '*'
    }

    override fun color(): Color {
        return Color(100, 200, 0)
    }

    override fun isWalkable(): Boolean {
        return false
    }
}