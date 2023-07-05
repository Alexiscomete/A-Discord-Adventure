package io.github.alexiscomete.lapinousecond.worlds.map.tiles.render.canvas

import io.github.alexiscomete.lapinousecond.worlds.map.tiles.types.Tile
import io.github.alexiscomete.lapinousecond.worlds.map.tiles.render.JustDrawIt
import io.github.alexiscomete.lapinousecond.worlds.map.tiles.sprite.Sprite
import java.awt.image.BufferedImage

class ImageWorldCanvas : WorldCanvas {
    // image
    var bufferedImage: BufferedImage = BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB)
        private set
    // stockage des priorités
    private var priorityMap: Array<Array<Int>> = arrayOf()

    override fun drawTile(tile: Tile, x: Int, y: Int, priority: Int): Boolean {
        if (y < 0 || y >= bufferedImage.height || x < 0 || x >= bufferedImage.width) return false
        if (priority < priorityMap[y][x]) return false
        bufferedImage.setRGB(x, y, tile.color().rgb)
        priorityMap[y][x] = priority
        return true
    }

    override fun drawSprite(sprite: Sprite, x: Int, y: Int, priority: Int) {
        if (y < 0 || y >= bufferedImage.height || x < 0 || x >= bufferedImage.width) return
        if (priority < priorityMap[y][x]) return
        bufferedImage.setRGB(x, y, sprite.color().rgb)
        priorityMap[y][x] = priority
    }

    override fun justDrawThisOver(justDrawIt: JustDrawIt, x: Int, y: Int) {
        if (y < 0 || y >= bufferedImage.height || x < 0 || x >= bufferedImage.width) return
        bufferedImage.setRGB(x, y, justDrawIt.color.rgb)
    }

    override fun resetCanvas(newW: Int, newH: Int) {
        bufferedImage = BufferedImage(newW, newH, BufferedImage.TYPE_INT_RGB)
        priorityMap = Array(newH) { Array(newW) { 0 } }
    }

    override fun onCanvas(x: Int, y: Int): Boolean {
        return y in 0 until bufferedImage.height && x in 0 until bufferedImage.width
    }

}