package io.github.alexiscomete.lapinousecond.worlds

import io.github.alexiscomete.lapinousecond.worlds.map.CachePixel

// Generate a line

fun generateLineDown(x: Int, y: Int, zoom: Zooms, size: Int, world: WorldManager): CachePixel {
    val startY = y + size - 1
    var cache = CachePixel(
        x, startY,
        world.getHeight(x, y, zoom),
    )
    for (currentY in startY - 1 downTo y) {
        cache.up = CachePixel(
            x, currentY,
            world.getHeight(x, currentY, zoom),
            down = cache
        )
        cache = cache.up!!
    }
    assert(cache.x == x && cache.y == y)
    return cache
}

fun generateLineUp(x: Int, y: Int, zoom: Zooms, size: Int, world: WorldManager): CachePixel {
    val startY = y - size + 1
    var cache = CachePixel(
        x, startY,
        world.getHeight(x, y, zoom),
    )
    for (currentY in startY + 1 .. y) {
        cache.down = CachePixel(
            x, currentY,
            world.getHeight(x, currentY, zoom),
            up = cache
        )
        cache = cache.down!!
    }
    assert(cache.x == x && cache.y == y)
    return cache
}

fun generateLineLeft(x: Int, y: Int, zoom: Zooms, size: Int, world: WorldManager): CachePixel {
    val startX = x - size + 1
    var cache = CachePixel(
        startX, y,
        world.getHeight(x, y, zoom),
    )
    for (currentX in startX + 1 .. x) {
        cache.right = CachePixel(
            currentX, y,
            world.getHeight(currentX, y, zoom),
            left = cache
        )
        cache = cache.right!!
    }
    assert(cache.x == x && cache.y == y)
    return cache
}

fun generateLineRight(x: Int, y: Int, zoom: Zooms, size: Int, world: WorldManager): CachePixel {
    val startX = x + size - 1
    var cache = CachePixel(
        startX, y,
        world.getHeight(size + x - 1, y, zoom),
    )
    for (currentX in startX - 1 downTo x) {
        cache.left = CachePixel(
            currentX, y,
            world.getHeight(currentX, y, zoom),
            right = cache
        )
        cache = cache.left!!
    }
    assert(cache.x == x && cache.y == y)
    return cache
}

class WorldViewCache(
    val world: WorldManager,
    viewWidth: Int, viewHeight: Int,
    x: Int, y: Int,
    private val zoom: Zooms
) {
    var cache1 = CachePixel(
        x, y,
        world.getHeight(x, y, zoom),
    )
        private set
    var cache2 = cache1
        private set

    init {
        repeat(viewWidth - 1) {
            cache2.right = CachePixel(
                x + it + 1, y,
                world.getHeight(x + it + 1, y, zoom),
                left = cache2
            )
            cache2 = cache2.right!!
        }
        repeat(viewHeight - 1) {
            addDown()
        }
    }

    // Add a line

    fun addDown() {
        val len = cache2.x - cache1.x + 1
        val y = cache2.y + 1
        val x = cache2.x
        cache2.addToDown(generateLineLeft(x, y, zoom, len, world))
        cache2 = cache2.down!!
    }

    fun addUp() {
        val len = cache2.x - cache1.x + 1
        val y = cache1.y - 1
        val x = cache1.x
        cache1.addToUp(generateLineRight(x, y, zoom, len, world))
        cache1 = cache1.up!!
    }

    fun addLeft() {
        val len = cache2.y - cache1.y + 1
        val y = cache1.y
        val x = cache1.x - 1
        cache1.addToLeft(generateLineDown(x, y, zoom, len, world))
        cache1 = cache1.left!!
    }

    fun addRight() {
        val len = cache2.y - cache1.y + 1
        val y = cache2.y
        val x = cache2.x + 1
        cache2.addToRight(generateLineUp(x, y, zoom, len, world))
        cache2 = cache2.right!!
    }

    // Delete a line

    fun deleteDown() {
        with(cache2) {
            cache2 = up!!
            deleteToLeft()
        }
    }

    fun deleteUp() {
        with(cache1) {
            cache1 = down!!
            deleteToRight()
        }
    }

    fun deleteLeft() {
        with(cache1) {
            cache1 = right!!
            deleteToDown()
        }
    }

    fun deleteRight() {
        with(cache2) {
            cache2 = left!!
            deleteToUp()
        }
    }

    // Get a pixel at a position

    fun getPixel(x: Int, y: Int): CachePixel {
        var cache = cache1
        repeat(y - cache1.y) {
            cache = cache.down ?: throw Exception("Out of bounds")
        }
        repeat(x - cache1.x) {
            cache = cache.right ?: throw Exception("Out of bounds")
        }
        return cache
    }

    // Move

    fun moveUp() {
        addUp()
        deleteDown()
    }

    fun moveDown() {
        addDown()
        deleteUp()
    }

    fun moveLeft() {
        addLeft()
        deleteRight()
    }

    fun moveRight() {
        addRight()
        deleteLeft()
    }
}