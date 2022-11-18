package io.github.alexiscomete.lapinousecond.worlds

import java.awt.image.BufferedImage

interface WorldManager {
    fun isLand(x: Int, y: Int): Boolean
    fun zoom(zoneToAdapt: WorldEnum.ZoneToAdapt): BufferedImage
}