package io.github.alexiscomete.lapinousecond.worlds.map.tiles.types.multitiles.templating.managers

import io.github.alexiscomete.lapinousecond.worlds.map.tiles.types.multitiles.ComplexTile
import io.github.alexiscomete.lapinousecond.worlds.map.tiles.types.multitiles.MultiTilesManager
import io.github.alexiscomete.lapinousecond.worlds.map.tiles.types.multitiles.templating.contexts.TemplateWorld
import io.github.alexiscomete.lapinousecond.worlds.map.tiles.types.multitiles.templating.tiles.WhiteFloorOpenedTTile

class EmptyRoom(
    private val size: Int,
    private val templateWorld: TemplateWorld,
    val x: Int,
    val y: Int
) : MultiTilesManager {
    private val dicoTilesInt = mutableMapOf<Pair<Int, Int>, ComplexTile>()
    private val door = WhiteFloorOpenedTTile(x, y, this, 1)
    private var isLoaded = false

    override fun load() {
        dicoTilesInt[Pair(x, y)] = door
        // STEP 1 - load all
        for (i in -size / 2 until size / 2 + 1) {
            for (j in 1 until size + 1) {
                val x = x + i
                val y = y + j
                val tile = WhiteFloorOpenedTTile(x, y, this)
                dicoTilesInt[Pair(x, y)] = tile
                /* CONCEPT
                 WWWW
                WXXXX
                WXXX
                WXX
                WX
                 */
                val tileUp = dicoTilesInt[Pair(x, y - 1)] ?: templateWorld.getTile(this).also {
                    dicoTilesInt[Pair(x, y - 1)] = it
                }
                tile.up = tileUp
                tileUp.down = tile
                val tileLeft = dicoTilesInt[Pair(x - 1, y)] ?: templateWorld.getTile(this).also {
                    dicoTilesInt[Pair(x - 1, y)] = it
                }
                tile.left = tileLeft
                tileLeft.right = tile
                // ---
                if (j == size) {
                    val tileDown = dicoTilesInt[Pair(x, y + 1)] ?: templateWorld.getTile(this).also {
                        dicoTilesInt[Pair(x, y + 1)] = it
                    }
                    tile.down = tileDown
                    tileDown.up = tile
                }
                if (i == size / 2) {
                    val tileRight = dicoTilesInt[Pair(x + 1, y)] ?: templateWorld.getTile(this).also {
                        dicoTilesInt[Pair(x + 1, y)] = it
                    }
                    tile.right = tileRight
                    tileRight.left = tile
                }
            }
        }
    }

    override fun baseTileAt(x: Int, y: Int): ComplexTile {
        return if (x == this.x && y == this.y) {
            door
        } else {
            throw Exception("No EXT tile at $x, $y")
        }
    }

    override fun hasTileAt(x: Int, y: Int): Boolean {
        return x == this.x && y == this.y
    }

    override fun canBeRemoved(): Boolean {
        return !isLoaded
    }

    override fun removeAllTiles() {
        dicoTilesInt.forEach { (_, tile) ->
            tile.delete()
        }
        dicoTilesInt.clear()
    }

    override fun iAmLoaded() {
        isLoaded = true
    }

    override fun resetIAmLoaded() {
        isLoaded = false
        dicoTilesInt.forEach { (_, u) ->
            u.resetRender()
        }
    }

    override fun delete() {
        removeAllTiles()
    }

    override fun unload() {
        val up = door.up
        val down = door.down
        val left = door.left
        val right = door.right
        dicoTilesInt.forEach { (_, tile) ->
            tile.delete()
        }
        dicoTilesInt.clear()
        dicoTilesInt[Pair(x, y)] = door
        door.up = up
        door.down = down
        door.left = left
        door.right = right
    }
}