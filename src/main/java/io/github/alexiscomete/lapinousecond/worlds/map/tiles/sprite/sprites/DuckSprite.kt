package io.github.alexiscomete.lapinousecond.worlds.map.tiles.sprite.sprites

import io.github.alexiscomete.lapinousecond.worlds.map.tiles.render.canvas.WorldCanvas
import io.github.alexiscomete.lapinousecond.worlds.map.tiles.sprite.SpriteWithIA
import io.github.alexiscomete.lapinousecond.worlds.map.tiles.textures.TexturesForSprites
import io.github.alexiscomete.lapinousecond.worlds.map.tiles.textures.mirrorImage
import io.github.alexiscomete.lapinousecond.worlds.map.tiles.types.MapTile
import io.github.alexiscomete.lapinousecond.worlds.map.tiles.types.Tile
import java.awt.Color
import java.awt.image.BufferedImage

class DuckSprite(override var tile: Tile) : SpriteWithIA {
    // false is left, true is right
    private var direction = false
    private var canUp = true
    private var canDown = true

    override fun useIA() {
        if (direction) {
            val right = tile.right
            val up = tile.up
            val down = tile.down
            if (right is MapTile && right.isRiver) {
                tile = right
                canDown = true
                canUp = true
            } else if (canUp) {
                if (up is MapTile && up.isRiver) {
                    tile = up
                } else {
                    canUp = false
                }
            } else if (canDown) {
                if (down is MapTile && down.isRiver) {
                    tile = down
                } else {
                    canDown = false
                }
            } else {
                direction = false
                canDown = true
                canUp = true
            }
        } else {
            val left = tile.left
            val up = tile.up
            val down = tile.down
            if (left is MapTile && left.isRiver) {
                tile = left
                canDown = true
                canUp = true
            } else if (canUp) {
                if (up is MapTile && up.isRiver) {
                    tile = up
                } else {
                    canUp = false
                }
            } else if (canDown) {
                if (down is MapTile && down.isRiver) {
                    tile = down
                } else {
                    canDown = false
                }
            } else {
                direction = true
                canDown = true
                canUp = true
            }
        }
    }

    override fun render(canvas: WorldCanvas, xToUse: Int, yToUse: Int, distance: Int) {
        canvas.drawSprite(this, xToUse, yToUse, 5)
    }

    override fun isRendered(): Boolean {
        return tile.isRendered()
    }

    override fun color(): Color {
        return Color(255, 219, 0)
    }

    override fun letter(): Char {
        return 'D'
    }

    override fun texture(): BufferedImage {
        if (direction) {
            return TexturesForSprites.DUCK.image.mirrorImage
        }
        return TexturesForSprites.DUCK.image
    }

}
