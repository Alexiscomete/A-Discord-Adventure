import io.github.alexiscomete.lapinousecond.worlds.Zooms
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class FirstTest {

    @Deprecated("This test is deprecated.", ReplaceWith("println(\"Hello, world!\")"))
    @Test
    fun test() {
        println("Hello, world!")
    }

    @Test
    fun test2() {
        val (x, y) = (1 to 25)
        Assertions.assertEquals(
            Zooms.ZOOM_OUT.zoomInTo(Zooms.ZOOM_IN, x, y),
            (7000 to 175000)
        )
    }
}