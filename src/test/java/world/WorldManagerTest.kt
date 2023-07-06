package world

import io.github.alexiscomete.lapinousecond.entity.entities.Player
import io.github.alexiscomete.lapinousecond.worlds.WorldManager
import io.github.alexiscomete.lapinousecond.worlds.ZoneToAdapt
import io.github.alexiscomete.lapinousecond.worlds.Zooms
import java.awt.image.BufferedImage
import java.util.*

@Deprecated("This class is only used for testing purpose")
class WorldManagerTest : WorldManager {
    override fun isLand(x: Double, y: Double): Boolean {
        return getHeight(x, y) > 0.5
    }

    override fun isLand(x: Int, y: Int, zoom: Zooms): Boolean {
        return getHeight(x, y, zoom) > 0.5
    }

    override fun zoomWithDecorElements(zoneToAdapt: ZoneToAdapt, image: BufferedImage, player: Player?, big: Boolean): BufferedImage {
        return BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB)
    }

    override fun getHeight(x: Double, y: Double): Double {
        return Random().nextDouble()
    }

    override fun getHeight(x: Int, y: Int, zoom: Zooms): Double {
        return Random().nextDouble()
    }

    override fun isPath(x: Double, y: Double): Boolean {
        return false
    }

    override fun pathLevel(x: Double, y: Double): Double {
        return 0.0
    }

    override fun isRiver(x: Double, y: Double): Boolean {
        return false
    }

    override fun riverLevel(x: Double, y: Double): Double {
        return 0.0
    }
}