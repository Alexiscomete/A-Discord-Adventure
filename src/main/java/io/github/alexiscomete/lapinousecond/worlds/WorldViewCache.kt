package io.github.alexiscomete.lapinousecond.worlds

import io.github.alexiscomete.lapinousecond.worlds.map.CachePixel

class WorldViewCache(
    val world: WorldManager, cacheSize: Int,
    val viewWidth: Int, viewHeight: Int,
    var x: Int, y: Int,
    val zoom: Zooms
) {
    var cache1 = CachePixel(
        x, y,
        world.getHeight(x, y, zoom),
    )
    var cache2 = cache1

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

    fun addDown() {

    }
}