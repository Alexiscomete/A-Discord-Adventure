package io.github.alexiscomete.lapinousecond.worlds

import io.github.alexiscomete.lapinousecond.entity.Player
import procedural_generation.noise.ComplexNoise
import java.awt.Color
import java.awt.image.BufferedImage

class WorldProcedural(private val complexNoise: ComplexNoise, private val maxX: Int, private val maxY: Int) : WorldManager {
    override fun isLand(x: Int, y: Int): Boolean {
        return complexNoise.getValue(x.toDouble(), y.toDouble()) > 0.5
    }

    override fun zoom(zoneToAdapt: WorldEnum.ZoneToAdapt): BufferedImage {
        // generate the image
        val image = BufferedImage(zoneToAdapt.width, zoneToAdapt.height, BufferedImage.TYPE_INT_RGB)
        // fill the image
        for (x in 0 until zoneToAdapt.width) {
            for (y in 0 until zoneToAdapt.height) {
                val color = if (isLand(x + zoneToAdapt.x, y + zoneToAdapt.y)) 0 else 0xFFFFFF
                image.setRGB(x, y, color)
            }
        }
        return image
    }

    override fun zoomWithCity(zoneToAdapt: WorldEnum.ZoneToAdapt, progName: String, player: Player?): BufferedImage {
        // generate the image
        var image = uniqueTotalImage()
        // add the player
        if (player != null) {
            image.setRGB(player["place_${progName}_x"].toInt(), player["place_${progName}_y"].toInt(), Color.RED.rgb)
        }

        image = bigger(strictZoom(zoneToAdapt, image), 10)

        val places = Place.getPlacesWithWorld(progName)
        println(places.size)

        places.removeIf { place: Place ->
            !place.getX().isPresent || !place.getY().isPresent
                    || place.getX().get() < zoneToAdapt.x
                    || place.getX().get() > zoneToAdapt.x + zoneToAdapt.width
                    || place.getY().get() < zoneToAdapt.y
                    || place.getY().get() > zoneToAdapt.y + zoneToAdapt.height
        }

        println(places.size)

        getMapWithNames(
            places,
            zoneToAdapt,
            image
        )

        return image
    }

    override fun uniqueTotalImage(): BufferedImage {
        // generate the image
        val image = BufferedImage(maxX, maxY, BufferedImage.TYPE_INT_RGB)
        // fill the image
        for (x in 0 until maxX) {
            for (y in 0 until maxY) {
                val color = if (isLand(x, y)) 0 else 0xFFFFFF
                image.setRGB(x, y, color)
            }
        }
        return image
    }
}