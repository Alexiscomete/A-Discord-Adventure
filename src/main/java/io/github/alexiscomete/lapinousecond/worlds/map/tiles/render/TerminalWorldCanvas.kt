package io.github.alexiscomete.lapinousecond.worlds.map.tiles.render

import io.github.alexiscomete.lapinousecond.worlds.map.tiles.Tile
import io.github.alexiscomete.lapinousecond.worlds.map.tiles.sprite.Sprite

class TerminalWorldCanvas : WorldCanvas {

    var canvas: Array<Array<Char>> = arrayOf()

    override fun drawTile(tile: Tile, x: Int, y: Int) {
        if (canvas.isEmpty()) return
        if (canvas[0].isEmpty()) return
        if (y < 0 || y >= canvas.size) return
        if (x < 0 || x >= canvas[0].size) return
        canvas[y][x] = tile.letter()
    }

    override fun drawSprite(sprite: Sprite, x: Int, y: Int) {
        // TODO ... no sprite for now
    }

    override fun resetCanvas(newSize: Pair<Int, Int>) {
        canvas = Array(newSize.second) { Array(newSize.first) { ' ' } }
    }

    fun printlnCanvas() {
        canvas.forEach { line ->
            line.forEach { char ->
                print(char)
            }
            println()
        }
    }
}