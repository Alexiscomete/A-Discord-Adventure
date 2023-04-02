package io.github.alexiscomete.lapinousecond.worlds.map.tiles

interface Tile {

    val x: Int
    val y: Int
    var up: Tile?
    var down: Tile?
    var left: Tile?
    var right: Tile?

    fun delete()

    fun renderRecursive(remainingSteps: Int, worldRenderScene: WorldRenderScene, xToUse: Int, yToUse: Int)

    /**
     * Aucun argument requis
     *
     * @see CachePixel.toStringRecFalse
     */
    fun resetRecursive()
    fun render(worldRenderScene: WorldRenderScene)
}