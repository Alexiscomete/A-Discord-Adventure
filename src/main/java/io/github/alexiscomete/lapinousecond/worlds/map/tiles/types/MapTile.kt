package io.github.alexiscomete.lapinousecond.worlds.map.tiles.types

import io.github.alexiscomete.lapinousecond.worlds.map.tiles.Tile
import io.github.alexiscomete.lapinousecond.worlds.map.tiles.WorldRenderScene
import io.github.alexiscomete.lapinousecond.worlds.map.tiles.sprite.Sprite
import java.awt.Color

class MapTile(
    override val x: Int,
    override val y: Int,
    private val height: Double,
    private val isPath: Boolean = false,
    private val isRiver: Boolean = false,
    private val sprites: MutableList<Sprite> = mutableListOf(),
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

    var currentState: Int = 0
        private set

    override fun renderRecursive(remainingSteps: Int, worldRenderScene: WorldRenderScene, xToUse: Int, yToUse: Int) {
        if (remainingSteps <= currentState) return
        currentState = remainingSteps
        if (worldRenderScene.canvas.drawTile(this, xToUse, yToUse)) {
            (up ?: run {
                val tile = worldRenderScene.getOrGenerateTileAt(x, y - 1)
                up = tile
                tile
            }).renderRecursive(
                remainingSteps - 1,
                worldRenderScene,
                xToUse,
                yToUse - 1
            )
            (down ?: run {
                worldRenderScene.getOrGenerateTileAt(x, y + 1).also {
                    down = it
                }
            }).renderRecursive(
                remainingSteps - 1,
                worldRenderScene,
                xToUse,
                yToUse + 1
            )
            (left ?: run {
                worldRenderScene.getOrGenerateTileAt(x - 1, y).also {
                    left = it
                }
            }).renderRecursive(
                remainingSteps - 1,
                worldRenderScene,
                xToUse - 1,
                yToUse
            )
            (right ?: run {
                worldRenderScene.getOrGenerateTileAt(x + 1, y).also {
                    right = it
                }
            }).renderRecursive(
                remainingSteps - 1,
                worldRenderScene,
                xToUse + 1,
                yToUse
            )
            sprites.forEach {
                it.render(worldRenderScene, xToUse, yToUse)
            }
        }
    }

    fun resetRender() {
        currentState = 0
    }

    override fun render(worldRenderScene: WorldRenderScene, x: Int, y: Int) {
        renderRecursive(50, worldRenderScene, x, y)
    }

    override fun letter(): Char {
        return when (height) {
            in 0.0..0.3 -> '@'
            in 0.3..0.5 -> '~'
            in 0.5..0.7 -> if (isPath) '#' else ','
            in 0.7..1.0 -> if (isPath) '#' else '^'
            else -> ' '
        }
    }

    private var tileColor: Color? = null

    private fun currentColorCalc() : Color {
        val color: Int = (height * 255).toInt()
        var blue = 0
        var green = 0
        var red = 0
        if (color > 127) {
            if (color > 128) {
                if (isPath) {
                    return Color(255, 178, 79)
                }
                if (isRiver) {
                    return Color(0, 111, 255)
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

    override fun color(): Color {
        if (tileColor == null) {
            tileColor = currentColorCalc()
        }
        return tileColor ?: Color(0, 0, 0)
    }

    private var tileTexture: Array<Array<Color>>? = null

    private fun currentTextureCalc() : Array<Array<Color>> {
        val color: Int = (height * 255).toInt()
        var blue = 0
        var green = 0
        var red = 0
        if (color > 127) {
            if (color > 128) {
                if (isPath) {
                    return Array(16) { Array(16) { Color(255, 178, 79) } }
                }
                if (isRiver) {
                    return Array(16) { Array(16) { Color(0, 111, 255) } }
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
                return Array(16) { Array(16) { Color(245, 245, 66) } }
            }
        } else {
            blue = color
        }
        return Array(16) { Array(16) { Color(red, green, blue) } }
    }

    override fun texture(): Array<Array<Color>> {
        if (tileTexture == null) {
            tileTexture = currentTextureCalc()
        }
        return tileTexture ?: Array(16) { Array(16) { Color(0, 0, 0) } }
    }

    override fun isWalkable(): Boolean {
        return true
    }

    override fun removeSprite(sprite: Sprite) {
        sprites.remove(sprite)
    }
}