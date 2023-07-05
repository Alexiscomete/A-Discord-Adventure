package io.github.alexiscomete.lapinousecond.worlds.map.tiles

import io.github.alexiscomete.lapinousecond.worlds.map.tiles.render.WorldCanvas
import java.util.*

class BaseWorldRenderer(
    val size: Int,
    val canvas: WorldCanvas,
    private val tileGenerator: TileGenerator
) : WorldRenderer {
    private var renderQueue: Queue<RenderInfos> = LinkedList()

    val xReset = (size / 2)
    val yReset = (size / 2)

    override fun renderAll(from: Tile) {
        canvas.resetCanvas(size, size)
        from.render(xReset, yReset, 0, canvas)
        while (!renderQueue.isEmpty()) {
            val renderInfos = renderQueue.poll()
            when (renderInfos.render(canvas)) {
                RenderingType.NO_RENDER -> {}
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
                }
            }
        }
    }
}