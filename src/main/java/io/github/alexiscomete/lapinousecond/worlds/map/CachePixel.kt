package io.github.alexiscomete.lapinousecond.worlds.map

import io.github.alexiscomete.lapinousecond.worlds.WorldManager
import io.github.alexiscomete.lapinousecond.worlds.Zooms

class CachePixel(
    val x: Int, val y: Int,
    val height: Double,
    var up: CachePixel? = null,
    var down: CachePixel? = null,
    var left: CachePixel? = null,
    var right: CachePixel? = null
) {

    // --- Part 1 = delete ---

    fun delete() {
        up?.down = null
        down?.up = null
        left?.right = null
        right?.left = null
    }

    fun deleteToUp() {
        up?.deleteToUp()
        delete()
    }

    fun deleteToDown() {
        down?.deleteToDown()
        delete()
    }

    fun deleteToLeft() {
        left?.deleteToLeft()
        delete()
    }

    fun deleteToRight() {
        right?.deleteToRight()
        delete()
    }

    // --- Part 2 = add ---

    fun addToUp(cachePixel: CachePixel) {
        up = cachePixel
        cachePixel.down = this
        cachePixel.right?.let { right?.addToUp(it) }
    }

    fun addToDown(cachePixel: CachePixel) {
        down = cachePixel
        cachePixel.up = this
        cachePixel.left?.let { left?.addToDown(it) }
    }

    fun addToLeft(cachePixel: CachePixel) {
        left = cachePixel
        cachePixel.right = this
        cachePixel.down?.let { down?.addToLeft(it) }
    }

    fun addToRight(cachePixel: CachePixel) {
        right = cachePixel
        cachePixel.left = this
        cachePixel.up?.let { up?.addToRight(it) }
    }
}