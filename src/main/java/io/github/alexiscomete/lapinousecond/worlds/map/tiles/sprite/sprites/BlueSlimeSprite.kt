package io.github.alexiscomete.lapinousecond.worlds.map.tiles.sprite.sprites

import io.github.alexiscomete.lapinousecond.entity.entities.PlayerManager
import io.github.alexiscomete.lapinousecond.worlds.map.tiles.sprite.Sprite
import io.github.alexiscomete.lapinousecond.worlds.map.tiles.sprite.SpriteSpawner
import io.github.alexiscomete.lapinousecond.worlds.map.tiles.textures.TexturesForSprites
import io.github.alexiscomete.lapinousecond.worlds.map.tiles.textures.mirrorImage
import io.github.alexiscomete.lapinousecond.worlds.map.tiles.types.MapTile
import io.github.alexiscomete.lapinousecond.worlds.map.tiles.types.Tile
import java.awt.Color
import java.awt.image.BufferedImage

class BlueSlimeSprite(override var tile: Tile, private val playerManager: PlayerManager) : BaseMonsterSprite(),
    SpriteSpawner {

    private val baseImage = TexturesForSprites.SLIME_V2_DESATURE.colorFilterFor(Color(0, 150, 255), 1.0)

    override fun color(): Color {
        return Color(0, 150, 255)
    }

    override fun letter(): Char {
        return 'S'
    }

    override fun texture(): BufferedImage {
        if ((1..2).random() == 1) return baseImage.mirrorImage()
        return baseImage
    }

    private var coolDown = 2

    override fun spritesToSpawn(): List<Sprite> {
        if (tile is MapTile && coolDown <= 0) {
            coolDown = 2
            return listOf(
                BaseStaticSprite(
                    playerManager,
                    5,
                    1,
                    Color(0, 150, 255),
                    'S',
                    TexturesForSprites.ATTACK_V1,
                    tile
                )
            )
        } else {
            coolDown--
        }
        return listOf()
    }
}
