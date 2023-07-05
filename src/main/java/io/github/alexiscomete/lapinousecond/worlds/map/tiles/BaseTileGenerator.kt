package io.github.alexiscomete.lapinousecond.worlds.map.tiles

import io.github.alexiscomete.lapinousecond.worlds.THRESHOLD_PATH
import io.github.alexiscomete.lapinousecond.worlds.THRESHOLD_RIVER
import io.github.alexiscomete.lapinousecond.worlds.WorldManager
import io.github.alexiscomete.lapinousecond.worlds.Zooms
import io.github.alexiscomete.lapinousecond.worlds.map.tiles.multitiles.MultiTilesManager
import io.github.alexiscomete.lapinousecond.worlds.map.tiles.multitiles.templating.contexts.TemplateWorld
import io.github.alexiscomete.lapinousecond.worlds.map.tiles.multitiles.templating.managers.EmptyRoom
import io.github.alexiscomete.lapinousecond.worlds.map.tiles.types.MapTile
import io.github.alexiscomete.lapinousecond.worlds.map.tiles.types.OCEAN_HEIGHT
import io.github.alexiscomete.lapinousecond.worlds.map.tiles.types.TreeTrunk

class BaseTileGenerator(
    private val zoomLevel: Zooms,
    val world: WorldManager,
) : TileGenerator {
    private val multiTilesManagers = mutableListOf<MultiTilesManager>()
    private var dicoTiles = mutableMapOf<Pair<Int, Int>, Tile>()
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
                )
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