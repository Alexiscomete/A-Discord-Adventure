package io.github.alexiscomete.lapinousecond.worlds.map.tiles.render

import io.github.alexiscomete.lapinousecond.worlds.map.tiles.PIXEL_HEIGHT
import io.github.alexiscomete.lapinousecond.worlds.map.tiles.PIXEL_WIDTH
import io.github.alexiscomete.lapinousecond.worlds.map.tiles.Tile
import io.github.alexiscomete.lapinousecond.worlds.map.tiles.sprite.Sprite
import java.awt.image.BufferedImage

class TextureWorldCanvas : WorldCanvas {

    private var currentHeight = 1
    private var currentWidth = 1
    var bufferedImage: BufferedImage =
        BufferedImage(currentWidth * PIXEL_WIDTH, currentHeight * PIXEL_HEIGHT, BufferedImage.TYPE_INT_RGB)
        private set
    private var priorityMap: Array<Array<Int>> = arrayOf()

    override fun drawTile(tile: Tile, x: Int, y: Int, priority: Int): Boolean {
        if (y < 0 || y >= currentHeight || x < 0 || x >= currentWidth) return false
        val colors = tile.texture()
        for (i in 0 until PIXEL_WIDTH) {
            for (j in 0 until PIXEL_HEIGHT) {
                if (priority < priorityMap[y * PIXEL_HEIGHT + j][x * PIXEL_WIDTH + i]) continue
                bufferedImage.setRGB(x * PIXEL_WIDTH + i, y * PIXEL_HEIGHT + j, colors[j][i].rgb)
                priorityMap[y * PIXEL_HEIGHT + j][x * PIXEL_WIDTH + i] = priority
            }
        }
        return true
    }

    override fun drawSprite(sprite: Sprite, x: Int, y: Int, priority: Int) {
        if (y < 0 || y >= currentHeight || x < 0 || x >= currentWidth) return
        val colors = sprite.texture()
        val transparent = sprite.transparentMap()
        for (i in 0 until PIXEL_WIDTH) {
            for (j in 0 until PIXEL_HEIGHT) {
                if (priority < priorityMap[y * PIXEL_HEIGHT + j][x * PIXEL_WIDTH + i] || transparent[y * PIXEL_HEIGHT + i][x * PIXEL_WIDTH + i]) continue
                bufferedImage.setRGB(
                    x * PIXEL_WIDTH + i,
                    y * PIXEL_HEIGHT + j,
                    colors[y * PIXEL_HEIGHT + i][x * PIXEL_WIDTH + i].rgb
                )
                priorityMap[y * PIXEL_HEIGHT + j][x * PIXEL_WIDTH + i] = priority
            }
        }
    }

    override fun justDrawThisOver(justDrawIt: JustDrawIt, x: Int, y: Int) {
        if (y < 0 || y >= currentHeight || x < 0 || x >= currentWidth) return
        val colors = justDrawIt.texture
        for (i in 0 until PIXEL_WIDTH) {
            for (j in 0 until PIXEL_HEIGHT) {
                bufferedImage.setRGB(x * PIXEL_WIDTH + i, y * PIXEL_HEIGHT + j, colors[j][i].rgb)
                priorityMap[y * PIXEL_HEIGHT + j][x * PIXEL_WIDTH + i] = 0
            }
        }
    }

    override fun resetCanvas(newW: Int, newH: Int) {
        currentHeight = newH
        currentWidth = newH
        bufferedImage =
            BufferedImage(currentWidth * PIXEL_WIDTH, currentHeight * PIXEL_HEIGHT, BufferedImage.TYPE_INT_RGB)
        priorityMap = Array(currentHeight * PIXEL_HEIGHT) { Array(currentWidth * PIXEL_WIDTH) { 0 } }
    }

    override fun onCanvas(x: Int, y: Int): Boolean {
        return y in 0 until currentHeight && x in 0 until currentWidth
    }
}