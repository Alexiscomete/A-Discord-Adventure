package world

import io.github.alexiscomete.lapinousecond.worlds.*
import org.junit.jupiter.api.Test

class CacheTest {

    @Test
    fun testCacheGenerate() {
        val lineDown = generateLineDown(0, 0, Zooms.ZOOM_IN, 5, WorldManagerTest())
        val lineUp = generateLineUp(0, 0, Zooms.ZOOM_IN, 5, WorldManagerTest())
        val lineLeft = generateLineLeft(0, 0, Zooms.ZOOM_IN, 5, WorldManagerTest())
        val lineRight = generateLineRight(0, 0, Zooms.ZOOM_IN, 5, WorldManagerTest())
        // print line
        println(lineDown)
        println(lineUp)
        println(lineLeft)
        println(lineRight)
        println(lineDown)
    }

    @Test
    fun testCacheMove() {
        val view = WorldViewCache(WorldManagerTest(), 10, 10, 0, 0, Zooms.ZOOM_IN)
        printView(view)
        view.moveUp()
        printView(view)
        view.moveUp()
        printView(view)
        view.moveUp()
        printView(view)
        view.moveUp()
        printView(view)
        view.moveDown()
        printView(view)
        view.moveDown()
        printView(view)
        view.moveDown()
        printView(view)
    }

    private fun printView(view : WorldViewCache) {
        var cache = view.cache1
        repeat(view.cache2.y - view.cache1.y + 1) {
            var cache2 = cache
            repeat(view.cache2.x - view.cache1.x + 1) {
                print(if (cache2.height > 0.5) "X" else ".")
                print(" ")
                cache2 = cache2.right?: cache2
            }
            println()
            cache = cache.down?: cache
        }
        println()
    }
}