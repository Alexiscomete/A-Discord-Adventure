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
        val x = zoneToAdapt.x
        val y = zoneToAdapt.y
        val width = zoneToAdapt.width
        val height = zoneToAdapt.height
        return mapFile!!.getSubimage(
            x * mapFile.getWidth(null) / zoneToAdapt.maxX,
            y * mapFile.getHeight(null) / zoneToAdapt.maxY,
            width * mapFile.getWidth(null) / zoneToAdapt.maxX,
            height * mapFile.getHeight(null) / zoneToAdapt.maxY
        )
    }

    override fun zoomWithDecorElements(zoneToAdapt: ZoneToAdapt, progName: String, player: Player?): BufferedImage {
        var image = cloneBufferedImage(mapFile!!)
        if (player != null) {
            image.setRGB(player["place_${progName}_x"].toInt(), player["place_${progName}_y"].toInt(), Color.RED.rgb)
        }

        image = strictZoom(zoneToAdapt, image)
        image = bigger(image, 10)

        val places = Place.getPlacesWithWorld(progName)
        println(places.size)

        places.removeIf { place: Place ->
            !place.getX().isPresent || !place.getY().isPresent || place.getX().get() < zoneToAdapt.x || place.getX()
                .get() > zoneToAdapt.x + zoneToAdapt.width || place.getY().get() < zoneToAdapt.y || place.getY()
                .get() > zoneToAdapt.y + zoneToAdapt.height
        }

        println(places.size)

        getMapWithNames(
            places, zoneToAdapt, image
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