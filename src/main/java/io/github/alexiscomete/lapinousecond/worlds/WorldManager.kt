package io.github.alexiscomete.lapinousecond.worlds

import io.github.alexiscomete.lapinousecond.entity.Player
import java.awt.image.BufferedImage

interface WorldManager {
    fun isLand(x: Int, y: Int): Boolean
    fun isLand(x: Double, y: Double): Boolean
    fun isLand(x: Int, y: Int, zoom: Zooms): Boolean
    fun zoom(zoneToAdapt: WorldEnum.ZoneToAdapt): BufferedImage
    fun zoomWithCity(zoneToAdapt: WorldEnum.ZoneToAdapt, progName: String, player: Player? = null): BufferedImage
    fun uniqueTotalImage(): BufferedImage
    fun xImage(x: Int): Int
    fun yImage(y: Int): Int
}