package io.github.alexiscomete.lapinousecond.worlds.map.tiles.sprite.sprites

import io.github.alexiscomete.lapinousecond.data.managesave.generateUniqueID
import io.github.alexiscomete.lapinousecond.entity.concrete.items.items.StrasbourgSausage
import io.github.alexiscomete.lapinousecond.entity.concrete.items.itemsCacheCustom
import io.github.alexiscomete.lapinousecond.entity.entities.Player
import io.github.alexiscomete.lapinousecond.worlds.map.tiles.render.canvas.WorldCanvas
import io.github.alexiscomete.lapinousecond.worlds.map.tiles.sprite.Sprite
import io.github.alexiscomete.lapinousecond.worlds.map.tiles.textures.TexturesForSprites
import io.github.alexiscomete.lapinousecond.worlds.map.tiles.types.Tile
import java.awt.Color
import java.awt.image.BufferedImage

class LootSprite(override var tile: Tile, val player: Player) : Sprite {

    private var opened = false

    override fun render(canvas: WorldCanvas, xToUse: Int, yToUse: Int, distance: Int) {
        if (!opened && distance <= 1) {
            opened = true
            val id = generateUniqueID()
            itemsCacheCustom.add(id)
            val item = StrasbourgSausage(id)
            item["type"] = "StrasbourgSausage"
            player.addItem(item)
        }
        canvas.drawSprite(this, xToUse, yToUse, 5)
    }

    override fun isRendered(): Boolean {
        return tile.isRendered()
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