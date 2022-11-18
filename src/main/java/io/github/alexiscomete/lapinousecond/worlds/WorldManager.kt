package io.github.alexiscomete.lapinousecond.worlds

import io.github.alexiscomete.lapinousecond.entity.Player
import java.awt.image.BufferedImage

interface WorldManager {
    fun isLand(x: Int, y: Int): Boolean
    fun zoom(zoneToAdapt: WorldEnum.ZoneToAdapt): BufferedImage
    fun zoomWithCity(zoneToAdapt: WorldEnum.ZoneToAdapt, progName: String, player: Player? = null): BufferedImage
}