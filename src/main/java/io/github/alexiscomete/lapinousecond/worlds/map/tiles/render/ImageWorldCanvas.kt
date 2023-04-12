package io.github.alexiscomete.lapinousecond.worlds.map.tiles.render

import io.github.alexiscomete.lapinousecond.worlds.map.tiles.Tile
import io.github.alexiscomete.lapinousecond.worlds.map.tiles.sprite.Sprite
import java.awt.image.BufferedImage

/**
 * Permet de dessiner la map sur une image
 */
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

    override fun drawSprite(sprite: Sprite, x: Int, y: Int) {
        // TODO ... no sprite for now
    }

    override fun resetCanvas(newSize: Pair<Int, Int>) {
        bufferedImage = BufferedImage(newSize.first, newSize.second, BufferedImage.TYPE_INT_RGB)
        priorityMap = Array(newSize.second) { Array(newSize.first) { 0 } }
    }

}