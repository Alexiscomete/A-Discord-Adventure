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
}