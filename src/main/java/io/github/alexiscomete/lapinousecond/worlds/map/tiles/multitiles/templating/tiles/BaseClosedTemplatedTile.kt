package io.github.alexiscomete.lapinousecond.worlds.map.tiles.multitiles.templating.tiles

import io.github.alexiscomete.lapinousecond.worlds.map.tiles.Tile
import io.github.alexiscomete.lapinousecond.worlds.map.tiles.WorldRenderScene
import io.github.alexiscomete.lapinousecond.worlds.map.tiles.sprite.Sprite
import java.awt.Color

abstract class BaseClosedTemplatedTile(
    override val x: Int,
    override val y: Int,
    override var up: Tile?,
    override var down: Tile?,
    override var left: Tile?,
    override var right: Tile?
) : TemplatedTile {
    override fun delete(worldRenderScene: WorldRenderScene) {
        TODO("Not yet implemented")
    }

    override fun render(worldRenderScene: WorldRenderScene, xToUse: Int, yToUse: Int, distance: Int) {
        TODO("Not yet implemented")
    }

    override fun addToRenderQueue(worldRenderScene: WorldRenderScene, x: Int, y: Int, distance: Int) {
        TODO("Not yet implemented")
    }

    override fun isWalkable(): Boolean {
        return false
    }

    override fun removeSprite(sprite: Sprite) {
        // no sprite 
    }
}