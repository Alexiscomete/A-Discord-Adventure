package io.github.alexiscomete.lapinousecond.worlds.map.tiles.render.canvas

import io.github.alexiscomete.lapinousecond.worlds.map.tiles.types.Tile
import io.github.alexiscomete.lapinousecond.worlds.map.tiles.render.JustDrawIt
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

    override fun drawSprite(sprite: Sprite, x: Int, y: Int, priority: Int) {
        if (y < 0 || y >= canvas.size || x < 0 || x >= sizeArray) return
        if (priority < canvas[y][x].second) return
        canvas[y][x] = Pair(sprite.letter(), priority)
    }

    override fun justDrawThisOver(justDrawIt: JustDrawIt, x: Int, y: Int) {
        if (y < 0 || y >= canvas.size || x < 0 || x >= sizeArray) return
        canvas[y][x] = Pair(justDrawIt.letter, 0)
    }

    override fun resetCanvas(newW: Int, newH: Int) {
        canvas = Array(newH) { Array(newW) { Pair(' ', 0) } }
        sizeArray = newW
    }

    override fun onCanvas(x: Int, y: Int): Boolean {
        return y >= 0 && y < canvas.size && x >= 0 && x < sizeArray
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