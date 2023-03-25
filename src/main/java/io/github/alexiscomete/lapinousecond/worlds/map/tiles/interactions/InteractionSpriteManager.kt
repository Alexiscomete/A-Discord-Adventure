package io.github.alexiscomete.lapinousecond.worlds.map.tiles.interactions

interface InteractionSpriteManager {
    fun getAllElements(): List<InteractionSprite>
    fun updateAfter()
}