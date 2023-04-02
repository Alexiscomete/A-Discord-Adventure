package io.github.alexiscomete.lapinousecond.worlds.map.tiles

class MapTile(
    override val x: Int,
    override val y: Int,
    override var up: Tile? = null,
    override var down: Tile? = null,
    override var left: Tile? = null,
    override var right: Tile? = null,
) : Tile {
    override fun delete() {
        up?.down = null
        down?.up = null
        left?.right = null
        right?.left = null
        up = null
        down = null
        left = null
        right = null
    }

    private var currentState: Int = 0

    override fun renderRecursive(remainingSteps: Int, worldRenderScene: WorldRenderScene, xToUse: Int, yToUse: Int) {
        if (remainingSteps < currentState) return
        currentState = remainingSteps
        worldRenderScene.canvas.drawTile(this, xToUse, yToUse)
        (up ?: worldRenderScene.getOrGenerateTileAt(x, y - 1)).renderRecursive(
            remainingSteps - 1,
            worldRenderScene,
            xToUse,
            yToUse - 1
        )
        (down ?: worldRenderScene.getOrGenerateTileAt(x, y + 1)).renderRecursive(
            remainingSteps - 1,
            worldRenderScene,
            xToUse,
            yToUse + 1
        )
        (left ?: worldRenderScene.getOrGenerateTileAt(x - 1, y)).renderRecursive(
            remainingSteps - 1,
            worldRenderScene,
            xToUse - 1,
            yToUse
        )
        (right ?: worldRenderScene.getOrGenerateTileAt(x + 1, y)).renderRecursive(
            remainingSteps - 1,
            worldRenderScene,
            xToUse + 1,
            yToUse
        )

    }

    override fun resetRecursive() {
        if (currentState == 0) return
        currentState = 0
        up?.resetRecursive()
        down?.resetRecursive()
        left?.resetRecursive()
        right?.resetRecursive()
    }

    override fun render(worldRenderScene: WorldRenderScene) {
        renderRecursive(30, worldRenderScene, x, y)
    }
}