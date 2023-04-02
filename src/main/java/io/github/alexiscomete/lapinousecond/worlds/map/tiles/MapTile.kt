package io.github.alexiscomete.lapinousecond.worlds.map.tiles

class MapTile(
    override val x: Int,
    override val y: Int
) : Tile {
    override fun delete() {
        TODO("Not yet implemented")
    }

    override var displayState: Int = 0

    override fun renderRecursive(remainingSteps: Int, worldRenderScene: WorldRenderScene, xToUse: Int, yToUse: Int) {
        TODO("Not yet implemented")
    }

    override fun resetRecursive() {
        TODO("Not yet implemented")
    }

    override fun render() {
        TODO("Not yet implemented")
    }
}