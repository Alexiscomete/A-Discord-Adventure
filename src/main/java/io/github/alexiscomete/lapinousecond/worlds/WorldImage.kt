package io.github.alexiscomete.lapinousecond.worlds

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
}