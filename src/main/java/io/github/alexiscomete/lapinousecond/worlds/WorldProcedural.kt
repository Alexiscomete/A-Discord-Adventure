package io.github.alexiscomete.lapinousecond.worlds

import io.github.alexiscomete.lapinousecond.entity.entities.Player
import procedural_generation.noise.ComplexNoise
import java.awt.Color
import java.awt.image.BufferedImage

class WorldProcedural(
    private val complexNoise: ComplexNoise,
    private val maxX: Int,
    private val maxY: Int,
    private val worldNameInDatabase: String
) : WorldManager {

    private val path: ComplexNoise = complexNoiseBuilderForCaves.build(80)
    private val river: ComplexNoise = complexNoiseBuilderForRivers.build(81)

    override fun isLand(x: Double, y: Double): Boolean {
        return complexNoise.getValue(x, y) > 0.5
    }

    override fun isLand(x: Int, y: Int, zoom: Zooms): Boolean {
        val (x1, y1) = zoom.zoomOutTo(Zooms.ZOOM_OUT, x.toDouble(), y.toDouble())
        return isLand(x1, y1)
    }

    override fun zoom(zoneToAdapt: ZoneToAdapt): BufferedImage {
        // generate the image
        val image = BufferedImage(zoneToAdapt.width, zoneToAdapt.height, BufferedImage.TYPE_INT_RGB)
        // fill the image
        for (x in zoneToAdapt.x until zoneToAdapt.x + zoneToAdapt.width) {
            for (y in zoneToAdapt.y until zoneToAdapt.y + zoneToAdapt.height) {
                image.setRGB(x - zoneToAdapt.x, y - zoneToAdapt.y, findColor(x, y, zoneToAdapt.zoom))
            }
        }
        return image
    }

    override fun zoomWithDecorElements(
        zoneToAdapt: ZoneToAdapt,
        image: BufferedImage?,
        player: Player?,
        big: Boolean
    ): BufferedImage {
        // generate the image
        var image0 = image ?: zoom(zoneToAdapt)
        // add the player
        if (player != null) {
            addPlayerInImage(player, zoneToAdapt, image0)
        }

        if (big) {
            image0 = bigger(image0, 10)
        }

        addCitiesNamesInImage(zoneToAdapt, image0)

        return image0
    }

    private fun addCitiesNamesInImage(
        zoneToAdapt: ZoneToAdapt,
        image: BufferedImage
    ) {
        val places = getPlacesWithWorld(worldNameInDatabase)

        places.removeIf { place: Place ->
            if (!place.getX().isPresent || !place.getY().isPresent) {
                return@removeIf true
            }
            val (x, y) = Zooms.ZOOM_OUT.zoomInTo(zoneToAdapt.zoom, place.getX().get(), place.getY().get())
            (x < zoneToAdapt.x || x > zoneToAdapt.x + zoneToAdapt.width || y < zoneToAdapt.y || y > zoneToAdapt.y + zoneToAdapt.height)
        }

        getMapWithNames(
            places,
            zoneToAdapt,
            image
        )
    }

    private fun addPlayerInImage(
        player: Player,
        zoneToAdapt: ZoneToAdapt,
        image0: BufferedImage
    ) {
        if (player["place_${worldNameInDatabase}_zoom"] == "") {
            player["place_${worldNameInDatabase}_zoom"] = Zooms.ZOOM_OUT.name
        }
        if (player["place_${worldNameInDatabase}_zoom"] != zoneToAdapt.zoom.name) {
            val (x, y) = try {
                zoneToAdapt.zoom.zoomInTo(
                    Zooms.valueOf(player["place_${worldNameInDatabase}_zoom"]),
                    player["place_${worldNameInDatabase}_x"].toInt(),
                    player["place_${worldNameInDatabase}_y"].toInt()
                )
            } catch (e: Exception) {
                zoneToAdapt.zoom.zoomOutTo(
                    Zooms.valueOf(player["place_${worldNameInDatabase}_zoom"]),
                    player["place_${worldNameInDatabase}_x"].toInt(),
                    player["place_${worldNameInDatabase}_y"].toInt()
                )
            }
            image0.setRGB(x - zoneToAdapt.x, y - zoneToAdapt.y, Color.RED.rgb)
        } else {
            image0.setRGB(
                player["place_${worldNameInDatabase}_x"].toInt() - zoneToAdapt.x,
                player["place_${worldNameInDatabase}_y"].toInt() - zoneToAdapt.y,
                Color.RED.rgb
            )
        }
    }

    override fun uniqueTotalImage(): BufferedImage {
        // generate the image
        val image = BufferedImage(maxX, maxY, BufferedImage.TYPE_INT_RGB)
        // fill the image
        for (x in 0 until maxX) {
            for (y in 0 until maxY) {
                image.setRGB(x, y, findColor(x, y, Zooms.ZOOM_OUT))
            }
        }
        return image
    }

    override fun xImage(x: Int): Int {
        return x
    }

    override fun yImage(y: Int): Int {
        return y
    }

    override fun getHeight(x: Double, y: Double): Double {
        return complexNoise.getValue(x, y)
    }

    override fun getHeight(x: Int, y: Int, zoom: Zooms): Double {
        val (x1, y1) = zoom.zoomOutTo(Zooms.ZOOM_OUT, x.toDouble(), y.toDouble())
        return getHeight(x1, y1)
    }

    override fun isPath(x: Double, y: Double): Boolean {
        return path.getValue(x, y) < 0.55
    }

    override fun pathLevel(x: Double, y: Double): Double {
        return path.getValue(x, y)
    }

    override fun isRiver(x: Double, y: Double): Boolean {
        return river.getValue(x, y) < 0.56
    }

    override fun riverLevel(x: Double, y: Double): Double {
        return river.getValue(x, y)
    }

    @Deprecated("Use WorldRenderScene instead")
    private fun findColor(x: Int, y: Int, zooms: Zooms): Int {
        val color: Int = (getHeight(x, y, zooms) * 255).toInt()
        var blue = 0
        var green = 0
        var red = 0
        if (color > 127) {
            if (color > 128) {
                if (zooms == Zooms.ZOOM_IN && isPath(x.toDouble(), y.toDouble())) {
                    return Color(255, 178, 79).rgb
                }
                green = 255 - color
                if (color > 191) {
                    red = 255 - color
                    if (color > 224) {
                        blue = color
                        green = color
                    }
                }
            } else {
                return Color(245, 245, 66).rgb
            }
        } else {
            blue = color
        }
        return Color(red, green, blue).rgb
    }
}