package io.github.alexiscomete.lapinousecond.worlds

import io.github.alexiscomete.lapinousecond.entity.entities.Player
import java.awt.image.BufferedImage

interface WorldManager {
    fun isLand(x: Double, y: Double): Boolean
    fun isLand(x: Int, y: Int, zoom: Zooms): Boolean
    @Deprecated("Please use WorldRenderScene instead")
    fun zoom(zoneToAdapt: ZoneToAdapt): BufferedImage
    @Deprecated("Please use WorldRenderScene instead")
    fun zoomWithDecorElements(
        zoneToAdapt: ZoneToAdapt,
        image: BufferedImage? = null,
        player: Player? = null,
        big: Boolean
    ): BufferedImage
    @Deprecated("Please use WorldRenderScene instead")
    fun uniqueTotalImage(): BufferedImage
    @Deprecated("Always return x")
    fun xImage(x: Int): Int
    @Deprecated("Always return y")
    fun yImage(y: Int): Int
    fun getHeight(x: Double, y: Double): Double
    fun getHeight(x: Int, y: Int, zoom: Zooms): Double
    fun isPath(x: Double, y: Double): Boolean
    fun pathLevel(x: Double, y: Double): Double
    fun isRiver(x: Double, y: Double): Boolean
    fun riverLevel(x: Double, y: Double): Double
}