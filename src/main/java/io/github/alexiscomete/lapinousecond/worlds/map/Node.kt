package io.github.alexiscomete.lapinousecond.worlds.map

import io.github.alexiscomete.lapinousecond.worlds.WorldManager

class Node(x: Int, y: Int, worldManager: WorldManager, var cost: Double, var heuristic: Double) :
    PixelManager(x, y, worldManager) {
    var parent: Node? = null

    operator fun compareTo(o: Any): Int {
        val node = o as Node
        return heuristic.compareTo(node.heuristic)
    }
}