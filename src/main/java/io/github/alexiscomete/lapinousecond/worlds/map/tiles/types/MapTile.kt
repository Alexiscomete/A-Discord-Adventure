package io.github.alexiscomete.lapinousecond.worlds.map.tiles.types

import io.github.alexiscomete.lapinousecond.worlds.map.tiles.RenderInfos
import io.github.alexiscomete.lapinousecond.worlds.map.tiles.Tile
import io.github.alexiscomete.lapinousecond.worlds.map.tiles.WorldRenderScene
import io.github.alexiscomete.lapinousecond.worlds.map.tiles.sprite.Sprite
import io.github.alexiscomete.lapinousecond.worlds.map.tiles.textures.TexturesInCode
import java.awt.Color
import kotlin.math.min

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

    private var onCanvas = false
    var rendered = false
        private set
    private var inQueue: Boolean = false

    override fun resetRender() {
        onCanvas = false
        rendered = false
        inQueue = false
    }

    override fun render(worldRenderScene: WorldRenderScene, xToUse: Int, yToUse: Int, distance: Int) {
        if (rendered) return
        rendered = true
        if (distance > 50) return
        onCanvas = worldRenderScene.canvas.onCanvas(xToUse, yToUse)
        if (onCanvas) {
            (up ?: run {
                worldRenderScene.getOrGenerateTileAt(xToUse, yToUse - 1).also {
                    up = it
                }
            }).addToRenderQueue(
                worldRenderScene,
                xToUse,
                yToUse - 1,
                distance + 1
            )
            (down ?: run {
                worldRenderScene.getOrGenerateTileAt(xToUse, yToUse + 1).also {
                    down = it
                }
            }).addToRenderQueue(
                worldRenderScene,
                xToUse,
                yToUse + 1,
                distance + 1
            )
            (left ?: run {
                worldRenderScene.getOrGenerateTileAt(xToUse - 1, yToUse).also {
                    left = it
                }
            }).addToRenderQueue(
                worldRenderScene,
                xToUse - 1,
                yToUse,
                distance + 1
            )
            (right ?: run {
                worldRenderScene.getOrGenerateTileAt(xToUse + 1, yToUse).also {
                    right = it
                }
            }).addToRenderQueue(
                worldRenderScene,
                xToUse + 1,
                yToUse,
                distance + 1
            )
            sprites.forEach {
                it.render(worldRenderScene, xToUse, yToUse)
            }
            worldRenderScene.canvas.drawTile(this, xToUse, yToUse)
        }
    }

    override fun addToRenderQueue(worldRenderScene: WorldRenderScene, x: Int, y: Int, distance: Int) {
        if (inQueue) return
        worldRenderScene.renderQueue.add(RenderInfos(this, x, y, distance))
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

    private fun currentColorCalc(): Color {
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
                            16
                        }
                    } ?: 16,
                    it.right?.let { upRight ->
                        if (upRight is MapTile && upRight.isPath) {
                            subY + 16 - subX
                        } else {
                            16
                        }
                    } ?: 16
                )
            }
        } ?: 16
        val distanceDown = down?.let {
            if (it is MapTile && it.isPath) {
                16 - subY
            } else {
                min(
                    it.left?.let { downLeft ->
                        if (downLeft is MapTile && downLeft.isPath) {
                            16 - subY + subX
                        } else {
                            16
                        }
                    } ?: 16,
                    it.right?.let { downRight ->
                        if (downRight is MapTile && downRight.isPath) {
                            32 - subY - subX
                        } else {
                            16
                        }
                    } ?: 16
                )
            }
        } ?: 16
        val distanceLeft = left?.let {
            if (it is MapTile && it.isPath) {
                subX
            } else {
                min(
                    it.up?.let { leftUp ->
                        if (leftUp is MapTile && leftUp.isPath) {
                            subX + subY
                        } else {
                            16
                        }
                    } ?: 16,
                    it.down?.let { leftDown ->
                        if (leftDown is MapTile && leftDown.isPath) {
                            subX + 16 - subY
                        } else {
                            16
                        }
                    } ?: 16
                )
            }
        } ?: 16
        val distanceRight = right?.let {
            if (it is MapTile && it.isPath) {
                16 - subX
            } else {
                min(
                    it.up?.let { rightUp ->
                        if (rightUp is MapTile && rightUp.isPath) {
                            16 - subX + subY
                        } else {
                            16
                        }
                    } ?: 16,
                    it.down?.let { rightDown ->
                        if (rightDown is MapTile && rightDown.isPath) {
                            32 - subX - subY
                        } else {
                            16
                        }
                    } ?: 16
                )
            }
        } ?: 16
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
                            16
                        }
                    } ?: 16,
                    it.right?.let { upRight ->
                        if (upRight is MapTile && upRight.isRiver) {
                            subY + 16 - subX
                        } else {
                            16
                        }
                    } ?: 16
                )
            }
        } ?: 16
        val distanceDown = down?.let {
            if (it is MapTile && it.isRiver) {
                16 - subY
            } else {
                min(
                    it.left?.let { downLeft ->
                        if (downLeft is MapTile && downLeft.isRiver) {
                            16 - subY + subX
                        } else {
                            16
                        }
                    } ?: 16,
                    it.right?.let { downRight ->
                        if (downRight is MapTile && downRight.isRiver) {
                            32 - subY - subX
                        } else {
                            16
                        }
                    } ?: 16
                )
            }
        } ?: 16
        val distanceLeft = left?.let {
            if (it is MapTile && it.isRiver) {
                subX
            } else {
                min(
                    it.up?.let { leftUp ->
                        if (leftUp is MapTile && leftUp.isRiver) {
                            subX + subY
                        } else {
                            16
                        }
                    } ?: 16,
                    it.down?.let { leftDown ->
                        if (leftDown is MapTile && leftDown.isRiver) {
                            subX + 16 - subY
                        } else {
                            16
                        }
                    } ?: 16
                )
            }
        } ?: 16
        val distanceRight = right?.let {
            if (it is MapTile && it.isRiver) {
                16 - subX
            } else {
                min(
                    it.up?.let { rightUp ->
                        if (rightUp is MapTile && rightUp.isRiver) {
                            16 - subX + subY
                        } else {
                            16
                        }
                    } ?: 16,
                    it.down?.let { rightDown ->
                        if (rightDown is MapTile && rightDown.isRiver) {
                            32 - subX - subY
                        } else {
                            16
                        }
                    } ?: 16
                )
            }
        } ?: 16
        return min(min(distanceUp, distanceDown), min(distanceLeft, distanceRight))
    }

    private fun currentTextureCalc(): Array<Array<Color>> {
        val color: Int = (height * 255).toInt()
        var blue = 0
        var green = 0
        var red = 0
        if (color > 127) {
            if (color > 128) {
                if (isPath) {
                    return TexturesInCode.PATH.texture
                }
                if (isRiver) {
                    return Array(16) { y ->
                        Array(16) { x ->
                            if (distanceWithPath(x, y) < 4) {
                                Color(255, 178, 79)
                            } else {
                                Color(0, 111, 255)
                            }
                        }
                    }
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
                return Array(16) { y ->
                    Array(16) { x ->
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
        return Array(16) { y ->
            Array(16) { x ->
                if (distanceWithPath(x, y) < 4) {
                    Color(255, 178, 79)
                } else if (distanceWithRiver(x, y) < 4) {
                    Color(0, 111, 255)
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