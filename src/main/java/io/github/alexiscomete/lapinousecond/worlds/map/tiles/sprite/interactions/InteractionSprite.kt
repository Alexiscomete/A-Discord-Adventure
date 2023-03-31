package io.github.alexiscomete.lapinousecond.worlds.map.tiles.sprite.interactions

import io.github.alexiscomete.lapinousecond.worlds.map.tiles.WorldRenderScene
import io.github.alexiscomete.lapinousecond.worlds.map.tiles.sprite.Sprite
import java.awt.Graphics2D

interface InteractionSprite : Sprite {
    var x: Int
    var y: Int
    var width: Int
    var height: Int

    fun isClicked(x: Int, y: Int): Boolean
    fun isHovered(x: Int, y: Int): Boolean

    fun onClick(worldRenderScene: WorldRenderScene)
    fun onHover(worldRenderScene: WorldRenderScene)
    fun onLeave(worldRenderScene: WorldRenderScene)

    fun drawOn(interactionScene: Graphics2D)
}