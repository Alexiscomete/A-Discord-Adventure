package io.github.alexiscomete.lapinousecond.worlds.map.tiles.types

import io.github.alexiscomete.lapinousecond.worlds.map.tiles.Tile
import java.awt.Color

class TreeTrunk(
    override val x: Int,
    override val y: Int,
    steps: Int,
) : BaseTileGroup() {

    override var up: Tile? = run {
        // get a random number : 0, 1, 2
        if (steps <= 0) return@run TreeFoliage(x, y - 1, 2)
        val random = (0..2).random()
        if (random == 0) {
            TreeFoliage(x, y - 1, 2)
        } else {
            TreeTrunk(x, y - 1, steps - 1)
        }
    }
    override var down: Tile? = null
    override var left: Tile? = run {
        // get a random number : 0, 1, 2, 3
        if (steps <= 0) return@run TreeFoliage(x - 1, y, 0)
        when ((0..3).random()) {
            0 -> TreeFoliage(x - 1, y, 1)
            1 -> TreeTrunk(x - 1, y, steps - 1)
            else -> null
        }
    }
    override var right: Tile? = run {
        // get a random number : 0, 1, 2, 3
        if (steps <= 0) return@run TreeFoliage(x + 1, y, 0)
        when ((0..3).random()) {
            0 -> TreeFoliage(x + 1, y, 1)
            1 -> TreeTrunk(x + 1, y, steps - 1)
            else -> null
        }
    }

    override fun letter(): Char {
        return '+'
    }

    override fun color(): Color {
        return Color(100, 50, 0)
    }

    override fun isWalkable(): Boolean {
        return false
    }

}