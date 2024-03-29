package io.github.alexiscomete.lapinousecond.worlds.map.tiles.sprite.sprites

import io.github.alexiscomete.lapinousecond.entity.concrete.items.Item
import io.github.alexiscomete.lapinousecond.entity.concrete.items.ItemTypesEnum
import io.github.alexiscomete.lapinousecond.entity.entities.managers.PlayerOwnerManager
import io.github.alexiscomete.lapinousecond.worlds.map.tiles.render.canvas.WorldCanvas
import io.github.alexiscomete.lapinousecond.worlds.map.tiles.sprite.Sprite
import io.github.alexiscomete.lapinousecond.worlds.map.tiles.textures.TexturesForSprites
import io.github.alexiscomete.lapinousecond.worlds.map.tiles.types.Tile
import java.awt.Color
import java.awt.image.BufferedImage

class LootSprite(override var tile: Tile, private val playerOwnerManager: PlayerOwnerManager) : Sprite {

    private var opened = false

    override fun render(canvas: WorldCanvas, xToUse: Int, yToUse: Int, distance: Int) {
        if (!opened && distance <= 1) {
            opened = true
            playerOwnerManager.addItem(Item.createItem(ItemTypesEnum.STRASBOURG_SAUSAGE))
        }
        canvas.drawSprite(this, xToUse, yToUse, 5)
    }

    override fun mustBeRemoved(): Boolean {
        return false
    }

    override fun color(): Color {
        return if (opened) {
            Color(0, 0, 0)
        } else {
            Color(255, 255, 255)
        }
    }

    override fun letter(): Char {
        return if (opened) {
            'O'
        } else {
            'C'
        }
    }

    override fun texture(): BufferedImage {
        return if (opened) {
            TexturesForSprites.CHEST_OPENED.image
        } else {
            TexturesForSprites.CHEST_CLOSED.image
        }
    }
}
