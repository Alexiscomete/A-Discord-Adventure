package io.github.alexiscomete.lapinousecond.worlds.map.tiles.sprite.sprites

import io.github.alexiscomete.lapinousecond.entity.entities.PlayerManager
import io.github.alexiscomete.lapinousecond.worlds.map.tiles.sprite.SpriteWithIA
import io.github.alexiscomete.lapinousecond.worlds.map.tiles.textures.TexturesForSprites
import io.github.alexiscomete.lapinousecond.worlds.map.tiles.types.DistanceWithPlayer
import io.github.alexiscomete.lapinousecond.worlds.map.tiles.types.Tile
import java.awt.Color
import java.awt.image.BufferedImage

open class BaseStaticSprite(
    val player: PlayerManager,
    private var baseLife: Int,
    private val baseAttack: Int,
    private val baseColor: Color,
    private val baseLetter: Char,
    baseTexture: TexturesForSprites,
    override var tile: Tile
) : SpriteWithIA, BaseSprite() {

    override fun useIA() {
        if (tile is DistanceWithPlayer && (tile as DistanceWithPlayer).currentDistance == 0) {
            player.setLife(player.getLife() - baseAttack)
            baseLife = 0
        } else if (baseLife > 0) {
            baseLife--
        }
    }

    override fun mustBeRemoved(): Boolean {
        return baseLife <= 0
    }

    override fun color(): Color {
        return baseColor
    }

    override fun letter(): Char {
        return baseLetter
    }

    private val texture = baseTexture.colorFilterFor(baseColor, 1.0)

    override fun texture(): BufferedImage {
        return texture
    }
}
