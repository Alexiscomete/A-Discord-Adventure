package io.github.alexiscomete.lapinousecond.worlds.interactions

interface InteractionSpriteManager {
    fun getAllElements(): List<InteractionSprite>
    fun updateAfter()
}