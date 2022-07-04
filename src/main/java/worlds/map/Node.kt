package worlds.map

import java.awt.image.BufferedImage

class Node(x: Int, y: Int, xMax: Int, yMax: Int, image: BufferedImage, var cost: Double, var heuristic: Double) :
    Pixel(x, y, xMax, yMax, image) {
    var parent: Node? = null

    operator fun compareTo(o: Any): Int {
        val node = o as Node
        return heuristic.compareTo(node.heuristic)
    }
}