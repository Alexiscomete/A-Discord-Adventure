package io.github.alexiscomete.lapinousecond.worlds

import io.github.alexiscomete.lapinousecond.Beurk
import io.github.alexiscomete.lapinousecond.entity.entities.Player
import io.github.alexiscomete.lapinousecond.worlds.map.Node
import io.github.alexiscomete.lapinousecond.worlds.map.PixelManager
import io.github.alexiscomete.lapinousecond.worlds.map.tiles.BaseTileGenerator
import io.github.alexiscomete.lapinousecond.worlds.map.tiles.WorldRenderScene
import io.github.alexiscomete.lapinousecond.worlds.map.tiles.render.SquareWorldRenderer
import io.github.alexiscomete.lapinousecond.worlds.map.tiles.render.canvas.ImageWorldCanvas
import procedural_generation.noise.ComplexNoiseBuilder
import procedural_generation.noise.NoiseMapBuilder
import procedural_generation.noise.nodes.*
import java.awt.Color
import java.awt.Font
import java.awt.Image
import java.awt.image.BufferedImage
import java.io.IOException
import javax.imageio.ImageIO
import kotlin.math.pow
import kotlin.math.sqrt

const val MAX_PATH_CACHE_SIZE = 4000

var complexNoiseBuilderForCaves = ComplexNoiseBuilder(
    ValueOperationNodeBuilder(
        ValueOperationNodeBuilder(
            AddNodeBuilder(
                NoiseMapBuilder(2.0),
                ChangeSeedNodeBuilder(
                    Operation.ADD,
                    1,
                    NoiseMapBuilder(2.0)
                ),
                ChangeLocationNodeBuilder(
                    ChangeSeedNodeBuilder(
                        Operation.ADD,
                        5,
                        NoiseMapBuilder(1.0)
                    ),
                    Operation.MULTIPLY,
                    Operation.MULTIPLY,
                    10.0,
                    10.0
                ),
                ChangeLocationNodeBuilder(
                    ChangeSeedNodeBuilder(
                        Operation.ADD,
                        6,
                        NoiseMapBuilder(0.5)
                    ),
                    Operation.MULTIPLY,
                    Operation.MULTIPLY,
                    100.0,
                    100.0
                )
            ),
            ValueOperation.ABS,
            0.5
        ),
        ValueOperation.REMOVE_POURCENT,
        0.4
    )
)

var complexNoiseBuilderForRivers = ComplexNoiseBuilder(
    ValueOperationNodeBuilder(
        ValueOperationNodeBuilder(
            AddNodeBuilder(
                NoiseMapBuilder(2.0),
                ChangeSeedNodeBuilder(
                    Operation.ADD,
                    1,
                    NoiseMapBuilder(2.0)
                ),
                ChangeLocationNodeBuilder(
                    ChangeSeedNodeBuilder(
                        Operation.ADD,
                        5,
                        NoiseMapBuilder(1.0)
                    ),
                    Operation.MULTIPLY,
                    Operation.MULTIPLY,
                    4.0,
                    4.0
                )
            ),
            ValueOperation.ABS,
            0.5
        ),
        ValueOperation.REMOVE_POURCENT,
        0.4
    )
)

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

fun getMapWithNames(
    places: ArrayList<Place>, zone: ZoneToAdapt, image: BufferedImage
) {
    val g = image.createGraphics()
    g.color = Color(0, 255, 0)
    // set the text size
    val size = image.width / 80
    g.font = Font("Arial", Font.BOLD, size)
    for (place in places) {
        if (place.getX().isPresent && place.getY().isPresent) {
            // coos on image (x, y) after resizing (x and y are not the same as the image's coos)
            val (placeX, placeY) = Zooms.ZOOM_OUT.zoomInTo(
                zone.zoom, place.getX().get(), place.getY().get()
            )
            val x = (placeX - zone.x) * image.width / zone.width
            val y = (placeY - zone.y) * image.height / zone.height
            // draw a point
            g.fillOval(x, y, (size * 0.7).toInt(), (size * 0.7).toInt())
            // draw the name
            try {
                g.drawString(place["nameRP"], (x + 1.1 * size).toInt(), y)
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

data class ZoneToAdapt(
    var x: Int,
    var y: Int,
    var width: Int,
    var height: Int,
    private var maxX: Int,
    private var maxY: Int,
    val zoom: Zooms
) {
    init {
        val (maxX, maxY) = Zooms.ZOOM_OUT.zoomInTo(zoom, maxX, maxY)
        this.maxX = maxX
        this.maxY = maxY
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

fun getImage(path: String): BufferedImage {
    return try {
        val image = ImageIO.read(WorldEnum::class.java.classLoader.getResourceAsStream("$path.png"))
            ?: throw IOException("Image not found")
        image
    } catch (e: IOException) {
        throw RuntimeException(e)
    }
}

val builder = ValueOperationNodeBuilder(
    ValueOperationNodeBuilder(
        AddNodeBuilder(
            ChangeLocationNodeBuilder(
                ChangeSeedNodeBuilder(
                    Operation.ADD,
                    4,
                    NoiseMapBuilder(5.0)
                ),
                Operation.DIVIDE,
                Operation.DIVIDE,
                2.5,
                2.5
            ),
            ChangeLocationNodeBuilder(
                ChangeSeedNodeBuilder(
                    Operation.ADD,
                    3,
                    NoiseMapBuilder(4.0)
                ),
                Operation.DIVIDE,
                Operation.DIVIDE,
                2.0,
                2.0
            ),
            ChangeLocationNodeBuilder(
                ChangeSeedNodeBuilder(
                    Operation.ADD,
                    2,
                    NoiseMapBuilder(1.0)
                ),
                Operation.DIVIDE,
                Operation.DIVIDE,
                2.0,
                2.0
            ),
            NoiseMapBuilder(0.5),
            ChangeSeedNodeBuilder(
                Operation.ADD,
                1,
                NoiseMapBuilder(0.5)
            ),
            ChangeLocationNodeBuilder(
                ChangeSeedNodeBuilder(
                    Operation.ADD,
                    5,
                    NoiseMapBuilder(0.3)
                ),
                Operation.MULTIPLY,
                Operation.MULTIPLY,
                10.0,
                10.0
            ),
            ChangeLocationNodeBuilder(
                ChangeSeedNodeBuilder(
                    Operation.ADD,
                    6,
                    NoiseMapBuilder(0.3)
                ),
                Operation.MULTIPLY,
                Operation.MULTIPLY,
                100.0,
                100.0
            ),
            ChangeLocationNodeBuilder(
                ChangeSeedNodeBuilder(
                    Operation.ADD,
                    7,
                    NoiseMapBuilder(0.2)
                ),
                Operation.MULTIPLY,
                Operation.MULTIPLY,
                1000.0,
                1000.0
            )
        ),
        ValueOperation.POWER_SYMMETRICAL,
        2.0
    ),
    ValueOperation.REMOVE_POURCENT,
    0.4
)

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
        150,
        150,
        300,
        300,
        WorldProcedural(
            ComplexNoiseBuilder(
                builder
            ).build(60),
            "NORMAL"
        )
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
        WorldProcedural(
            ComplexNoiseBuilder(
                AddNodeBuilder(
                    builder,
                    ImageMaskNodeBuilder(
                        getImage("DIBIMAP"),
                        5.0,
                        Color(0x704A40),
                        Color(0x4D759D),
                        6
                    )
                )
            ).build(80),
            "DIBIMAP"
        )
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
        WorldProcedural(
            ComplexNoiseBuilder(
                AddNodeBuilder(
                    builder,
                    ImageMaskNodeBuilder(
                        getImage("TUTO"),
                        5.0,
                        Color(0x704A40),
                        Color(0x4D759D),
                        6
                    )
                )
            ).build(50),
            "TUTO"
        )
    );

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
        return getPixel(x, y).isLanded
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
    @Beurk
    fun zoomWithDecorElements(
        x: Int,
        y: Int,
        zoom: Int,
        zooms: Zooms,
        player: Player? = null
    ): BufferedImage {
        return worldManager.zoomWithDecorElements(
            ZoneToAdapt(x - zoom * 2, y - zoom, zoom * 4, zoom * 2, mapWidth, mapHeight, zooms),
            imageFrom(x - zoom * 2, y - zoom, zoom * 4, zoom * 2, zooms),
            player,
            true
        )
    }

    /**
     * It takes in a bunch of parameters, and returns a BufferedImage
     *
     * @param x The x coordinate of the center of the map
     * @param y The y coordinate of the center of the map
     * @param size The zoom level of the map.
     * @return A BufferedImage
     */
    @Beurk
    fun zoomWithDecorElementsSquare(
        x: Int,
        y: Int,
        size: Int,
        zooms: Zooms,
        image: BufferedImage,
    ): BufferedImage {
        return worldManager.zoomWithDecorElements(
            ZoneToAdapt(x - size, y - size, size * 2 + 1, size * 2 + 1, mapWidth, mapHeight, zooms),
            image,
            big = false
        )
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
            require(openList.size <= MAX_PATH_CACHE_SIZE) { "Le chemin met trop de puissance à calculer, essayez en plusieurs fois" }
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
        // pour chaque pixel, on l'enlève si ce n'est pas du même type que le pixel courant
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
        val maxX = path.maxOf { it.x }
        val maxY = path.maxOf { it.y }
        val minX = path.minOf { it.x }
        val minY = path.minOf { it.y }
        val img = imageFrom(minX-1, minY-1, maxX+1, maxY+1)
        for (p in path) {
            img.setRGB(p.x - minX + 1, p.y - minY + 1, Color.RED.rgb)
        }
        return img
    }

    private fun imageFrom(minX: Int, minY: Int, maxX: Int, maxY: Int, zooms: Zooms = Zooms.ZOOM_OUT): BufferedImage {
        val canvas = ImageWorldCanvas()
        val tileGenerator = BaseTileGenerator(zooms, worldManager)
        val renderScene = WorldRenderScene(
            canvas,
            minX,
            minY,
            tileGenerator = tileGenerator,
            worldRenderer = SquareWorldRenderer(maxY - minY, maxX - minX, canvas, tileGenerator)
        )
        renderScene.renderAll()
        return canvas.bufferedImage
    }
}