package io.github.alexiscomete.lapinousecond.worlds.map.tiles.render

import io.github.alexiscomete.lapinousecond.worlds.map.tiles.Tile
import io.github.alexiscomete.lapinousecond.worlds.map.tiles.sprite.Sprite

class TerminalWorldCanvas : WorldCanvas {

    private var canvas: Array<Array<Pair<Char, Int>>> = arrayOf()

    override fun drawTile(tile: Tile, x: Int, y: Int, priority: Int) {
        if (canvas.isEmpty()) return
        if (canvas[0].isEmpty()) return
        if (y < 0 || y >= canvas.size) return
        if (x < 0 || x >= canvas[0].size) return
        if (priority < canvas[y][x].second) return
        canvas[y][x] = Pair(tile.letter(), priority)
    }

    override fun drawSprite(sprite: Sprite, x: Int, y: Int) {
        // TODO ... no sprite for now
    }

    override fun resetCanvas(newSize: Pair<Int, Int>) {
        canvas = Array(newSize.second) { Array(newSize.first) { Pair(' ', 0) } }
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