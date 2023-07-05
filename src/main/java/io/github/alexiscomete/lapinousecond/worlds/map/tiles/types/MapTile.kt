package io.github.alexiscomete.lapinousecond.worlds.map.tiles.types

import io.github.alexiscomete.lapinousecond.worlds.map.tiles.*
import io.github.alexiscomete.lapinousecond.worlds.map.tiles.render.WorldCanvas
import io.github.alexiscomete.lapinousecond.worlds.map.tiles.sprite.Sprite
import io.github.alexiscomete.lapinousecond.worlds.map.tiles.textures.TexturesInCode
import java.awt.Color
import kotlin.math.min

const val RENDER_DISTANCE_DEFAULT = 50
const val HEIGHT_MULTIPLICATOR_TO_INT = 255

const val MIN_HEIGHT = 0.0
const val DEEP_OCEAN_HEIGHT = 0.3
const val OCEAN_HEIGHT = 0.5
const val INT_OCEAN_HEIGHT = (OCEAN_HEIGHT * HEIGHT_MULTIPLICATOR_TO_INT).toInt()
const val INT_GRASS_HEIGHT = INT_OCEAN_HEIGHT + 1 // 128
const val MONTAIN_HEIGHT = 0.75
const val INT_MONTAIN_HEIGHT = (MONTAIN_HEIGHT * HEIGHT_MULTIPLICATOR_TO_INT).toInt() // 191
const val INT_SNOW_HEIGHT = 224
const val MAX_HEIGHT = 1.0

class MapTile(
    override val x: Int,
    override val y: Int,
    private val height: Double,
    private val isPath: Boolean = false,
    private val isRiver: Boolean = false,
    private val sprites: MutableList<Sprite> = mutableListOf()
) : Tile {
    override var up: Tile? = null
    override var down: Tile? = null
    override var left: Tile? = null
    override var right: Tile? = null

    override fun delete() {
        up?.also {
            if (this == it.down) it.down = null
        }
        down?.also {
            if (this == it.up) it.up = null
        }
        left?.also {
            if (this == it.right) it.right = null
        }
        right?.also {
            if (this == it.left) it.left = null
        }
        up = null
        down = null
        left = null
        right = null
    }

    private var rendered = false
    private var inQueue: Boolean = false

    override fun resetRender() {
        rendered = false
        inQueue = false
    }

    override fun isRendered(): Boolean {
        return rendered
    }

    override fun render(
        xToUse: Int,
        yToUse: Int,
        distance: Int,
        canvas: WorldCanvas
    ): RenderingType {
        if (rendered) return RenderingType.NO_RENDER
        rendered = true
        if (distance > RENDER_DISTANCE_DEFAULT) return RenderingType.NO_RENDER
        val onCanvas = canvas.onCanvas(xToUse, yToUse)
        if (onCanvas) {
            sprites.forEach {
                it.render(canvas, xToUse, yToUse, distance)
            }
            canvas.drawTile(this, xToUse, yToUse)
            return RenderingType.ALWAYS_RENDER
        }
        return RenderingType.NO_RENDER
    }

    override fun letter(): Char {
        return when (height) {
            in MIN_HEIGHT..DEEP_OCEAN_HEIGHT -> '@'
            in DEEP_OCEAN_HEIGHT..OCEAN_HEIGHT -> '~'
            in OCEAN_HEIGHT..MONTAIN_HEIGHT -> if (isPath) '#' else ','
            in MONTAIN_HEIGHT..MAX_HEIGHT -> if (isPath) '#' else '^'
            else -> {
                println("Error: height $height is not in range [${MIN_HEIGHT}, ${MAX_HEIGHT}]")
                ' '
            }
        }
    }

    private var tileColor: Color? = null

    private fun currentColorCalc(): Color {
        val color: Int = (height * HEIGHT_MULTIPLICATOR_TO_INT).toInt()
        var blue = 0
        var green = 0
        var red = 0
        if (color > INT_OCEAN_HEIGHT) {
            if (color > INT_GRASS_HEIGHT) {
                if (isPath) {
                    return Color(255, 178, 79)
                }
                if (isRiver) {
                    return Color(0, 111, 255)
                }
                green = 255 - color
                if (color > INT_MONTAIN_HEIGHT) {
                    red = 255 - color
                    if (color > INT_SNOW_HEIGHT) {
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
        return tileColor ?: Color(0, 0, 0).also { println("Error: tileColor is null") }
    }

    private val tileTexture: Array<Array<Color>> by lazy {
        currentTextureCalc()
    }

    private fun distanceWithPath(subX: Int, subY: Int): Int {
        // max is 16
        val distanceUp = up?.let {
            if (it is MapTile && it.isPath) {
                subY
            } else {
                min(
                    it.left?.let { upLeft ->
                        if (upLeft is MapTile && upLeft.isPath) {
                            subY + subX
                        } else {
                            PIXEL_HEIGHT
                        }
                    } ?: PIXEL_HEIGHT,
                    it.right?.let { upRight ->
                        if (upRight is MapTile && upRight.isPath) {
                            subY + PIXEL_HEIGHT - subX
                        } else {
                            PIXEL_HEIGHT
                        }
                    } ?: PIXEL_HEIGHT
                )
            }
        } ?: PIXEL_HEIGHT
        val distanceDown = down?.let {
            if (it is MapTile && it.isPath) {
                PIXEL_HEIGHT - subY
            } else {
                min(
                    it.left?.let { downLeft ->
                        if (downLeft is MapTile && downLeft.isPath) {
                            PIXEL_HEIGHT - subY + subX
                        } else {
                            PIXEL_HEIGHT
                        }
                    } ?: PIXEL_HEIGHT,
                    it.right?.let { downRight ->
                        if (downRight is MapTile && downRight.isPath) {
                            (PIXEL_HEIGHT * 2) - subY - subX
                        } else {
                            PIXEL_HEIGHT
                        }
                    } ?: PIXEL_HEIGHT
                )
            }
        } ?: PIXEL_HEIGHT
        val distanceLeft = left?.let {
            if (it is MapTile && it.isPath) {
                subX
            } else {
                min(
                    it.up?.let { leftUp ->
                        if (leftUp is MapTile && leftUp.isPath) {
                            subX + subY
                        } else {
                            PIXEL_WIDTH
                        }
                    } ?: PIXEL_WIDTH,
                    it.down?.let { leftDown ->
                        if (leftDown is MapTile && leftDown.isPath) {
                            subX + PIXEL_WIDTH - subY
                        } else {
                            PIXEL_WIDTH
                        }
                    } ?: PIXEL_WIDTH
                )
            }
        } ?: PIXEL_WIDTH
        val distanceRight = right?.let {
            if (it is MapTile && it.isPath) {
                PIXEL_WIDTH - subX
            } else {
                min(
                    it.up?.let { rightUp ->
                        if (rightUp is MapTile && rightUp.isPath) {
                            PIXEL_WIDTH - subX + subY
                        } else {
                            PIXEL_WIDTH
                        }
                    } ?: PIXEL_WIDTH,
                    it.down?.let { rightDown ->
                        if (rightDown is MapTile && rightDown.isPath) {
                            (PIXEL_WIDTH * 2) - subX - subY
                        } else {
                            PIXEL_WIDTH
                        }
                    } ?: PIXEL_WIDTH
                )
            }
        } ?: PIXEL_WIDTH
        return min(min(distanceUp, distanceDown), min(distanceLeft, distanceRight))
    }

    private fun distanceWithRiver(subX: Int, subY: Int): Int {
        // max is 16
        val distanceUp = up?.let {
            if (it is MapTile && it.isRiver) {
                subY
            } else {
                min(
                    it.left?.let { upLeft ->
                        if (upLeft is MapTile && upLeft.isRiver) {
                            subY + subX
                        } else {
                            PIXEL_HEIGHT
                        }
                    } ?: PIXEL_HEIGHT,
                    it.right?.let { upRight ->
                        if (upRight is MapTile && upRight.isRiver) {
                            subY + PIXEL_HEIGHT - subX
                        } else {
                            PIXEL_HEIGHT
                        }
                    } ?: PIXEL_HEIGHT
                )
            }
        } ?: PIXEL_HEIGHT
        val distanceDown = down?.let {
            if (it is MapTile && it.isRiver) {
                PIXEL_HEIGHT - subY
            } else {
                min(
                    it.left?.let { downLeft ->
                        if (downLeft is MapTile && downLeft.isRiver) {
                            PIXEL_HEIGHT - subY + subX
                        } else {
                            PIXEL_HEIGHT
                        }
                    } ?: PIXEL_HEIGHT,
                    it.right?.let { downRight ->
                        if (downRight is MapTile && downRight.isRiver) {
                            (PIXEL_HEIGHT * 2) - subY - subX
                        } else {
                            PIXEL_HEIGHT
                        }
                    } ?: PIXEL_HEIGHT
                )
            }
        } ?: PIXEL_HEIGHT
        val distanceLeft = left?.let {
            if (it is MapTile && it.isRiver) {
                subX
            } else {
                min(
                    it.up?.let { leftUp ->
                        if (leftUp is MapTile && leftUp.isRiver) {
                            subX + subY
                        } else {
                            PIXEL_WIDTH
                        }
                    } ?: PIXEL_WIDTH,
                    it.down?.let { leftDown ->
                        if (leftDown is MapTile && leftDown.isRiver) {
                            subX + PIXEL_WIDTH - subY
                        } else {
                            PIXEL_WIDTH
                        }
                    } ?: PIXEL_WIDTH
                )
            }
        } ?: PIXEL_WIDTH
        val distanceRight = right?.let {
            if (it is MapTile && it.isRiver) {
                PIXEL_WIDTH - subX
            } else {
                min(
                    it.up?.let { rightUp ->
                        if (rightUp is MapTile && rightUp.isRiver) {
                            PIXEL_WIDTH - subX + subY
                        } else {
                            PIXEL_WIDTH
                        }
                    } ?: PIXEL_WIDTH,
                    it.down?.let { rightDown ->
                        if (rightDown is MapTile && rightDown.isRiver) {
                            (PIXEL_WIDTH * 2) - subX - subY
                        } else {
                            PIXEL_WIDTH
                        }
                    } ?: PIXEL_WIDTH
                )
            }
        } ?: PIXEL_WIDTH
        return min(min(distanceUp, distanceDown), min(distanceLeft, distanceRight))
    }

    private fun currentTextureCalc(): Array<Array<Color>> {
        val color: Int = (height * HEIGHT_MULTIPLICATOR_TO_INT).toInt()
        var blue = 0
        var green = 0
        var red = 0
        if (color > INT_OCEAN_HEIGHT) {
            if (color > INT_GRASS_HEIGHT) {
                if (isPath) {
                    return TexturesInCode.PATH.texture
                }
                if (isRiver) {
                    return Array(PIXEL_HEIGHT) { y ->
                        Array(PIXEL_WIDTH) { x ->
                            if (distanceWithPath(x, y) < 4) {
                                Color(255, 178, 79)
                            } else if (distanceWithRiver(x, y) < 10) {
                                Color(0, 90, 255)
                            } else {
                                Color(0, 111, 255)
                            }
                        }
                    }
                }
                green = 255 - color
                if (color > INT_MONTAIN_HEIGHT) {
                    red = 255 - color
                    if (color > INT_SNOW_HEIGHT) {
                        blue = color
                        green = color
                    }
                }
            } else {
                return Array(PIXEL_HEIGHT) { y ->
                    Array(PIXEL_WIDTH) { x ->
                        if (distanceWithPath(x, y) < 4) {
                            Color(255, 178, 79)
                        } else if (distanceWithRiver(x, y) < 4) {
                            Color(0, 111, 255)
                        } else {
                            Color(245, 245, 66)
                        }
                    }
                }
            }
        } else {
            blue = color
        }
        return Array(PIXEL_HEIGHT) { y ->
            Array(PIXEL_WIDTH) { x ->
                val distanceR = distanceWithRiver(x, y)
                if (distanceWithPath(x, y) < 4) {
                    Color(255, 178, 79)
                } else if (distanceR < 4) {
                    Color(0, 111, 255)
                } else if (distanceR < 6) {
                    Color(91, 32, 0).mix(Color(red, green, blue))
                } else {
                    Color(red, green, blue)
                }
            }
        }
    }

    override fun texture(): Array<Array<Color>> {
        return tileTexture
    }

    override fun isWalkable(): Boolean {
        return true
    }

    override fun removeSprite(sprite: Sprite) {
        sprites.remove(sprite)
    }
}

fun Color.mix(color: Color): Color {
    return Color(
        (this.red + color.red) / 2,
        (this.green + color.green) / 2,
        (this.blue + color.blue) / 2
    )
}