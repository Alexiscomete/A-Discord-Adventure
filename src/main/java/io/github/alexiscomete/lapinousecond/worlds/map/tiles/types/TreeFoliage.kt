package io.github.alexiscomete.lapinousecond.worlds.map.tiles.types

import io.github.alexiscomete.lapinousecond.worlds.map.tiles.Tile
import io.github.alexiscomete.lapinousecond.worlds.map.tiles.sprite.Sprite
import java.awt.Color

class TreeFoliage(
    override val x: Int,
    override val y: Int,
    steps: Int,
) : BaseTileGroup(priority = 2, stepsDecreasing = false) {
    override var up: Tile? = run {
        // get a random number : 0, 1, 2
        if (steps == 0) {
            null
        } else {
            val random = (0..2).random()
            if (random == 0) {
                null
            } else {
                TreeFoliage(x, y - 1, steps - 1)
            }
        }
    }
    override var down: Tile? = null
    override var left: Tile? = run {
        // get a random number : 0, 1, 2
        if (steps == 0) {
            null
        } else {
            val random = (0..2).random()
            if (random == 0) {
                null
            } else {
                TreeFoliage(x - 1, y, steps - 1)
            }
        }
    }
    override var right: Tile? = run {
        // get a random number : 0, 1, 2
        if (steps == 0) {
            null
        } else {
            val random = (0..2).random()
            if (random == 0) {
                null
            } else {
                TreeFoliage(x + 1, y, steps - 1)
            }
        }
    }

    override fun letter(): Char {
        return '@'
    }

    override fun color(): Color {
        return Color(100, 200, 0)
    }

    override fun texture(): Array<Array<Color>> {
        TODO("Not yet implemented")
    }

    override fun isWalkable(): Boolean {
        return false
    }

    override fun removeSprite(sprite: Sprite) {
        TODO("Not yet implemented")
    }
}