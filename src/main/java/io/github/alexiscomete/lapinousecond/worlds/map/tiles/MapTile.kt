package io.github.alexiscomete.lapinousecond.worlds.map.tiles

import java.awt.Color

class MapTile(
    override val x: Int,
    override val y: Int,
    private val height: Double,
    private val isPath: Boolean = false,
    override var up: Tile? = null,
    override var down: Tile? = null,
    override var left: Tile? = null,
    override var right: Tile? = null,
) : Tile {
    override fun delete(worldRenderScene: WorldRenderScene) {
        worldRenderScene.dicoTiles.remove(Pair(x, y))
        up?.down = null
        down?.up = null
        left?.right = null
        right?.left = null
        up = null
        down = null
        left = null
        right = null
    }

    private var currentState: Int = 1

    override fun renderRecursive(remainingSteps: Int, worldRenderScene: WorldRenderScene, xToUse: Int, yToUse: Int) {
        if (remainingSteps < currentState) return
        currentState = remainingSteps
        worldRenderScene.canvas.drawTile(this, xToUse, yToUse)
        (up ?: worldRenderScene.getOrGenerateTileAt(x, y - 1)).renderRecursive(
            remainingSteps - 1,
            worldRenderScene,
            xToUse,
            yToUse - 1
        )
        (down ?: worldRenderScene.getOrGenerateTileAt(x, y + 1)).renderRecursive(
            remainingSteps - 1,
            worldRenderScene,
            xToUse,
            yToUse + 1
        )
        (left ?: worldRenderScene.getOrGenerateTileAt(x - 1, y)).renderRecursive(
            remainingSteps - 1,
            worldRenderScene,
            xToUse - 1,
            yToUse
        )
        (right ?: worldRenderScene.getOrGenerateTileAt(x + 1, y)).renderRecursive(
            remainingSteps - 1,
            worldRenderScene,
            xToUse + 1,
            yToUse
        )

    }

    override fun resetRecursive() {
        if (currentState == 0) return
        currentState = 0
        up?.resetRecursive()
        down?.resetRecursive()
        left?.resetRecursive()
        right?.resetRecursive()
    }

    override fun render(worldRenderScene: WorldRenderScene, x: Int, y: Int) {
        renderRecursive(15, worldRenderScene, x, y)
    }

    override fun letter(): Char {
        return when(height) {
            in 0.0..0.3 -> '@'
            in 0.3..0.5 -> '~'
            in 0.5..0.7 -> if (isPath) '#' else ','
            in 0.7..1.0 -> if (isPath) '#' else '^'
            else -> ' '
        }
    }

    override fun color(): Color {
        val color: Int = (height * 255).toInt()
        var blue = 0
        var green = 0
        var red = 0
        if (color > 127) {
            if (color > 128) {
                if (isPath) {
                    return Color(255, 178, 79)
                }
                green = 255 - color
                if (color > 191) {
                    red = 255 - color
                    if (color > 224) {
                        blue = color
                        green = color
                    }
                }
            } else {
                return Color(245, 245, 66)
            }
        } else {
            blue = color
        }
        return Color(red, green, blue)
    }

    override fun isWalkable(): Boolean {
        return true
    }
}