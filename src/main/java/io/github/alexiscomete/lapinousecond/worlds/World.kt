package io.github.alexiscomete.lapinousecond.worlds

import io.github.alexiscomete.lapinousecond.worlds.map.Map
import java.awt.image.BufferedImage
import java.io.IOException
import javax.imageio.ImageIO

open class World(
    val typeOfServer: String,
    val name: String,
    val progName: String,
    val desc: String,
    val defaultX: Int,
    val defaultY: Int,
    val mapPath: String,
    val mapWidth: Int,
    val mapHeight: Int,
    val map: Map,
) {
    val mapFile: BufferedImage? = try {
        ImageIO.read(
            World::class.java.classLoader.getResourceAsStream(mapPath)
        )
    } catch (e: IOException) {
        e.printStackTrace()
        null
    }
}