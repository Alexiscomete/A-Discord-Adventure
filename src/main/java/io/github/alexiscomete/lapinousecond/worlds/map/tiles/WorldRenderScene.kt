package io.github.alexiscomete.lapinousecond.worlds.map.tiles

import io.github.alexiscomete.lapinousecond.worlds.map.tiles.sprite.InteractionSpriteManager
import java.awt.Graphics2D
import kotlin.concurrent.thread

class WorldRenderScene {

    val spritesManagers = mutableListOf<InteractionSpriteManager>()

    fun drawOn(image: Graphics2D) {
        spritesManagers.forEach { it.getAllElements().forEach { sprite -> sprite.drawOn(image) } }
        thread {
            spritesManagers.forEach { it.updateAfter() }
        }
    }
}