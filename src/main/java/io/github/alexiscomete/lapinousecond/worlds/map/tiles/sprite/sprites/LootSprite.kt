package io.github.alexiscomete.lapinousecond.worlds.map.tiles.sprite.sprites

import io.github.alexiscomete.lapinousecond.worlds.map.tiles.Tile
import io.github.alexiscomete.lapinousecond.worlds.map.tiles.WorldRenderScene
import io.github.alexiscomete.lapinousecond.worlds.map.tiles.sprite.Sprite
import java.awt.Color

class LootSprite(override var tile: Tile) : Sprite {
    override fun initialLoadOn(tile: Tile) {
        return
    }

    override fun render(worldRenderScene: WorldRenderScene, x: Int, y: Int) {
        TODO("Not yet implemented")
    }

    override fun delete(tile: Tile) {
        TODO("Not yet implemented")
    }

    override fun color(): Color {
        TODO("Not yet implemented")
    }

    override fun letter(): Char {
        TODO("Not yet implemented")
    }

    override fun texture(): Array<Array<Color>> {
        TODO("Not yet implemented")
    }
}