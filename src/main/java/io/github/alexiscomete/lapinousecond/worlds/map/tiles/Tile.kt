package io.github.alexiscomete.lapinousecond.worlds.map.tiles

import java.awt.Color

interface Tile {

    val x: Int
    val y: Int
    var up: Tile?
    var down: Tile?
    var left: Tile?
    var right: Tile?

    fun delete()

    fun renderRecursive(remainingSteps: Int, worldRenderScene: WorldRenderScene, xToUse: Int, yToUse: Int)

    /**
     * Aucun argument requis. Permet de réinitialiser les états de chaque tuile.
     */
    fun resetRecursive()
    fun render(worldRenderScene: WorldRenderScene, x: Int, y: Int)

    fun letter(): Char

    fun color(): Color

    fun isWalkable(): Boolean
}