package io.github.alexiscomete.lapinousecond.worlds.map.tiles.render.canvas

import io.github.alexiscomete.lapinousecond.worlds.map.tiles.types.TILE_HEIGHT
import io.github.alexiscomete.lapinousecond.worlds.map.tiles.types.TILE_WIDTH
import io.github.alexiscomete.lapinousecond.worlds.map.tiles.types.Tile
import io.github.alexiscomete.lapinousecond.worlds.map.tiles.render.JustDrawIt
import io.github.alexiscomete.lapinousecond.worlds.map.tiles.sprite.Sprite
import java.awt.image.BufferedImage

class TextureWorldCanvas : WorldCanvas {

    private var currentHeight = 1
    private var currentWidth = 1
    var bufferedImage: BufferedImage =
        BufferedImage(currentWidth * TILE_WIDTH, currentHeight * TILE_HEIGHT, BufferedImage.TYPE_INT_RGB)
        private set
    private var priorityMap: Array<Array<Int>> = arrayOf()

    override fun drawTile(tile: Tile, x: Int, y: Int, priority: Int): Boolean {
        if (y < 0 || y >= currentHeight || x < 0 || x >= currentWidth) return false
        val colors = tile.texture()
        for (i in 0 until TILE_WIDTH) {
            for (j in 0 until TILE_HEIGHT) {
                if (priority < priorityMap[y * TILE_HEIGHT + j][x * TILE_WIDTH + i]) continue
                bufferedImage.setRGB(x * TILE_WIDTH + i, y * TILE_HEIGHT + j, colors[j][i].rgb)
                priorityMap[y * TILE_HEIGHT + j][x * TILE_WIDTH + i] = priority
            }
        }
        return true
    }

    override fun drawSprite(sprite: Sprite, x: Int, y: Int, priority: Int) {
        if (y < 0 || y >= currentHeight || x < 0 || x >= currentWidth) return
        val colors = sprite.texture()
        val transparent = sprite.transparentMap()
        for (i in 0 until TILE_WIDTH) {
            for (j in 0 until TILE_HEIGHT) {
                if (priority < priorityMap[y * TILE_HEIGHT + j][x * TILE_WIDTH + i] || transparent[y * TILE_HEIGHT + i][x * TILE_WIDTH + i]) continue
                bufferedImage.setRGB(
                    x * TILE_WIDTH + i,
                    y * TILE_HEIGHT + j,
                    colors[y * TILE_HEIGHT + i][x * TILE_WIDTH + i].rgb
                )
                priorityMap[y * TILE_HEIGHT + j][x * TILE_WIDTH + i] = priority
            }
        }
    }

    override fun justDrawThisOver(justDrawIt: JustDrawIt, x: Int, y: Int) {
        if (y < 0 || y >= currentHeight || x < 0 || x >= currentWidth) return
        val colors = justDrawIt.texture
        for (i in 0 until TILE_WIDTH) {
            for (j in 0 until TILE_HEIGHT) {
                bufferedImage.setRGB(x * TILE_WIDTH + i, y * TILE_HEIGHT + j, colors[j][i].rgb)
                priorityMap[y * TILE_HEIGHT + j][x * TILE_WIDTH + i] = 0
            }
        }
    }

    override fun resetCanvas(newW: Int, newH: Int) {
        currentHeight = newH
        currentWidth = newH
        bufferedImage =
            BufferedImage(currentWidth * TILE_WIDTH, currentHeight * TILE_HEIGHT, BufferedImage.TYPE_INT_RGB)
        priorityMap = Array(currentHeight * TILE_HEIGHT) { Array(currentWidth * TILE_WIDTH) { 0 } }
    }

    override fun onCanvas(x: Int, y: Int): Boolean {
        return y in 0 until currentHeight && x in 0 until currentWidth
    }
}