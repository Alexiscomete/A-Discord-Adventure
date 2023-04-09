package io.github.alexiscomete.lapinousecond.worlds.map.tiles.render

import io.github.alexiscomete.lapinousecond.worlds.map.tiles.Tile
import io.github.alexiscomete.lapinousecond.worlds.map.tiles.sprite.Sprite

class TerminalWorldCanvas : WorldCanvas {

    private var canvas: Array<Array<Pair<Char, Int>>> = arrayOf()
    private var sizeArray = 0

    override fun drawTile(tile: Tile, x: Int, y: Int, priority: Int): Boolean {
        if (y < 0 || y >= canvas.size || x < 0 || x >= sizeArray) return false
        if (priority < canvas[y][x].second) return false
        canvas[y][x] = Pair(tile.letter(), priority)
        return true
    }

    override fun drawSprite(sprite: Sprite, x: Int, y: Int) {
        // TODO ... no sprite for now
    }

    override fun resetCanvas(newSize: Pair<Int, Int>) {
        canvas = Array(newSize.second) { Array(newSize.first) { Pair(' ', 0) } }
        sizeArray = newSize.first
    }

    fun printlnCanvas() {
        canvas.forEach { line ->
            line.forEach { char ->
                print(char.first)
            }
            println()
        }
    }
}