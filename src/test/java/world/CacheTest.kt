package world

import io.github.alexiscomete.lapinousecond.worlds.WorldEnum
import io.github.alexiscomete.lapinousecond.worlds.Zooms
import io.github.alexiscomete.lapinousecond.worlds.map.tiles.WorldRenderScene
import io.github.alexiscomete.lapinousecond.worlds.map.tiles.render.JustDrawIt
import io.github.alexiscomete.lapinousecond.worlds.map.tiles.render.TerminalWorldCanvas
import org.junit.jupiter.api.Test
import java.awt.Color

@Deprecated("This test is deprecated.")
class CacheTest {

    @Test
    fun testCacheMove() {
        val canvas = TerminalWorldCanvas()
        var time = System.currentTimeMillis()
        val view = WorldRenderScene(canvas, 10, 10, Zooms.ZOOM_IN, WorldEnum.DIBIMAP.worldManager)
        println("Time to create: ${System.currentTimeMillis() - time}")
        time = System.currentTimeMillis()
        view.renderAll()
        println("Time to render: ${System.currentTimeMillis() - time}")
        canvas.printlnCanvas()
        view.moveUp()
        time = System.currentTimeMillis()
        view.renderAll()
        println("Time to render: ${System.currentTimeMillis() - time}")
        canvas.printlnCanvas()
        view.moveUp()
        view.renderAll()
        canvas.printlnCanvas()
        view.moveUp()
        view.renderAll()
        canvas.printlnCanvas()
        view.moveUp()
        view.renderAll()
        canvas.printlnCanvas()
        view.moveDown()
        view.renderAll()
        canvas.printlnCanvas()
        view.moveDown()
        view.renderAll()
        canvas.printlnCanvas()
        view.moveDown()
        view.renderAll()
        canvas.printlnCanvas()
    }
}

fun main() {
    val canvas = TerminalWorldCanvas()
    val view = WorldRenderScene(canvas, 0, 600, Zooms.ZOOM_IN, WorldEnum.NORMAL.worldManager)
    var input = ""
    while (input != "quit") {
        view.renderAll()
        canvas.justDrawThisOver(
            JustDrawIt('X', Color(255, 0, 0), arrayOf(arrayOf(Color(255, 0, 0)))),
            view.worldRenderer.xReset,
            view.worldRenderer.yReset
        )
        canvas.printlnCanvas()
        input = readlnOrNull() ?: ""
        when (input) {
            "z" -> view.moveUp()
            "s" -> view.moveDown()
            "q" -> view.moveLeft()
            "d" -> view.moveRight()
            "quit" -> println("Bye")
            else -> println("Unknown command")
        }
    }
}