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
                image.setRGB(x - zoneToAdapt.x, y - zoneToAdapt.y, findColor(x + zoneToAdapt.x, y + zoneToAdapt.y, zoneToAdapt.zoom))
            }
        }
        return image
    }

    override fun zoomWithDecorElements(zoneToAdapt: ZoneToAdapt, progName: String, player: Player?): BufferedImage {
        // generate the image
        var image = zoom(zoneToAdapt)
        println(zoneToAdapt)
        // add the player
        if (player != null) {
            if (player["place_${progName}_zoom"] == "") {
                player["place_${progName}_zoom"] = Zooms.ZOOM_OUT.name
            }
            if (player["place_${progName}_zoom"] != zoneToAdapt.zoom.name) {
                val (x, y) = try {
                    zoneToAdapt.zoom.zoomInTo(
                        Zooms.valueOf(player["place_${progName}_zoom"]),
                        player["place_${progName}_x"].toInt(),
                        player["place_${progName}_y"].toInt()
                    )
                } catch (e: Exception) {
                    zoneToAdapt.zoom.zoomOutTo(
                        Zooms.valueOf(player["place_${progName}_zoom"]),
                        player["place_${progName}_x"].toInt(),
                        player["place_${progName}_y"].toInt()
                    )
                }
                image.setRGB(x - zoneToAdapt.x, y - zoneToAdapt.y, Color.RED.rgb)
            } else {
                image.setRGB(
                    player["place_${progName}_x"].toInt() - zoneToAdapt.x,
                    player["place_${progName}_y"].toInt() - zoneToAdapt.y,
                    Color.RED.rgb
                )
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
                image.setRGB(x, y, findColor(x, y, Zooms.ZOOM_OUT))
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

    override fun getHeight(x: Double, y: Double): Double {
        return complexNoise.getValue(x, y)
    }

    override fun getHeight(x: Int, y: Int, zoom: Zooms): Double {
        val (x1, y1) = zoom.zoomOutTo(Zooms.ZOOM_OUT, x.toDouble(), y.toDouble())
        println("x: $x, y: $y, x1: $x1, y1: $y1")
        return getHeight(x1, y1)
    }

    private fun findColor(x: Int, y: Int, zooms: Zooms): Int {
        val color: Int = (getHeight(x, y, zooms) * 255).toInt()
        var blue = 0
        var green = 0
        var red = 0
        if (color > 127) {
            if (color > 128) {
                green = 255 - color
                if (color > 191) {
                    red = 255 - color
                    if (color > 224) {
                        blue = color
                        green = color
                    }
                }
            } else {
                return Color(245, 245, 66).rgb
            }
        } else {
            blue = color
        }
        return Color(red, green, blue).rgb
    }
}