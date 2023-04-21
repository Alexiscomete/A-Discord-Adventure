package io.github.alexiscomete.lapinousecond.worlds.map.tiles.render

import io.github.alexiscomete.lapinousecond.worlds.map.tiles.Tile
import io.github.alexiscomete.lapinousecond.worlds.map.tiles.sprite.Sprite
import java.awt.image.BufferedImage

class TextureWorldCanvas : WorldCanvas {

    private var currentHeight = 1
    private var currentWidth = 1
    var bufferedImage: BufferedImage = BufferedImage(currentWidth * 16, currentHeight * 16, BufferedImage.TYPE_INT_RGB)
        private set
    private var priorityMap: Array<Array<Int>> = arrayOf()

    override fun drawTile(tile: Tile, x: Int, y: Int, priority: Int): Boolean {
        if (y < 0 || y >= currentHeight || x < 0 || x >= currentWidth) return false
        val colors = tile.texture()
        for (i in 0..15) {
            for (j in 0..15) {
                if (priority < priorityMap[y * 16 + j][x * 16 + i]) continue
                bufferedImage.setRGB(x * 16 + i, y * 16 + j, colors[j][i].rgb)
                priorityMap[y * 16 + j][x * 16 + i] = priority
            }
        }
        return true
    }

    override fun drawSprite(sprite: Sprite, x: Int, y: Int, priority: Int) {
        if (y < 0 || y >= currentHeight || x < 0 || x >= currentWidth) return
        val colors = sprite.texture()
        val transparent = sprite.transparentMap()
        for (i in 0..15) {
            for (j in 0..15) {
                if (priority < priorityMap[y * 16 + j][x * 16 + i] || transparent[j * 16 + i][x * 16 + i]) continue
                bufferedImage.setRGB(x * 16 + i, y * 16 + j, colors[j * 16 + i][x * 16 + i].rgb)
                priorityMap[y * 16 + j][x * 16 + i] = priority
            }
        }
    }

    override fun resetCanvas(newW: Int, newH: Int) {
        currentHeight = newH
        currentWidth = newH
        bufferedImage = BufferedImage(currentWidth * 16, currentHeight * 16, BufferedImage.TYPE_INT_RGB)
        priorityMap = Array(currentHeight * 16) { Array(currentWidth * 16) { 0 } }
    }

    override fun onCanvas(x: Int, y: Int): Boolean {
        return y >= 0 && y < currentHeight && x >= 0 && x < currentWidth
    }
}