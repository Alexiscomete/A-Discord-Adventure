package io.github.alexiscomete.lapinousecond.worlds

import io.github.alexiscomete.lapinousecond.entity.Player
import java.awt.image.BufferedImage

interface WorldManager {
    fun isLand(x: Double, y: Double): Boolean
    fun isLand(x: Int, y: Int, zoom: Zooms): Boolean
    fun zoom(zoneToAdapt: ZoneToAdapt): BufferedImage
    fun zoomWithDecorElements(zoneToAdapt: ZoneToAdapt, progName: String, player: Player? = null): BufferedImage
    fun uniqueTotalImage(): BufferedImage
    fun xImage(x: Int): Int
    fun yImage(y: Int): Int
    fun getHeight(x: Double, y: Double): Double
    fun getHeight(x: Int, y: Int, zoom: Zooms): Double
    fun isPath(x: Double, y: Double): Boolean
}