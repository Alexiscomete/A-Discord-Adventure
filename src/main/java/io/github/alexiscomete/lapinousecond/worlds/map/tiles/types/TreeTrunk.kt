package io.github.alexiscomete.lapinousecond.worlds.map.tiles.types

import io.github.alexiscomete.lapinousecond.worlds.map.tiles.Tile
import io.github.alexiscomete.lapinousecond.worlds.map.tiles.sprite.Sprite
import java.awt.Color

class TreeTrunk(
    override val x: Int,
    override val y: Int,
    steps: Int = 2,
    maxSteps: Int = steps,
) : BaseTileGroup(
    priority = 1,
    stepsDecreasing = false
) {

    override var up: Tile? = run {
        // get a random number : 0, 1, 2
        if (steps <= 0) return@run TreeFoliage(x, y - 1, 2)
        val random = (0..2).random()
        if (random == 0) {
            TreeFoliage(x, y - 1, 2)
        } else {
            TreeTrunk(x, y - 1, steps - 1, maxSteps)
        }
    }
    override var down: Tile? = null
    override var left: Tile? = run {
        // get a random number : 0, 1, 2, 3
        if (steps <= 0) return@run TreeFoliage(x - 1, y, 0)
        when ((0..3).random()) {
            0 -> TreeFoliage(x - 1, y, 1)
            1 -> if (steps == maxSteps) null else TreeTrunk(x - 1, y, steps - 1, maxSteps)
            else -> null
        }
    }
    override var right: Tile? = run {
        // get a random number : 0, 1, 2, 3
        if (steps <= 0) return@run TreeFoliage(x + 1, y, 0)
        when ((0..3).random()) {
            0 -> TreeFoliage(x + 1, y, 1)
            1 -> if (steps == maxSteps) null else TreeTrunk(x - 1, y, steps - 1, maxSteps)
            else -> null
        }
    }

    override fun letter(): Char {
        return '+'
    }

    override fun color(): Color {
        return Color(100, 50, 0)
    }

    override fun texture(): Array<Array<Color>> {
        return Array(16) { Array(16) { color() } }
    }

    override fun isWalkable(): Boolean {
        return false
    }

    override fun removeSprite(sprite: Sprite) {
        TODO("Not yet implemented")
    }

}