package io.github.alexiscomete.lapinousecond.worlds

import io.github.alexiscomete.lapinousecond.worlds.map.Node
import io.github.alexiscomete.lapinousecond.worlds.map.Pixel
import java.awt.Color
import java.awt.Font
import java.awt.Image
import java.awt.image.BufferedImage
import java.io.IOException
import javax.imageio.ImageIO
import kotlin.math.pow
import kotlin.math.sqrt

/**
 * Converts a given Image into a BufferedImage
 * thanks to [https://stackoverflow.com/questions/13605248/java-converting-image-to-bufferedimage](https://stackoverflow.com/questions/13605248/java-converting-image-to-bufferedimage)
 * @param img The Image to be converted
 * @return The converted BufferedImage
 */
private fun toBufferedImage(img: Image): BufferedImage {
    if (img is BufferedImage) {
        return img
    }

    // Create a buffered image with transparency
    val bimage = BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB)

    // Draw the image on to the buffered image
    val bGr = bimage.createGraphics()
    bGr.drawImage(img, 0, 0, null)
    bGr.dispose()

    // Return the buffered image
    return bimage
}

//return a bigger BufferedImage and ask size
fun bigger(image: BufferedImage, sizeMultiplier: Int): BufferedImage {
    return toBufferedImage(
        image.getScaledInstance(
            image.width * sizeMultiplier,
            image.height * sizeMultiplier,
            BufferedImage.SCALE_SMOOTH
        )
    )
}

enum class WorldEnum(
    val typeOfServer: String,
    val nameRP: String,
    val progName: String,
    val desc: String,
    val defaultX: Int,
    val defaultY: Int,
    val mapPath: String,
    val mapWidth: Int,
    val mapHeight: Int,
) {
    NORMAL(
        "Serveur normal",
        "Monde du chaos",
        "NORMAL",
        "Ce monde regroupe tous les serveurs discord qui ne sont pas sur le drapeau. (ex : Wiki, projet, etc.)",
        250,
        250,
        "NORMAL.png",
        500,
        500,
        ),
    DIBIMAP(
        "Serveur de territoire",
        "Monde du drapeau",
        "DIBIMAP",
        "Le serveur discord a un territoire sur le drapeau du Dibistan ? Alors c'est le monde du drapeau !",
        449,
        75,
        "DIBIMAP.png",
        528,
        272,
        ),
    TUTO(
        "Serveur du tutoriel",
        "Monde du tutoriel",
        "TUTO",
        "Ce monde est réservé au tutoriel du jeu",
        50,
        25,
        "TUTO.png",
        100,
        50,
        );

    val mapFile: BufferedImage? = try {
        ImageIO.read(
            World::class.java.classLoader.getResourceAsStream(mapPath)
        )
    } catch (e: IOException) {
        e.printStackTrace()
        null
    }

    fun getPixel(x: Int, y: Int): Pixel {
        return Pixel(x, y, mapWidth, mapHeight, mapFile!!)
    }

    // if pixel is Dirt
    fun isDirt(x: Int, y: Int): Boolean {
        return getPixel(x, y).isDirt
    }

    // zoom and return a BufferedImage
    private fun zoom(xArg: Int, yArg: Int, widthArg: Int, heightArg: Int): BufferedImage {
        var x = xArg
        var y = yArg
        var width = widthArg
        var height = heightArg
        val mapWidth = mapWidth
        val mapHeight = mapHeight
        if (x < width) {
            x = width
            // change the value of width if it is too big for the map
            if (x + width > mapWidth) {
                width = mapWidth - x
            }
        } else if (x + width > mapWidth) {
            x = mapWidth - width
        }
        if (y < height) {
            y = height
            // change the value of height if it is too big for the map
            if (y + height > mapHeight) {
                height = mapHeight - y
            }
        } else if (y + height > mapHeight) {
            y = mapHeight - height
        }
        return mapFile!!.getSubimage(
            x * mapFile.getWidth(null) / mapWidth,
            y * mapFile.getHeight(null) / mapHeight,
            width * mapFile.getWidth(null) / mapWidth,
            height * mapFile.getHeight(null) / mapHeight
        )
    }

    // zoom on coordinates (x, y) and return a BufferedImage
    fun zoom(x: Int, y: Int, zoom: Int): BufferedImage {
        return zoom(x - zoom * 2, y - zoom, zoom * 4, zoom * 2)
    }

    // return an image with the places' names on it
    fun getMapWithNames(
        places: ArrayList<Place>,
        xStart: Int,
        yStart: Int,
        width: Int,
        height: Int,
        image: BufferedImage
    ) {
        val g = image.createGraphics()
        g.color = Color(0, 255, 0)
        // set the text size
        g.font = Font("Arial", Font.BOLD, image.width / 80)
        val size = image.width / 80
        for (place in places) {
            if (place.getX().isPresent && place.getY().isPresent) {
                // coos on image (x, y) after resizing (x and y are not the same as the image's coos)
                val x = (place.getX().get() - xStart) * image.width / width
                val y = (place.getY().get() - yStart) * image.height / height
                // draw a point
                g.fillOval(x, y, (size * 0.7).toInt(), (size * 0.7).toInt())
                // draw the name
                try {
                    g.drawString(place.getString("name"), (x + 1.1 * size).toInt(), y)
                } catch (ignored: Exception) {
                }
                // draw the id
                try {
                    g.drawString(place.id.toString(), (x + 1.1 * size).toInt(), (y + 1.1 * size).toInt())
                } catch (ignored: Exception) {
                }
            }
        }
    }

    // --------------------
    // PATH FINDING with A*
    // --------------------

    // return the path from one pixel to another
    fun findPath(start: Node, end: Node): ArrayList<Pixel> {

        require(start.isDirt == end.isDirt) { "start and end must be on the same type of tile" }

        val closedList = ArrayList<Node>()
        val openList = ArrayList<Node>()
        openList.add(start)

        while (openList.isNotEmpty()) {
            var current = openList[0]
            // je cherche le nœud avec la plus petite heuristique
            for (node in openList) {
                if (node.heuristic < current.heuristic) {
                    current = node
                }
            }
            if (current == end) {
                val path = ArrayList<Pixel>()
                while (current.parent != null) {
                    path.add(current)
                    current = current.parent!!
                }
                path.reverse()
                return path
            }
            openList.remove(current)
            for (n in getConnectedNodes(current, openList)) {
                if (!openList.contains(n)) {
                    n.parent = current
                }
                if (!(openList.contains(n) && n.heuristic < current.heuristic
                            || closedList.contains(n))
                ) {
                    n.cost = current.cost + 1
                    n.heuristic = n.cost + distance(n, end)
                    n.parent = current
                    if (!openList.contains(n)) {
                        openList.add(n)
                    }
                }
            }
            closedList.add(current)
            require(openList.size <= 4000) { "Le chemin met trop de puissance à calculer, essayez en plusieurs fois" }
        }
        throw IllegalArgumentException("Aucun chemin trouvé")
    }

    // return connected pixels
    private fun getConnectedNodes(pixel: Node, nodes: ArrayList<Node>): ArrayList<Node> {
        val nodes2 = ArrayList<Node>()
        // ajout des pixels voisins un par un
        if (isInMap(pixel.x - 1, pixel.y - 1)) nodes2.add(
            getNode(
                pixel.x - 1, pixel.y - 1, nodes
            )
        )
        if (isInMap(pixel.x, pixel.y - 1)) nodes2.add(
            getNode(
                pixel.x, pixel.y - 1, nodes
            )
        )
        if (isInMap(pixel.x + 1, pixel.y - 1)) nodes2.add(
            getNode(
                pixel.x + 1, pixel.y - 1, nodes
            )
        )
        if (isInMap(pixel.x - 1, pixel.y)) nodes2.add(
            getNode(
                pixel.x - 1, pixel.y, nodes
            )
        )
        if (isInMap(pixel.x + 1, pixel.y)) nodes2.add(
            getNode(
                pixel.x + 1, pixel.y, nodes
            )
        )
        if (isInMap(pixel.x - 1, pixel.y + 1)) nodes2.add(
            getNode(
                pixel.x - 1, pixel.y + 1, nodes
            )
        )
        if (isInMap(pixel.x, pixel.y + 1)) nodes2.add(
            getNode(
                pixel.x, pixel.y + 1, nodes
            )
        )
        if (isInMap(pixel.x + 1, pixel.y + 1)) nodes2.add(
            getNode(
                pixel.x + 1, pixel.y + 1, nodes
            )
        )
        // pour chaque pixel on l'enlève si ce n'est pas de le même type que le pixel courant
        nodes2.removeIf { n: Node? -> n!!.isDirt != pixel.isDirt }
        return nodes2
    }

    // vérifie si le pixel est dans la map
    private fun isInMap(x: Int, y: Int): Boolean {
        return x in 0 until mapWidth && y in 0 until mapHeight
    }

    fun getNode(x: Int, y: Int, nodes: ArrayList<Node>): Node {
        val n = Node(x, y, mapWidth, mapHeight, mapFile!!, 0.0, 0.0)
        return if (nodes.contains(n)) {
            nodes[nodes.indexOf(n)]
        } else n
    }

    // distance between two pixels
    private fun distance(a: Pixel, b: Pixel): Double {
        return sqrt((a.x - b.x).toDouble().pow(2.0) + (a.y - b.y).toDouble().pow(2.0))
    }

    fun drawPath(path: ArrayList<Pixel>): BufferedImage {
        val img = BufferedImage(mapFile!!.width, mapFile.height, BufferedImage.TYPE_INT_ARGB)
        img.data = mapFile.data
        for (p in path) {
            img.setRGB(p.xImage, p.yImage, Color.RED.rgb)
        }
        return img
    }
}