package io.github.alexiscomete.lapinousecond.worlds.map.tiles.interactions

import java.awt.Graphics2D

interface InteractionSprite : Sprite {
    var x: Int
    var y: Int
    var width: Int
    var height: Int

    fun isClicked(x: Int, y: Int): Boolean
    fun isHovered(x: Int, y: Int): Boolean

    fun onClick(interactionScene: InteractionScene)
    fun onHover(interactionScene: InteractionScene)
    fun onLeave(interactionScene: InteractionScene)

    fun drawOn(interactionScene: Graphics2D)
}