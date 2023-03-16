package io.github.alexiscomete.lapinousecond.worlds.interactions

interface InteractionSpriteManager {
    fun getAllElements(): List<InteractionSprite>
    fun canBeRemoved(interactionSprite: InteractionSprite): Boolean
}