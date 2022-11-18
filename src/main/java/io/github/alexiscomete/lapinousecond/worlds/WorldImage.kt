package io.github.alexiscomete.lapinousecond.worlds

import io.github.alexiscomete.lapinousecond.entity.Player
import java.awt.Color
import java.awt.image.BufferedImage
import java.io.IOException
import javax.imageio.ImageIO

class WorldImage(mapPath: String) : WorldManager {
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

    override fun zoom(zoneToAdapt: WorldEnum.ZoneToAdapt): BufferedImage {
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

    override fun zoomWithCity(zoneToAdapt: WorldEnum.ZoneToAdapt, progName: String, player: Player?): BufferedImage {
        var image = cloneBufferedImage(mapFile!!)
        if (player != null) {
            image.setRGB(player["place_${progName}_x"].toInt(), player["place_${progName}_y"].toInt(), Color.RED.rgb)
        }

        image = strictZoom(zoneToAdapt, image)
        image = bigger(image, 10)

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
}