package io.github.alexiscomete.lapinousecond.worlds

import io.github.alexiscomete.lapinousecond.entity.Player
import procedural_generation.noise.ComplexNoise
import java.awt.Color
import java.awt.image.BufferedImage

class WorldProcedural(
    private val complexNoise: ComplexNoise,
    private val maxX: Int,
    private val maxY: Int
) : WorldManager {
    override fun isLand(x: Int, y: Int): Boolean {
        return complexNoise.getValue(x.toDouble(), y.toDouble()) > 0.5
    }

    override fun isLand(x: Double, y: Double): Boolean {
        return complexNoise.getValue(x, y) > 0.5
    }

    override fun isLand(x: Int, y: Int, zoom: Zooms): Boolean {
        val (x1, y1) = zoom.zoomOutTo(Zooms.ZOOM_OUT, x.toDouble(), y.toDouble())
        return isLand(x1, y1)
    }

    override fun zoom(zoneToAdapt: ZoneToAdapt): BufferedImage {
        // generate the image
        val image = BufferedImage(zoneToAdapt.width, zoneToAdapt.height, BufferedImage.TYPE_INT_RGB)
        // fill the image
        for (x in zoneToAdapt.x until zoneToAdapt.x + zoneToAdapt.width) {
            for (y in zoneToAdapt.y until zoneToAdapt.y + zoneToAdapt.height) {
                val color = if (isLand(x + zoneToAdapt.x, y + zoneToAdapt.y, zoneToAdapt.zoom)) 0x704A40 else 0x4D759D
                image.setRGB(x - zoneToAdapt.x, y - zoneToAdapt.y, color)
            }
        }
        return image
    }

    override fun zoomWithDecorElements(zoneToAdapt: ZoneToAdapt, progName: String, player: Player?): BufferedImage {
        // generate the image
        var image = zoom(zoneToAdapt)
        // add the player
        if (player != null) {
            if (player["place_${progName}_zoom"] != zoneToAdapt.zoom.name) {
                val (x, y) = try {
                    zoneToAdapt.zoom.zoomInTo(Zooms.valueOf(player["place_${progName}_zoom"]), player["place_${progName}_x"].toInt(), player["place_${progName}_y"].toInt())
                } catch (e: Exception) {
                    zoneToAdapt.zoom.zoomOutTo(Zooms.valueOf(player["place_${progName}_zoom"]), player["place_${progName}_x"].toInt(), player["place_${progName}_y"].toInt())
                }
                image.setRGB(x - zoneToAdapt.x, y - zoneToAdapt.y, Color.RED.rgb)
            } else {
                image.setRGB(player["place_${progName}_x"].toInt() - zoneToAdapt.x, player["place_${progName}_y"].toInt() - zoneToAdapt.y, Color.RED.rgb)
            }
        }

        image = bigger(image, 10)

        val places = Place.getPlacesWithWorld(progName)

        places.removeIf { place: Place ->
            if (!place.getX().isPresent || !place.getY().isPresent) {
                return@removeIf true
            }
            val (x, y) = Zooms.ZOOM_OUT.zoomInTo(zoneToAdapt.zoom, place.getX().get(), place.getY().get())
            (x < zoneToAdapt.x || x > zoneToAdapt.x + zoneToAdapt.width || y < zoneToAdapt.y || y > zoneToAdapt.y + zoneToAdapt.height)
        }

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
                val color = if (isLand(x, y)) 0x704A40 else 0x4D759D
                image.setRGB(x, y, color)
            }
        }
        return image
    }

    override fun xImage(x: Int): Int {
        return x
    }

    override fun yImage(y: Int): Int {
        return y
    }
}