package io.github.alexiscomete.lapinousecond.worlds.map

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

    /**
     * Delete the current pixel and all the pixels above
     * ^
     * |
     * |
     * X
     */
    fun deleteToUp() {
        up?.deleteToUp()
        delete()
    }

    /**
     * Delete the current pixel and all the pixels below
     * X
     * |
     * |
     * v
     */
    fun deleteToDown() {
        down?.deleteToDown()
        delete()
    }

    /**
     * Delete the current pixel and all the pixels on the left
     * <-------X
     */
    fun deleteToLeft() {
        left?.deleteToLeft()
        delete()
    }

    /**
     * Delete the current pixel and all the pixels on the right
     * X------->
     */
    fun deleteToRight() {
        right?.deleteToRight()
        delete()
    }

    // --- Part 2 = add ---

    /**
     * Add a line of pixels above the current pixel
     * ----->
     * X---->
     */
    fun addToUp(cachePixel: CachePixel) {
        up = cachePixel
        cachePixel.down = this
        cachePixel.right?.let { right?.addToUp(it) }
    }

    /**
     * Add a line of pixels below the current pixel
     * <----X
     * <-----
     */
    fun addToDown(cachePixel: CachePixel) {
        down = cachePixel
        cachePixel.up = this
        cachePixel.left?.let { left?.addToDown(it) }
    }

    /**
     * Add a line of pixels on the left of the current pixel
     * ^^
     * ||
     * ||
     * X|
     */
    fun addToLeft(cachePixel: CachePixel) {
        left = cachePixel
        cachePixel.right = this
        cachePixel.down?.let { down?.addToLeft(it) }
    }

    /**
     * Add a line of pixels on the right of the current pixel
     * |X
     * ||
     * ||
     * vv
     */
    fun addToRight(cachePixel: CachePixel) {
        right = cachePixel
        cachePixel.left = this
        cachePixel.up?.let { up?.addToRight(it) }
    }
}