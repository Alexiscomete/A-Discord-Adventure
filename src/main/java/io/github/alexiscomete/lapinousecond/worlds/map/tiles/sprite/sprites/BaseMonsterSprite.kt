package io.github.alexiscomete.lapinousecond.worlds.map.tiles.sprite.sprites

import io.github.alexiscomete.lapinousecond.worlds.map.tiles.sprite.SpriteWithIA
import io.github.alexiscomete.lapinousecond.worlds.map.tiles.types.DistanceWithPlayer
import io.github.alexiscomete.lapinousecond.worlds.map.tiles.types.MapTile
import io.github.alexiscomete.lapinousecond.worlds.map.tiles.types.Tile

abstract class BaseMonsterSprite : SpriteWithIA, BaseSprite() {
    private var currentDistance = 100

    override fun useIA() {
        // step 1 - check the distance of each MapTile around the monster. Take the minimum distance
        var minMapTile: Tile? = null
        var dist = 100
        var noTile: Tile? = null
        tile.left?.let {
            if (it is DistanceWithPlayer && it.currentDistance < currentDistance) {
                minMapTile = it
                dist = it.currentDistance
            } else {
                noTile = it
            }
        }
        tile.right?.let {
            if (it is MapTile && it.currentDistance < currentDistance && (minMapTile == null || it.currentDistance < dist)) {
                minMapTile = it
                dist = it.currentDistance
            } else {
                noTile = it
            }
        }
        tile.up?.let {
            if (it is MapTile && it.currentDistance < currentDistance && (minMapTile == null || it.currentDistance < dist)) {
                minMapTile = it
                dist = it.currentDistance
            } else {
                noTile = it
            }
        }
        tile.down?.let {
            if (it is MapTile && it.currentDistance < currentDistance && (minMapTile == null || it.currentDistance < dist)) {
                minMapTile = it
                dist = it.currentDistance
            } else {
                noTile = it
            }
        }
        if (minMapTile != null) {
            tile = minMapTile!!
            currentDistance = dist
        } else if (noTile != null) {
            tile = noTile!!
            currentDistance = 100
        }
    }
}