package io.github.alexiscomete.lapinousecond.worlds.map.tiles.sprite.sprites

import io.github.alexiscomete.lapinousecond.entity.entities.PlayerManager
import io.github.alexiscomete.lapinousecond.worlds.map.tiles.sprite.Sprite
import io.github.alexiscomete.lapinousecond.worlds.map.tiles.sprite.SpriteSpawner
import io.github.alexiscomete.lapinousecond.worlds.map.tiles.textures.TexturesForSprites
import io.github.alexiscomete.lapinousecond.worlds.map.tiles.types.DistanceWithPlayer
import io.github.alexiscomete.lapinousecond.worlds.map.tiles.types.MapTile
import io.github.alexiscomete.lapinousecond.worlds.map.tiles.types.Tile
import java.awt.Color

class ExtendingAttackSprite(
    player: PlayerManager,
    baseLife: Int,
    baseAttack: Int,
    baseColor: Color,
    baseLetter: Char,
    baseTexture: TexturesForSprites,
    tile: Tile,
    private val attackDistance: Int
) : BaseStaticSprite(
    player,
    baseLife,
    baseAttack,
    baseColor,
    baseLetter,
    baseTexture,
    tile
), SpriteSpawner {
    private var spwanOK = true

    fun findTile(): Tile {
        var minMapTile: Tile? = null
        var dist = if (tile is DistanceWithPlayer) (tile as DistanceWithPlayer).currentDistance else 100
        var noTile: Tile? = null
        tile.left?.let {
            if (it is DistanceWithPlayer && it.currentDistance <= dist) {
                minMapTile = it
                dist = it.currentDistance
            } else {
                noTile = it
            }
        }
        tile.right?.let {
            if (it is MapTile && it.currentDistance <= dist) {
                minMapTile = it
                dist = it.currentDistance
            } else {
                noTile = it
            }
        }
        tile.up?.let {
            if (it is MapTile && it.currentDistance < dist) {
                minMapTile = it
                dist = it.currentDistance
            } else {
                noTile = it
            }
        }
        tile.down?.let {
            if (it is MapTile && it.currentDistance < dist) {
                minMapTile = it
                dist = it.currentDistance
            } else {
                noTile = it
            }
        }
        if (minMapTile != null) {
            return minMapTile!!
        } else if (noTile != null) {
            return noTile!!
        }
        return tile
    }

    override fun spritesToSpawn(): List<Sprite> {
        if (!spwanOK) return listOf()
        spwanOK = false
        if (attackDistance > 0) {
            val ext = ExtendingAttackSprite(
                player,
                baseLife,
                baseAttack,
                baseColor,
                baseLetter,
                baseTexture,
                findTile(),
                attackDistance - 1
            )
            return ext.spritesToSpawn() + listOf(ext)
        } else {
            return listOf(
                BaseStaticSprite(
                    player,
                    baseLife,
                    baseAttack,
                    baseColor,
                    baseLetter,
                    baseTexture,
                    findTile()
                )
            )
        }
    }
}
