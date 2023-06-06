package io.github.alexiscomete.lapinousecond.worlds.map.tiles

data class RenderInfos(
    val tile: Tile,
    val xToUse: Int,
    val yToUse: Int,
    val distance: Int
) {
    fun render(worldRenderScene: WorldRenderScene) {
        tile.render(worldRenderScene, xToUse, yToUse, distance)
    }
}