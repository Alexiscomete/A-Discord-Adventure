package io.github.alexiscomete.lapinousecond.worlds

import io.github.alexiscomete.lapinousecond.worlds.map.CachePixel

class WorldViewCache(
    val world: WorldManager,
    viewWidth: Int, viewHeight: Int,
    var x: Int, y: Int,
    val zoom: Zooms
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
            cache2 = CachePixel(
                x + it + 1, y,
                world.getHeight(x + it + 1, y, zoom),
                left = cache2
            )
        }
        repeat(viewHeight - 1) {
            addDown()
        }
    }

    // Generate a line

    fun generateLineDown(x: Int, y: Int, zoom: Zooms, size: Int): CachePixel {
        var cache = CachePixel(
            x, size + y - 1,
            world.getHeight(x, y, zoom),
        )
        repeat(
            size - 1
        ) {
            cache = CachePixel(
                x, size + y - it - 1,
                world.getHeight(x, size + y - it - 1, zoom),
                down = cache
            )
        }
        return cache
    }

    fun generateLineUp(x: Int, y: Int, zoom: Zooms, size: Int): CachePixel {
        var cache = CachePixel(
            x, y,
            world.getHeight(x, y, zoom),
        )
        repeat(
            size - 1
        ) {
            cache = CachePixel(
                x, y + it + 1,
                world.getHeight(x, y + it + 1, zoom),
                up = cache
            )
        }
        return cache
    }

    fun generateLineLeft(x: Int, y: Int, zoom: Zooms, size: Int): CachePixel {
        var cache = CachePixel(
            x, y,
            world.getHeight(x, y, zoom),
        )
        repeat(
            size - 1
        ) {
            cache = CachePixel(
                x + it + 1, y,
                world.getHeight(x + it + 1, y, zoom),
                left = cache
            )
        }
        return cache
    }

    fun generateLineRight(x: Int, y: Int, zoom: Zooms, size: Int): CachePixel {
        var cache = CachePixel(
            size + x - 1, y,
            world.getHeight(size + x - 1, y, zoom),
        )
        repeat(
            size - 1
        ) {
            cache = CachePixel(
                size + x - it - 1, y,
                world.getHeight(size + x - it - 1, y, zoom),
                right = cache
            )
        }
        return cache
    }

    // Add a line

    fun addDown() {
        val len = cache1.x - cache2.x + 1
        val y = cache2.y + 1
        val x = cache2.x
        cache2.addToDown(generateLineDown(x, y, zoom, len))
        cache2 = cache2.down!!
    }

    fun addUp() {
        val len = cache1.x - cache2.x + 1
        val y = cache1.y - 1
        val x = cache1.x
        cache2.addToUp(generateLineUp(x, y, zoom, len))
        cache2 = cache2.up!!
    }

    fun addLeft() {
        val len = cache1.y - cache2.y + 1
        val y = cache1.y
        val x = cache1.x - 1
        cache2.addToLeft(generateLineLeft(x, y, zoom, len))
        cache2 = cache2.left!!
    }

    fun addRight() {
        val len = cache1.y - cache2.y + 1
        val y = cache2.y
        val x = cache2.x + 1
        cache2.addToRight(generateLineRight(x, y, zoom, len))
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
        repeat(y) {
            cache = cache.down!!
        }
        repeat(x) {
            cache = cache.right!!
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