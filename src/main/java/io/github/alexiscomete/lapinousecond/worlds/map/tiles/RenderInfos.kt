package io.github.alexiscomete.lapinousecond.worlds.map.tiles

data class RenderInfos(
    val tile: Tile,
    val distance: Int,
    val xToUse: Int,
    val yToUse: Int
) {
    fun render(worldRenderScene: WorldRenderScene) {
        tile.render(worldRenderScene, xToUse, yToUse, distance)
    }
}