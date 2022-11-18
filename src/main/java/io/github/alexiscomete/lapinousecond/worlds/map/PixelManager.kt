package io.github.alexiscomete.lapinousecond.worlds.map

import io.github.alexiscomete.lapinousecond.worlds.WorldManager

open class PixelManager(val x: Int, val y: Int, protected val worldManager: WorldManager) {
    val isLanded = worldManager.isLand(x, y)
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is PixelManager) return false

        if (x != other.x) return false
        if (y != other.y) return false
        if (worldManager != other.worldManager) return false

        return true
    }

    override fun hashCode(): Int {
        var result = x
        result = 31 * result + y
        result = 31 * result + worldManager.hashCode()
        return result
    }

    override fun toString(): String {
        return "PixelManager(x=$x, y=$y, worldManager=$worldManager, isLanded=$isLanded)"
    }
}
