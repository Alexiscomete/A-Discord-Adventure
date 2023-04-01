package io.github.alexiscomete.lapinousecond.worlds.map.tiles

class CachePixel(
    val x: Int, val y: Int,
    val height: Double,
    var up: CachePixel? = null,
    var down: CachePixel? = null,
    var left: CachePixel? = null,
    var right: CachePixel? = null
) {

    // --- Part 1 = delete ---

    private fun delete() {
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

    private var toStringTab = Array(21) { Array(21) { "." } }
    private var toStringStat = 0

    override fun toString(): String {
        // Step 1 : create at tab of 11x11 pixels with the current pixel in the middle
        println("Creating tab...")
        setTab(x, y, "C")
        toStringStat = 30
        down?.toStringRecTrue(19, this, x, y + 1)
        left?.toStringRecTrue(19, this, x - 1, y)
        right?.toStringRecTrue(19, this, x + 1, y)
        up?.toStringRecTrue(19, this, x, y - 1)
        toStringStat = 0
        down?.toStringRecFalse()
        left?.toStringRecFalse()
        right?.toStringRecFalse()
        up?.toStringRecFalse()
        return toStringTab.joinToString("\n") { it.joinToString("") }
    }

    private fun setTab(x: Int, y: Int, value: String) {
        try {
            toStringTab[y - this.y + 10][x - this.x + 10] = value
        } catch (e: Exception) {
            println("Error at ${x - this.x + 10} ${y - this.y + 10}")
        }
    }

    private fun toStringRecTrue(remainingSteps: Int, cacheToUse: CachePixel, x: Int, y: Int) {
        if (x == 0 && y == 0) println("Remaining steps : $remainingSteps")
        if (remainingSteps <= toStringStat) return
        if (x == 0 && y == 0) {
            if (this.x == 0 && this.y == 0) {
                cacheToUse.setTab(x, y, "M")
            } else {
                cacheToUse.setTab(x, y, "O")
            }
        } else if (this.x == 0 && this.y == 0) {
            cacheToUse.setTab(x, y, "0")
        } else {
            cacheToUse.setTab(x, y, "U")
        }
        toStringStat = remainingSteps
        down?.toStringRecTrue(remainingSteps - 1, cacheToUse, x, y + 1)
        left?.toStringRecTrue(remainingSteps - 1, cacheToUse, x - 1, y)
        right?.toStringRecTrue(remainingSteps - 1, cacheToUse, x + 1, y)
        up?.toStringRecTrue(remainingSteps - 1, cacheToUse, x, y - 1)
    }

    private fun toStringRecFalse() {
        if (toStringStat == 0) return
        toStringStat = 0
        down?.toStringRecFalse()
        left?.toStringRecFalse()
        right?.toStringRecFalse()
        up?.toStringRecFalse()
    }
}