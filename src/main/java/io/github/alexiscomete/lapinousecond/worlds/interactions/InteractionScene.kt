package io.github.alexiscomete.lapinousecond.worlds.interactions

import java.awt.image.BufferedImage

class InteractionScene {

    val spritesManagers = mutableListOf<InteractionSpriteManager>()
    val currentSprites = mutableListOf<InteractionSprite>()

    fun drawOn(image: BufferedImage) {
        spritesManagers.forEach { it.getAllElements().forEach { sprite -> sprite.draw(image) } }
    }
}