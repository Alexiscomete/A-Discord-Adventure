package io.github.alexiscomete.lapinousecond.worlds.map

import io.github.alexiscomete.lapinousecond.worlds.WorldManager

data class PixelManager(val x: Int, val y: Int, private val worldManager: WorldManager) {
    fun isLand(): Boolean {
        return worldManager.isLand(x, y)
    }
}
