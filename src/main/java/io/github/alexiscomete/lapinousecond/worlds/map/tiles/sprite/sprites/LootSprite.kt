package io.github.alexiscomete.lapinousecond.worlds.map.tiles.sprite.sprites

import io.github.alexiscomete.lapinousecond.worlds.map.tiles.Tile
import io.github.alexiscomete.lapinousecond.worlds.map.tiles.WorldRenderScene
import io.github.alexiscomete.lapinousecond.worlds.map.tiles.sprite.Sprite
import java.awt.Color

class LootSprite(override var tile: Tile) : Sprite {
    override fun initialLoadOn(tile: Tile) {
        return
    }

    override fun delete(tile: Tile, worldRenderScene: WorldRenderScene) {
        return
    }

    private var opened = false

    override fun render(worldRenderScene: WorldRenderScene, xToUse: Int, yToUse: Int, distance: Int) {
        if (!opened && distance <= 1) {
            opened = true
        }
        worldRenderScene.canvas.drawSprite(this, xToUse, yToUse, 5)
    }

    override fun resetRender() {
        return
    }

    override fun isRendered(): Boolean {
        return tile.isRendered()
    }

    override fun addToRenderQueue(worldRenderScene: WorldRenderScene, x: Int, y: Int, distance: Int) {
        return
    }

    override fun color(): Color {
        return if (opened) {
            Color(0, 0, 0)
        }  else {
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

    override fun texture(): Array<Array<Color>> {
        TODO("Not yet implemented")
    }

    override fun transparentMap(): Array<Array<Boolean>> {
        TODO("Not yet implemented")
    }
}