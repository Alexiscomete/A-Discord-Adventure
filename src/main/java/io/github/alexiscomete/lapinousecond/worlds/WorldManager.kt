package io.github.alexiscomete.lapinousecond.worlds

import io.github.alexiscomete.lapinousecond.Beurk
import io.github.alexiscomete.lapinousecond.entity.entities.Player
import java.awt.image.BufferedImage

interface WorldManager {
    fun isLand(x: Double, y: Double): Boolean
    fun isLand(x: Int, y: Int, zoom: Zooms): Boolean

    @Beurk
    fun zoomWithDecorElements(
        zoneToAdapt: ZoneToAdapt,
        image: BufferedImage,
        player: Player? = null,
        big: Boolean
    ): BufferedImage

    fun getHeight(x: Double, y: Double): Double
    fun getHeight(x: Int, y: Int, zoom: Zooms): Double
    fun isPath(x: Double, y: Double): Boolean
    fun pathLevel(x: Double, y: Double): Double
    fun isRiver(x: Double, y: Double): Boolean
    fun riverLevel(x: Double, y: Double): Double
}