package io.github.alexiscomete.lapinousecond.worlds

import io.github.alexiscomete.lapinousecond.entity.Player
import java.awt.Color
import java.awt.image.BufferedImage
import java.io.IOException
import javax.imageio.ImageIO

class WorldImage(
    mapPath: String
) : WorldManager {
    private val mapFile: BufferedImage? = try {
        ImageIO.read(
            WorldEnum::class.java.classLoader.getResourceAsStream(mapPath)
        )
    } catch (e: IOException) {
        e.printStackTrace()
        null
    }
    private val xMax = mapFile?.width ?: 0
    private val yMax = mapFile?.height ?: 0

    override fun isLand(x: Int, y: Int): Boolean {
        val xImage = x * mapFile!!.getWidth(null) / xMax
        val yImage = y * mapFile.getHeight(null) / yMax
        // permet de récupérer la couleur du pixel sur l'image
        val color = Color(mapFile.getRGB(xImage, yImage))
        // permet de savoir si le pixel est un sol ou non
        return color.blue <= (color.red.toFloat() + color.green.toFloat()).toInt() / 1.5
    }

    override fun isLand(x: Double, y: Double): Boolean {
        // si le pixel est directement une valeur entière, retourner la valeur du pixel
        if (x == x.toInt().toDouble() && y == y.toInt().toDouble()) {
            return isLand(x.toInt(), y.toInt())
        }
        // sinon, faire une moyenne des 4 pixels autour
        val x1 = x.toInt()
        val y1 = y.toInt()
        val x2 = x1 + 1
        val y2 = y1 + 1
        val x1y1 = isLand(x1, y1)
        val x1y2 = isLand(x1, y2)
        val x2y1 = isLand(x2, y1)
        val x2y2 = isLand(x2, y2)
        val x1y1Weight = (x2 - x) * (y2 - y)
        val x1y2Weight = (x2 - x) * (y - y1)
        val x2y1Weight = (x - x1) * (y2 - y)
        val x2y2Weight = (x - x1) * (y - y1)
        return (x1y1Weight * (if (x1y1) 1 else 0)
                + x1y2Weight * (if (x1y2) 1 else 0)
                + x2y1Weight * (if (x2y1) 1 else 0)
                + x2y2Weight * (if (x2y2) 1 else 0)
                ) > 0.5
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
        return cloneBufferedImage(mapFile!!)
    }

    override fun xImage(x: Int): Int {
        return x * mapFile!!.getWidth(null) / xMax
    }

    override fun yImage(y: Int): Int {
        return y * mapFile!!.getHeight(null) / yMax
    }
}