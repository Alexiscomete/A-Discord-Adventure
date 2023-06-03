package io.github.alexiscomete.lapinousecond.worlds.map.tiles

import io.github.alexiscomete.lapinousecond.worlds.map.tiles.sprite.Sprite
import java.awt.Color

interface Tile {

    val x: Int
    val y: Int
    var up: Tile?
    var down: Tile?
    var left: Tile?
    var right: Tile?

    fun delete(worldRenderScene: WorldRenderScene)

    /**
     * Aucun argument requis. Permet de réinitialiser les états de chaque tuile.
     */
    fun render(worldRenderScene: WorldRenderScene, xToUse: Int, yToUse: Int, distance: Int)

    fun addToRenderQueue(worldRenderScene: WorldRenderScene, x: Int, y: Int, distance: Int)

    fun letter(): Char

    fun color(): Color
    fun texture(): Array<Array<Color>>

    fun isWalkable(): Boolean
    fun removeSprite(sprite: Sprite)

    fun resetRender()
}