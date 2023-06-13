package io.github.alexiscomete.lapinousecond.worlds.map.tiles.multitiles.templating.managers

import io.github.alexiscomete.lapinousecond.worlds.map.tiles.multitiles.ComplexTile
import io.github.alexiscomete.lapinousecond.worlds.map.tiles.multitiles.MultiTilesManager
import io.github.alexiscomete.lapinousecond.worlds.map.tiles.multitiles.templating.contexts.TemplateWorld
import io.github.alexiscomete.lapinousecond.worlds.map.tiles.multitiles.templating.tiles.WhiteFloorOpenedTTile

class EmptyRoom(
    val size: Int,
    val templateWorld: TemplateWorld,
    val x: Int,
    val y: Int
) : MultiTilesManager {
    private val dicoTilesExt = mutableMapOf<Pair<Int, Int>, ComplexTile>()
    private val dicoTilesInt = mutableMapOf<Pair<Int, Int>, ComplexTile>()
    val door = WhiteFloorOpenedTTile(x, y)

    override fun load() {
        // STEP 1 - load all
        for (i in 1 until size + 1) {
            for (j in 1 until size + 1) {
                val tile = WhiteFloorOpenedTTile(x + i, y + j)
                dicoTilesExt[Pair(i, j)] = tile
            }
        }
        // STEP 2 - load connections between tiles
        for (tile in dicoTilesExt.values) {
            val x = tile.x
            val y = tile.y
            val tileUp = dicoTilesExt[Pair(x, y - 1)] ?: WhiteWallOpenedTTile(x, y - 1).also {
                dicoTilesExt[Pair(x, y - 1)] = it
            }
            tile.up = tileUp
            tileUp.down = tile
            val tileDown = dicoTilesExt[Pair(x, y + 1)] ?: WhiteWallOpenedTTile(x, y + 1).also {
                dicoTilesExt[Pair(x, y + 1)] = it
            }
            tile.down = tileDown
            tileDown.up = tile
            val tileLeft = dicoTilesExt[Pair(x - 1, y)] ?: WhiteWallOpenedTTile(x - 1, y).also {
                dicoTilesExt[Pair(x - 1, y)] = it
            }
            tile.left = tileLeft
            tileLeft.right = tile
            val tileRight = dicoTilesExt[Pair(x + 1, y)] ?: WhiteWallOpenedTTile(x + 1, y).also {
                dicoTilesExt[Pair(x + 1, y)] = it
            }
            tile.right = tileRight
            tileRight.left = tile
        }
        // TODO - optimiser en ne faisant pas les 4 directions
    }

    override fun baseTileAt(x: Int, y: Int): ComplexTile {
        return dicoTilesExt[Pair(x, y)] ?: (throw IllegalArgumentException("Not in the room"))
    }

    override fun hasTileAt(x: Int, y: Int): Boolean {
        return dicoTilesExt.containsKey(Pair(x, y))
    }

    override fun canBeRemoved(): Boolean {
        TODO("Not yet implemented")
    }

    override fun removeAllTiles() {
        TODO("Not yet implemented")
    }

    override fun iAmLoaded() {
        TODO("Not yet implemented")
    }

    override fun delete() {
        TODO("Not yet implemented")
    }

    override fun unload() {
        TODO("Not yet implemented")
    }
}