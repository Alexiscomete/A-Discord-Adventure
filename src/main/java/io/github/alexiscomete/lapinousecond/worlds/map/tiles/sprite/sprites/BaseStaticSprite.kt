package io.github.alexiscomete.lapinousecond.worlds.map.tiles.sprite.sprites

import io.github.alexiscomete.lapinousecond.entity.entities.PlayerManager
import io.github.alexiscomete.lapinousecond.worlds.map.tiles.sprite.SpriteWithIA
import io.github.alexiscomete.lapinousecond.worlds.map.tiles.types.DistanceWithPlayer

abstract class BaseStaticSprite(
    val player: PlayerManager,
    private var baseLife: Int,
    private val baseAttack: Int
) : SpriteWithIA, BaseSprite() {
    override fun useIA() {
        if (tile is DistanceWithPlayer && (tile as DistanceWithPlayer).currentDistance == 0) {
            player.setLife(player.getLife() - baseAttack)
            // TODO : remove self
        } else if (baseLife > 0) {
            baseLife--
        } else {
            // TODO : remove self
        }
    }
}
