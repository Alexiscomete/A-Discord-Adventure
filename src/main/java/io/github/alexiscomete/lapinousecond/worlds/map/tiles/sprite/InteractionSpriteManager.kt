package io.github.alexiscomete.lapinousecond.worlds.map.tiles.sprite

import io.github.alexiscomete.lapinousecond.worlds.map.tiles.sprite.interactions.InteractionSprite

interface InteractionSpriteManager {
    fun getAllElements(): List<InteractionSprite>
    fun updateAfter()
}