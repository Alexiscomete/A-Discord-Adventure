package io.github.alexiscomete.lapinousecond.worlds.map.tiles.types

import io.github.alexiscomete.lapinousecond.worlds.map.tiles.Tile
import java.awt.Color

class TreeTrunk(
    override val x: Int,
    override val y: Int,
) : BaseTileGroup() {

    override var up: Tile? = run {
        // get a random number : 0, 1, 2
        val random = (0..2).random()
        if (random == 0) {
            TreeFoliage(x, y - 1)
        } else {
            TreeTrunk(x, y - 1)
        }
    }
    override var down: Tile? = null
    override var left: Tile? = run {
        // get a random number : 0, 1, 2, 3
        when ((0..3).random()) {
            0 -> TreeFoliage(x - 1, y)
            1 -> TreeTrunk(x - 1, y)
            else -> null
        }
    }
    override var right: Tile? = run {
        // get a random number : 0, 1, 2, 3
        when ((0..3).random()) {
            0 -> TreeFoliage(x + 1, y)
            1 -> TreeTrunk(x + 1, y)
            else -> null
        }
    }

    override fun letter(): Char {
        TODO("Not yet implemented")
    }

    override fun color(): Color {
        TODO("Not yet implemented")
    }

    override fun isWalkable(): Boolean {
        TODO("Not yet implemented")
    }

}