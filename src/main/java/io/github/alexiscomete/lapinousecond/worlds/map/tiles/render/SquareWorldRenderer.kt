package io.github.alexiscomete.lapinousecond.worlds.map.tiles.render

import io.github.alexiscomete.lapinousecond.worlds.map.tiles.TileGenerator
import io.github.alexiscomete.lapinousecond.worlds.map.tiles.render.canvas.WorldCanvas
import io.github.alexiscomete.lapinousecond.worlds.map.tiles.types.Tile
import java.util.*

class SquareWorldRenderer(
    val height: Int,
    private val width: Int,
    val canvas: WorldCanvas,
    private val tileGenerator: TileGenerator
) : WorldRenderer {
    private var renderQueue: Queue<RenderInfos> = LinkedList()

    override fun renderAll(from: Tile) {
        canvas.resetCanvas(width, height)
        renderQueue.add(RenderInfos(from, xSource, ySource, 0))
        while (!renderQueue.isEmpty()) {
            val renderInfos = renderQueue.poll()
            with(renderInfos) {
                renderingType(canvas)

                // UP - not here

                // DOWN

                if (yToUse < height + 1) {
                    renderQueue.add(
                        RenderInfos(
                            tile.down ?: run {
                                tileGenerator.getOrGenerateTileAt(tile.x, tile.y + 1).also {
                                    tile.down = it
                                }
                            },
                            xToUse,
                            yToUse + 1,
                            distance + 1
                        )
                    )
                }

                // LEFT - not here

                // RIGHT
                if (xToUse < width + 1) {
                    renderQueue.add(
                        RenderInfos(
                            tile.right ?: run {
                                tileGenerator.getOrGenerateTileAt(tile.x + 1, tile.y).also {
                                    tile.right = it
                                }
                            },
                            xToUse + 1,
                            yToUse,
                            distance + 1
                        )
                    )
                }

                render(canvas)
            }
        }
    }

    override val xSource: Int = 0
    override val ySource: Int = 0
}