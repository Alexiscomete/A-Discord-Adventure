package io.github.alexiscomete.lapinousecond.worlds.map.tiles.render

import io.github.alexiscomete.lapinousecond.worlds.map.tiles.RenderingType
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
            when (renderInfos.renderingType(canvas)) {
                RenderingType.NO_RENDER_REC -> {
                    renderInfos.render(canvas)
                }
                RenderingType.ONLY_IF_EXIST -> {
                    with(renderInfos) {
                        // UP
                        tile.up?.also {
                            renderQueue.add(
                                RenderInfos(
                                    it,
                                    xToUse,
                                    yToUse - 1,
                                    distance + 1
                                )
                            )
                        }
                        // DOWN
                        tile.down?.also {
                            renderQueue.add(
                                RenderInfos(
                                    it,
                                    xToUse,
                                    yToUse + 1,
                                    distance + 1
                                )
                            )
                        }
                        // LEFT
                        tile.left?.also {
                            renderQueue.add(
                                RenderInfos(
                                    it,
                                    xToUse - 1,
                                    yToUse,
                                    distance + 1
                                )
                            )
                        }
                        // RIGHT
                        tile.right?.also {
                            renderQueue.add(
                                RenderInfos(
                                    it,
                                    xToUse + 1,
                                    yToUse,
                                    distance + 1
                                )
                            )
                        }
                    }
                    renderInfos.render(canvas)
                }
                RenderingType.ALWAYS_RENDER -> {
                    with(renderInfos) {
                        // UP

                        renderQueue.add(
                            RenderInfos(
                                tile.up ?: run {
                                    tileGenerator.getOrGenerateTileAt(tile.x, tile.y - 1).also {
                                        tile.up = it
                                    }
                                },
                                xToUse,
                                yToUse - 1,
                                distance + 1
                            )
                        )

                        // DOWN

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

                        // LEFT

                        renderQueue.add(
                            RenderInfos(
                                tile.left ?: run {
                                    tileGenerator.getOrGenerateTileAt(tile.x - 1, tile.y).also {
                                        tile.left = it
                                    }
                                },
                                xToUse - 1,
                                yToUse,
                                distance + 1
                            )
                        )

                        // RIGHT

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
                    renderInfos.render(canvas)
                }

                RenderingType.NOTHING -> {}
            }
        }
    }

    override val xSource: Int = 0
    override val ySource: Int = 0
}