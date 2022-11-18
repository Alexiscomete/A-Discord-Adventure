package io.github.alexiscomete.lapinousecond.worlds

import procedural_generation.noise.ComplexNoise
import java.awt.image.BufferedImage

class WorldProcedural(private val complexNoise: ComplexNoise) : WorldManager {
    override fun isLand(x: Int, y: Int): Boolean {
        return complexNoise.getValue(x.toDouble(), y.toDouble()) > 0.5
    }

    override fun zoom(zoneToAdapt: WorldEnum.ZoneToAdapt): BufferedImage {
        TODO("Not yet implemented")
    }
}