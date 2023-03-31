package io.github.alexiscomete.lapinousecond.worlds.map.tiles

interface Tile {

    val x: Int
    val y: Int

    fun delete()

    var displayState: Int // 0 = normal, 1 or more = render in progress
    fun renderRecursive(remainingSteps: Int, worldRenderScene: WorldRenderScene, xToUse: Int, yToUse: Int)

    /**
     * Aucun argument requis
     *
     * @see CachePixel.toStringRecFalse
     */
    fun resetRecursive()
    fun render()
}