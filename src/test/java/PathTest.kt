import io.github.alexiscomete.lapinousecond.worlds.WorldEnum
import io.github.alexiscomete.lapinousecond.worlds.WorldProcedural
import io.github.alexiscomete.lapinousecond.worlds.Zooms
import org.junit.jupiter.api.Test
import java.awt.Color
import java.awt.image.BufferedImage
import java.io.File
import java.io.IOException
import javax.imageio.ImageIO

class PathTest {
    @Test
    fun testPath() {
        // generate an image of the noise (gray scale)
        val noiseImage = BufferedImage(3000, 3000, BufferedImage.TYPE_INT_RGB)
        for (x in 0 until 3000) {
            for (y in 0 until 3000) {
                val color = ((WorldEnum.TUTO.worldManager as WorldProcedural).isPath(300000 + x, 300000 + y, Zooms.ZOOM_IN))
                var blue = 255 - 100
                var green = 255 - 100
                var red = 255 - 100
                if (color) {
                    green = 0
                    red = 0
                    blue = 0
                }
                try {
                    noiseImage.setRGB(x, y, Color(red, green, blue).rgb)
                } catch (e: Exception) {
                    println("x: $x y: $y color: $color")
                }
            }
        }

        // save the image

        // save the image
        try {
            ImageIO.write(noiseImage, "png", File("paths.png"))
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}