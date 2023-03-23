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

    var toStringTab = Array(21) { Array(21) { "." } }

    override fun toString(): String {
        // Step 1 : create at tab of 11x11 pixels with the current pixel in the middle
        setTab(x, y, "U")
        down?.toStringRecDown(19, this)
        left?.toStringRecLeft(19, this)
        right?.toStringRecRight(19, this)
        up?.toStringRecUp(19, this)
        return toStringTab.joinToString("\n") { it.joinToString("") }
    }

    fun setTab(x: Int, y: Int, value: String) {
        try {
            toStringTab[y - this.y + 10][x - this.x + 10] = value
        } catch (e: Exception) {
            println("Error at ${x - this.x + 5} ${y - this.y + 5}")
        }
    }

    fun toStringRecDown(remainingSteps: Int, cacheToUse: CachePixel) {
        if (remainingSteps <= 0) return
        cacheToUse.setTab(x, y, "U")
        down?.toStringRecDown(remainingSteps - 1, cacheToUse)
        left?.toStringRecLeft(remainingSteps - 1, cacheToUse)
        right?.toStringRecRight(remainingSteps - 1, cacheToUse)
    }

    fun toStringRecLeft(remainingSteps: Int, cacheToUse: CachePixel) {
        if (remainingSteps <= 0) return
        cacheToUse.setTab(x, y, "U")
        up?.toStringRecUp(remainingSteps - 1, cacheToUse)
        down?.toStringRecDown(remainingSteps - 1, cacheToUse)
        left?.toStringRecLeft(remainingSteps - 1, cacheToUse)
    }

    fun toStringRecRight(remainingSteps: Int, cacheToUse: CachePixel) {
        if (remainingSteps <= 0) return
        cacheToUse.setTab(x, y, "U")
        up?.toStringRecUp(remainingSteps - 1, cacheToUse)
        down?.toStringRecDown(remainingSteps - 1, cacheToUse)
        right?.toStringRecRight(remainingSteps - 1, cacheToUse)
    }

    fun toStringRecUp(remainingSteps: Int, cacheToUse: CachePixel) {
        if (remainingSteps <= 0) return
        cacheToUse.setTab(x, y, "U")
        up?.toStringRecUp(remainingSteps - 1, cacheToUse)
        left?.toStringRecLeft(remainingSteps - 1, cacheToUse)
        right?.toStringRecRight(remainingSteps - 1, cacheToUse)
    }
}