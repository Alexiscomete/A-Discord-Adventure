package io.github.alexiscomete.lapinousecond.worlds.map.tiles.types

import io.github.alexiscomete.lapinousecond.worlds.map.tiles.render.RenderingType
import io.github.alexiscomete.lapinousecond.worlds.map.tiles.render.canvas.WorldCanvas
import io.github.alexiscomete.lapinousecond.worlds.map.tiles.textures.Textures
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

val texturesLight = listOf(
    Textures.BASE_WATER1.colorFilterFor(Color(0, 111, 255), 2.3),
    Textures.BASE_WATER2.colorFilterFor(Color(0, 111, 255), 2.3),
    Textures.BASE_WATER3.colorFilterFor(Color(0, 111, 255), 2.3),
    Textures.BASE_WATER4.colorFilterFor(Color(0, 111, 255), 2.3),
    Textures.BASE_WATER5.colorFilterFor(Color(0, 111, 255), 2.3),
)

val texturesDark = listOf(
    Textures.BASE_WATER1.colorFilterFor(Color(0, 90, 255), 1.7),
    Textures.BASE_WATER2.colorFilterFor(Color(0, 90, 255), 1.7),
    Textures.BASE_WATER3.colorFilterFor(Color(0, 90, 255), 1.7),
    Textures.BASE_WATER4.colorFilterFor(Color(0, 90, 255), 1.7),
    Textures.BASE_WATER5.colorFilterFor(Color(0, 90, 255), 1.7),
)

class MapTile(
    override val x: Int,
    override val y: Int,
    private val height: Double,
    val isPath: Boolean = false,
    val isRiver: Boolean = false
) : Tile, DistanceWithPlayer {
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

    override fun resetRender() {
        rendered = false
    }

    override fun isRendered(): Boolean {
        return rendered
    }

    override fun render(
        xToUse: Int,
        yToUse: Int,
        distance: Int,
        canvas: WorldCanvas
    ) {
        canvas.drawTile(this, xToUse, yToUse)
    }

    override var currentDistance = 100
        private set

    override fun renderingType(xToUse: Int, yToUse: Int, distance: Int, canvas: WorldCanvas): RenderingType {
        if (rendered) return RenderingType.NOTHING
        rendered = true
        if (distance > RENDER_DISTANCE_DEFAULT) return RenderingType.NOTHING
        val onCanvas = canvas.onCanvas(xToUse, yToUse)
        if (onCanvas) {
            currentDistance = distance
            return RenderingType.ALWAYS_RENDER
        }
        return RenderingType.NOTHING
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
        return distanceWith(subX, subY) { it.isPath }
    }

    private fun distanceWithRiver(subX: Int, subY: Int): Int {
        return distanceWith(subX, subY) { it.isRiver }
    }

    private inline fun distanceWith(subX: Int, subY: Int, apply: (MapTile) -> Boolean): Int {
        val distanceUp = up?.let {
            if (it is MapTile && apply(it)) {
                subY
            } else {
                min(
                    it.left?.let { upLeft ->
                        if (upLeft is MapTile && apply(upLeft)) {
                            subY + subX
                        } else {
                            TILE_HEIGHT
                        }
                    } ?: TILE_HEIGHT,
                    it.right?.let { upRight ->
                        if (upRight is MapTile && apply(upRight)) {
                            subY + TILE_HEIGHT - subX
                        } else {
                            TILE_HEIGHT
                        }
                    } ?: TILE_HEIGHT
                )
            }
        } ?: TILE_HEIGHT
        val distanceDown = down?.let {
            if (it is MapTile && apply(it)) {
                TILE_HEIGHT - subY
            } else {
                min(
                    it.left?.let { downLeft ->
                        if (downLeft is MapTile && apply(downLeft)) {
                            TILE_HEIGHT - subY + subX
                        } else {
                            TILE_HEIGHT
                        }
                    } ?: TILE_HEIGHT,
                    it.right?.let { downRight ->
                        if (downRight is MapTile && apply(downRight)) {
                            (TILE_HEIGHT * 2) - subY - subX
                        } else {
                            TILE_HEIGHT
                        }
                    } ?: TILE_HEIGHT
                )
            }
        } ?: TILE_HEIGHT
        val distanceLeft = left?.let {
            if (it is MapTile && apply(it)) {
                subX
            } else {
                min(
                    it.up?.let { leftUp ->
                        if (leftUp is MapTile && apply(leftUp)) {
                            subX + subY
                        } else {
                            TILE_WIDTH
                        }
                    } ?: TILE_WIDTH,
                    it.down?.let { leftDown ->
                        if (leftDown is MapTile && apply(leftDown)) {
                            subX + TILE_WIDTH - subY
                        } else {
                            TILE_WIDTH
                        }
                    } ?: TILE_WIDTH
                )
            }
        } ?: TILE_WIDTH
        val distanceRight = right?.let {
            if (it is MapTile && apply(it)) {
                TILE_WIDTH - subX
            } else {
                min(
                    it.up?.let { rightUp ->
                        if (rightUp is MapTile && apply(rightUp)) {
                            TILE_WIDTH - subX + subY
                        } else {
                            TILE_WIDTH
                        }
                    } ?: TILE_WIDTH,
                    it.down?.let { rightDown ->
                        if (rightDown is MapTile && apply(rightDown)) {
                            (TILE_WIDTH * 2) - subX - subY
                        } else {
                            TILE_WIDTH
                        }
                    } ?: TILE_WIDTH
                )
            }
        } ?: TILE_WIDTH
        return min(min(distanceUp, distanceDown), min(distanceLeft, distanceRight))
    }

    private fun currentTextureCalc(): Array<Array<Color>> {
        val indexWater = texturesLight.indices.random()
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
                    return Array(TILE_HEIGHT) { y ->
                        Array(TILE_WIDTH) { x ->
                            if (distanceWithPath(x, y) < 4) {
                                Color(255, 178, 79)
                            } else if (distanceWithRiver(x, y) < 10) {
                                texturesDark[indexWater][x][y]
                            } else {
                                texturesLight[indexWater][x][y]
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
                return Array(TILE_HEIGHT) { y ->
                    Array(TILE_WIDTH) { x ->
                        if (distanceWithPath(x, y) < 4) {
                            Color(255, 178, 79)
                        } else if (distanceWithRiver(x, y) < 4) {
                            texturesLight[indexWater][x][y]
                        } else {
                            Color(245, 245, 66)
                        }
                    }
                }
            }
        } else {
            blue = color
        }
        val textureTemp = if (color > INT_OCEAN_HEIGHT) {
            Textures.BASE_GRASS.colorFilterFor(Color(red, green, blue), 2.3)
        } else {
            Textures.BASE_WATER1.colorFilterFor(Color(red, green, blue), 2.3)
        }
        return Array(TILE_HEIGHT) { y ->
            Array(TILE_WIDTH) { x ->
                val distanceR = distanceWithRiver(x, y)
                if (distanceWithPath(x, y) < 4) {
                    Color(255, 178, 79)
                } else if (distanceR < 4) {
                    texturesLight[indexWater][x][y]
                } else if (distanceR < 6) {
                    Color(91, 32, 0).mix(Color(red, green, blue))
                } else {
                    textureTemp[x][y]
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
}

fun Color.mix(color: Color): Color {
    return Color(
        (this.red + color.red) / 2,
        (this.green + color.green) / 2,
        (this.blue + color.blue) / 2
    )
}