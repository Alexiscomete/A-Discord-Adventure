package io.github.alexiscomete.lapinousecond.worlds.map.tiles.types.multitiles.templating.tiles

import io.github.alexiscomete.lapinousecond.worlds.map.tiles.types.multitiles.MultiTilesManager
import io.github.alexiscomete.lapinousecond.worlds.map.tiles.sprite.Sprite
import io.github.alexiscomete.lapinousecond.worlds.map.tiles.textures.TexturesInCode
import java.awt.Color

class WhiteFloorOpenedTTile(x: Int, y: Int, multiTilesManager: MultiTilesManager, threshold: Int = 50) :
    BaseOpenedTemplatedTile(
        x, y,
        multiTilesManager, threshold
    ) {
    override fun letter(): Char {
        return '_'
    }

    override fun color(): Color {
        return Color(190, 190, 190)
    }

    override fun texture(): Array<Array<Color>> {
        return TexturesInCode.WHITE_FLOOR.texture
    }

    override fun removeSprite(sprite: Sprite) {
        return // TODO : Implement
    }
}