package io.github.alexiscomete.lapinousecond.worlds

import io.github.alexiscomete.lapinousecond.entity.Player
import io.github.alexiscomete.lapinousecond.worlds.map.Node
import io.github.alexiscomete.lapinousecond.worlds.map.PixelManager
import java.awt.Color
import java.awt.Font
import java.awt.Image
import java.awt.image.BufferedImage
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
            image.width * sizeMultiplier, image.height * sizeMultiplier, BufferedImage.SCALE_SMOOTH
        )
    )
}

/**
 * Clone a buffered image
 * Source : https://stackoverflow.com/questions/3514158/how-do-you-clone-a-bufferedimage
 *
 * @param bi The buffered image to clone
 * @return The cloned buffered image
 */
fun cloneBufferedImage(bi: BufferedImage): BufferedImage {
    val cm = bi.colorModel
    val isAlphaPremultiplied = cm.isAlphaPremultiplied
    val raster = bi.copyData(bi.raster.createCompatibleWritableRaster())
    return BufferedImage(cm, raster, isAlphaPremultiplied, null)
}

enum class WorldEnum(
    val typeOfServer: String,
    val nameRP: String,
    val progName: String,
    val desc: String,
    val defaultX: Int,
    val defaultY: Int,
    val mapWidth: Int,
    val mapHeight: Int,
    val worldManager: WorldManager
) {
    NORMAL(
        "Serveur normal",
        "Monde du chaos",
        "NORMAL",
        "Ce monde regroupe tous les serveurs discord qui ne sont pas sur le drapeau. (ex : Wiki, projet, etc.)",
        250,
        250,
        500,
        500,
        WorldProcedural()
    ),
    DIBIMAP(
        "Serveur de territoire",
        "Monde du drapeau",
        "DIBIMAP",
        "Le serveur discord a un territoire sur le drapeau du Dibistan ? Alors c'est le monde du drapeau !",
        449,
        75,
        528,
        272,
        WorldImage("DIBIMAP.png")
    ),
    TUTO(
        "Serveur du tutoriel",
        "Monde du tutoriel",
        "TUTO",
        "Ce monde est réservé au tutoriel du jeu",
        50,
        25,
        100,
        50,
        WorldImage("TUTO.png")
    );

    class ZoneToAdapt(var x: Int, var y: Int, var width: Int, var height: Int, maxX: Int, maxY: Int) {
        init {
            if (x < 0) {
                x = 0
                // change the value of width if it is too big for the map
                if (width > maxX) {
                    width = maxX
                }
            } else if (x + width > maxX) {
                x = maxX - width
                if (x < 0) {
                    x = 0
                    width = maxX
                }
            }
            if (y < 0) {
                y = 0
                // change the value of height if it is too big for the map
                if (height > maxY) {
                    height = maxY
                }
            } else if (y + height > maxY) {
                y = maxY - height
                if (y < 0) {
                    y = 0
                    height = maxY
                }
            }
        }
    }

    val START_X = 1
    val START_Y = 1

    /**
     * It takes two integers, x and y, and returns a Pixel object
     *
     * @param x The x coordinate of the pixel you want to get.
     * @param y The y coordinate of the pixel you want to get.
     * @return A Pixel object
     */
    fun getPixel(x: Int, y: Int): PixelManager {
        return PixelManager(x, y, worldManager)
    }

    /**
     * Return true if the pixel at (x, y) is dirt.
     *
     * @param x The x coordinate of the pixel you want to check.
     * @param y The y coordinate of the pixel you want to check.
     * @return A boolean value
     */
    fun isDirt(x: Int, y: Int): Boolean {
        return getPixel(x, y).isDirt
    }

    /**
     * Zoom on the map
     *
     * @param xArg The x coordinate of the top left corner of the rectangle to be zoomed in on.
     * @param yArg The y coordinate of the top left corner of the rectangle to be zoomed in on.
     * @param widthArg The width of the area you want to zoom in on.
     * @param heightArg The height of the area to be zoomed in on.
     * @return A subimage of the mapFile.
     */
    private fun zoom(xArg: Int, yArg: Int, widthArg: Int, heightArg: Int): BufferedImage {
        return zoom(ZoneToAdapt(xArg, yArg, widthArg, heightArg, mapWidth, mapHeight), mapFile!!)
    }

    /**
     * "Zoom in on the map at the given coordinates by the given amount."
     *
     * @param x The x coordinate of the top left corner of the image.
     * @param y The y coordinate of the center of the image.
     * @param zoom The zoom level.
     * @return A BufferedImage
     */
    fun zoom(x: Int, y: Int, zoom: Int): BufferedImage {
        return zoom(x - zoom * 2, y - zoom, zoom * 4, zoom * 2)
    }

    /**
     * "Given a point and a zoom level, return a BufferedImage of the map centered on that point at that zoom level."
     *
     * @param x The x coordinate of the center of the zoomed area
     * @param y The y coordinate of the center of the zoomed area
     * @param zoom the zoom level, 1 is the default, 2 is twice as big, etc.
     * @param bi The image to zoom
     * @return A BufferedImage
     */
    fun zoom(x: Int, y: Int, zoom: Int, bi: BufferedImage): BufferedImage {
        return zoom(ZoneToAdapt(x - zoom * 2, y - zoom, zoom * 4, zoom * 2, mapWidth, mapHeight), bi)
    }

    /**
     * It takes a zone and a buffered image, and returns a buffered image that is a zoomed in version of the original
     * buffered image, with the zoomed in area being the zone
     *
     * @param zone ZoneToAdapt
     * @param bi the image to be zoomed
     * @return A BufferedImage
     */
    private fun zoom(zone: ZoneToAdapt, bi: BufferedImage): BufferedImage {
        val x = zone.x
        val y = zone.y
        val width = zone.width
        val height = zone.height
        return bi.getSubimage(
            x * bi.getWidth(null) / mapWidth,
            y * bi.getHeight(null) / mapHeight,
            width * bi.getWidth(null) / mapWidth,
            height * bi.getHeight(null) / mapHeight
        )
    }

    /**
     * It takes in a bunch of parameters, and returns a BufferedImage
     *
     * @param x The x coordinate of the center of the map
     * @param y The y coordinate of the center of the map
     * @param zoom The zoom level of the map.
     * @param player The player that is viewing the map.
     * @return A BufferedImage
     */
    fun zoomWithCity(x: Int, y: Int, zoom: Int, player: Player? = null): BufferedImage {
        var image = cloneBufferedImage(mapFile!!)
        if (player != null) {
            image.setRGB(player["place_${progName}_x"].toInt(), player["place_${progName}_y"].toInt(), Color.RED.rgb)
        }

        image = zoom(x, y, zoom, image)
        image = bigger(image, 10)

        val places = Place.getPlacesWithWorld(progName)
        println(places.size)

        places.removeIf { place: Place ->
            !place.getX().isPresent || !place.getY().isPresent
                    || place.getX().get() < x - zoom * 2
                    || place.getX().get() > x + zoom * 2
                    || place.getY().get() < y - zoom
                    || place.getY().get() > y + zoom
        }

        println(places.size)

        getMapWithNames(
            places,
            ZoneToAdapt(
                x - zoom * 2,
                y - zoom,
                zoom * 4,
                zoom * 2,
                mapWidth,
                mapHeight
            ),
            image
        )

        return image
    }

    // return an image with the places' names on it
    private fun getMapWithNames(
        places: ArrayList<Place>, zone: ZoneToAdapt, image: BufferedImage
    ) {
        val g = image.createGraphics()
        g.color = Color(0, 255, 0)
        // set the text size
        g.font = Font("Arial", Font.BOLD, image.width / 80)
        val size = image.width / 80
        for (place in places) {
            if (place.getX().isPresent && place.getY().isPresent) {
                // coos on image (x, y) after resizing (x and y are not the same as the image's coos)
                val x = (place.getX().get() - zone.x) * image.width / zone.width
                val y = (place.getY().get() - zone.y) * image.height / zone.height
                // draw a point
                g.fillOval(x, y, (size * 0.7).toInt(), (size * 0.7).toInt())
                // draw the name
                try {
                    g.drawString(place.getString("nameRP"), (x + 1.1 * size).toInt(), y)
                } catch (ignored: Exception) {
                }
                // draw the x and y coordinates
                try {
                    g.drawString(
                        "(${place.getX().get()}, ${place.getY().get()})",
                        (x + 1.1 * size).toInt(),
                        (y + 1.1 * size).toInt()
                    )
                } catch (ignored: Exception) {
                }
            }
        }
        g.dispose()
    }

    // --------------------
    // PATH FINDING with A*
    // --------------------

    // return the path from one pixel to another
    fun findPath(start: Node, end: Node): ArrayList<PixelManager> {

        require(start.isLanded == end.isLanded) { "start and end must be on the same type of tile" }

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
                val path = ArrayList<PixelManager>()
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
                if (!(openList.contains(n) && n.heuristic < current.heuristic || closedList.contains(n))) {
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

    /**
     * It returns a list of all the nodes that are connected to the given node
     *
     * @param pixel the current pixel we're looking at
     * @param nodes the list of nodes that we will be working with
     * @return A list of nodes that are connected to the given node.
     */
    private fun getConnectedNodes(pixel: Node, nodes: ArrayList<Node>): ArrayList<Node> {
        val nodes2 = ArrayList<Node>()
        // ajout des pixels voisins un par un
        if (isInMap(pixel.x - 1, pixel.y - 1)) nodes2.add(getNode(pixel.x - 1, pixel.y - 1, nodes))
        if (isInMap(pixel.x, pixel.y - 1)) nodes2.add(getNode(pixel.x, pixel.y - 1, nodes))
        if (isInMap(pixel.x + 1, pixel.y - 1)) nodes2.add(getNode(pixel.x + 1, pixel.y - 1, nodes))
        if (isInMap(pixel.x - 1, pixel.y)) nodes2.add(getNode(pixel.x - 1, pixel.y, nodes))
        if (isInMap(pixel.x + 1, pixel.y)) nodes2.add(getNode(pixel.x + 1, pixel.y, nodes))
        if (isInMap(pixel.x - 1, pixel.y + 1)) nodes2.add(getNode(pixel.x - 1, pixel.y + 1, nodes))
        if (isInMap(pixel.x, pixel.y + 1)) nodes2.add(getNode(pixel.x, pixel.y + 1, nodes))
        if (isInMap(pixel.x + 1, pixel.y + 1)) nodes2.add(getNode(pixel.x + 1, pixel.y + 1, nodes))
        // pour chaque pixel on l'enlève si ce n'est pas du même type que le pixel courant
        nodes2.removeIf { n: Node? -> n!!.isLanded != pixel.isLanded }
        return nodes2
    }

    /**
     * "If x is in the range 0 to mapWidth and y is in the range 0 to mapHeight, then return true, otherwise return false."
     *
     * The function isInMap() is used to check if a given coordinate is within the bounds of the map
     *
     * @param x The x coordinate of the tile to check.
     * @param y The y coordinate of the tile to check.
     * @return A boolean value.
     */
    private fun isInMap(x: Int, y: Int): Boolean {
        return x in 0 until mapWidth && y in 0 until mapHeight
    }

    /**
     * It returns a node from the list of nodes.
     *
     * @param x The x coordinate of the node
     * @param y The y coordinate of the node
     * @param nodes ArrayList<Node> - This is the list of nodes that have been created.
     * @return The node that is being returned is the node that is being searched for.
     */
    fun getNode(x: Int, y: Int, nodes: ArrayList<Node>): Node {
        val n = Node(x, y, worldManager, 0.0, 0.0)
        return if (nodes.contains(n)) {
            nodes[nodes.indexOf(n)]
        } else n
    }

    // distance between two pixels
    private fun distance(a: PixelManager, b: PixelManager): Double {
        return sqrt((a.x - b.x).toDouble().pow(2.0) + (a.y - b.y).toDouble().pow(2.0))
    }

    fun drawPath(path: ArrayList<PixelManager>): BufferedImage {
        val img = cloneBufferedImage(mapFile!!)
        for (p in path) {
            img.setRGB(p.xImage, p.yImage, Color.RED.rgb)
        }
        return img
    }
}