package io.github.alexiscomete.lapinousecond.worlds.map.tiles

import io.github.alexiscomete.lapinousecond.entity.entities.Player
import io.github.alexiscomete.lapinousecond.worlds.THRESHOLD_PATH
import io.github.alexiscomete.lapinousecond.worlds.THRESHOLD_RIVER
import io.github.alexiscomete.lapinousecond.worlds.WorldManager
import io.github.alexiscomete.lapinousecond.worlds.Zooms
import io.github.alexiscomete.lapinousecond.worlds.map.tiles.sprite.SpritesManager
import io.github.alexiscomete.lapinousecond.worlds.map.tiles.sprite.sprites.DuckSprite
import io.github.alexiscomete.lapinousecond.worlds.map.tiles.sprite.sprites.LootSprite
import io.github.alexiscomete.lapinousecond.worlds.map.tiles.sprite.sprites.SlimeSprite
import io.github.alexiscomete.lapinousecond.worlds.map.tiles.types.MapTile
import io.github.alexiscomete.lapinousecond.worlds.map.tiles.types.OCEAN_HEIGHT
import io.github.alexiscomete.lapinousecond.worlds.map.tiles.types.Tile
import io.github.alexiscomete.lapinousecond.worlds.map.tiles.types.TreeTrunk
import io.github.alexiscomete.lapinousecond.worlds.map.tiles.types.multitiles.MultiTilesManager
import io.github.alexiscomete.lapinousecond.worlds.map.tiles.types.multitiles.templating.contexts.TemplateWorld
import io.github.alexiscomete.lapinousecond.worlds.map.tiles.types.multitiles.templating.managers.EmptyRoom

const val TREES_DEFAULT_SIZE = 2
const val TREE_DISTANCE_WITH_ELEMENTS = 0.25
const val EMPTY_ROOM_DISTANCE_WITH_ELEMENTS = 0.1
const val EMPTY_ROOM_MAX_DISTANCE_WITH_PATH = 0.3
val EMPTY_ROOM_PATH_LEVEL =
    (THRESHOLD_PATH + EMPTY_ROOM_DISTANCE_WITH_ELEMENTS)..(THRESHOLD_PATH + EMPTY_ROOM_MAX_DISTANCE_WITH_PATH)

class BaseTileGenerator(
    private val zoomLevel: Zooms,
    val world: WorldManager,
    val player: Player?
) : TileGenerator {
    private val multiTilesManagers = mutableListOf<MultiTilesManager>()
    private var dicoTiles = mutableMapOf<Pair<Int, Int>, Tile>()
    override val spritesManager: SpritesManager = SpritesManager()

    override fun getOrGenerateTileAt(x: Int, y: Int): Tile {
        var tile: Tile? = null
        multiTilesManagers.forEach { if (it.hasTileAt(x, y)) tile = it.baseTileAt(x, y) }
        if (tile != null) return tile!!
        return dicoTiles.getOrPut(Pair(x, y)) {
            if (zoomLevel == Zooms.ZOOM_IN) {
                when ((0..120).random()) {
                    in 0..4 -> {
                        if (
                            world.getHeight(x, y, zoomLevel) > OCEAN_HEIGHT
                            && world.pathLevel(
                                x.toDouble(),
                                y.toDouble()
                            ) > THRESHOLD_PATH + TREE_DISTANCE_WITH_ELEMENTS
                            && world.riverLevel(
                                x.toDouble(),
                                y.toDouble()
                            ) > THRESHOLD_RIVER + TREE_DISTANCE_WITH_ELEMENTS
                        ) {
                            return@getOrPut TreeTrunk(x, y, TREES_DEFAULT_SIZE)
                        }
                    }

                    10 -> {
                        if (
                            world.getHeight(x, y, zoomLevel) > OCEAN_HEIGHT
                            && world.pathLevel(
                                x.toDouble(),
                                y.toDouble()
                            ) in EMPTY_ROOM_PATH_LEVEL
                            && world.riverLevel(
                                x.toDouble(),
                                y.toDouble()
                            ) > THRESHOLD_RIVER + EMPTY_ROOM_DISTANCE_WITH_ELEMENTS
                        ) {
                            val manager = EmptyRoom(6, TemplateWorld.WHITE, x, y)
                            multiTilesManagers.add(manager)
                            manager.load()
                            return@getOrPut manager.baseTileAt(x, y)
                        }
                    }
                }
                MapTile(
                    x,
                    y,
                    world.getHeight(x, y, zoomLevel),
                    world.isPath(x.toDouble(), y.toDouble()),
                    world.isRiver(x.toDouble(), y.toDouble())
                ).also {
                    if (it.isRiver) {
                        if ((0..100).random() == 5) {
                            spritesManager.sprites.add(DuckSprite(it))
                        }
                    } else {
                        when ((0..200).random()) {
                            5 -> {
                                if (player != null) spritesManager.sprites.add(LootSprite(it, player))
                            }
                            in 6..10 -> {
                                spritesManager.sprites.add(SlimeSprite(it))
                            }
                        }
                    }
                }
            } else {
                MapTile(x, y, world.getHeight(x, y, zoomLevel))
            }
        }
    }

    override fun updateCache() {
        val toDelete = mutableListOf<MutableMap.MutableEntry<Pair<Int, Int>, Tile>>()
        for (entry in dicoTiles.entries) {
            if (entry.value.isRendered()) {
                entry.value.resetRender()
            } else {
                toDelete.add(entry)
            }
        }
        toDelete.forEach {
            it.value.delete()
            spritesManager.spritesOnTile(it.value).forEach { s -> spritesManager.sprites.remove(s) }
            dicoTiles.remove(it.key)
        }
        val toDelete2 = mutableListOf<MultiTilesManager>()
        multiTilesManagers.forEach {
            if (it.canBeRemoved()) {
                it.delete()
                toDelete2.add(it)
            }
            it.resetIAmLoaded()
        }
        toDelete2.forEach { multiTilesManagers.remove(it) }
    }
}