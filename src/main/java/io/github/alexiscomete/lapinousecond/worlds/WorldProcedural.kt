package io.github.alexiscomete.lapinousecond.worlds

import io.github.alexiscomete.lapinousecond.Beurk
import io.github.alexiscomete.lapinousecond.entity.entities.PlayerData
import io.github.alexiscomete.lapinousecond.worlds.map.tiles.types.OCEAN_HEIGHT
import procedural_generation.noise.ComplexNoise
import java.awt.Color
import java.awt.image.BufferedImage

const val PATH_SEED = 80L
const val RIVER_SEED = PATH_SEED + 1L

const val THRESHOLD_PATH = 0.55
const val THRESHOLD_RIVER = 0.56

class WorldProcedural(
    private val complexNoise: ComplexNoise,
    private val worldNameInDatabase: String
) : WorldManager {

    private val path: ComplexNoise = complexNoiseBuilderForCaves.build(PATH_SEED)
    private val river: ComplexNoise = complexNoiseBuilderForRivers.build(RIVER_SEED)

    override fun isLand(x: Double, y: Double): Boolean {
        return complexNoise.getValue(x, y) > OCEAN_HEIGHT
    }

    override fun isLand(x: Int, y: Int, zoom: Zooms): Boolean {
        val (x1, y1) = zoom.zoomOutTo(Zooms.ZOOM_OUT, x.toDouble(), y.toDouble())
        return isLand(x1, y1)
    }

    @Beurk
    override fun zoomWithDecorElements(
        zoneToAdapt: ZoneToAdapt,
        image: BufferedImage,
        playerData: PlayerData?,
        big: Boolean
    ): BufferedImage {
        // generate the image
        var image0 = image
        // add the player
        if (playerData != null) {
            addPlayerInImage(playerData, zoneToAdapt, image0)
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
        playerData: PlayerData,
        zoneToAdapt: ZoneToAdapt,
        image0: BufferedImage
    ) {
        if (playerData["place_${worldNameInDatabase}_zoom"] == "") {
            playerData["place_${worldNameInDatabase}_zoom"] = Zooms.ZOOM_OUT.name
        }
        if (playerData["place_${worldNameInDatabase}_zoom"] == zoneToAdapt.zoom.name) {
            image0.setRGB(
                playerData["place_${worldNameInDatabase}_x"].toInt() - zoneToAdapt.x,
                playerData["place_${worldNameInDatabase}_y"].toInt() - zoneToAdapt.y,
                Color.RED.rgb
            )
        } else {
            val (x, y) = try {
                zoneToAdapt.zoom.zoomInTo(
                    Zooms.valueOf(playerData["place_${worldNameInDatabase}_zoom"]),
                    playerData["place_${worldNameInDatabase}_x"].toInt(),
                    playerData["place_${worldNameInDatabase}_y"].toInt()
                )
            } catch (e: Exception) {
                zoneToAdapt.zoom.zoomOutTo(
                    Zooms.valueOf(playerData["place_${worldNameInDatabase}_zoom"]),
                    playerData["place_${worldNameInDatabase}_x"].toInt(),
                    playerData["place_${worldNameInDatabase}_y"].toInt()
                )
            }
            image0.setRGB(x - zoneToAdapt.x, y - zoneToAdapt.y, Color.RED.rgb)
        }
    }

    override fun getHeight(x: Double, y: Double): Double {
        return complexNoise.getValue(x, y)
    }

    override fun getHeight(x: Int, y: Int, zoom: Zooms): Double {
        val (x1, y1) = zoom.zoomOutTo(Zooms.ZOOM_OUT, x.toDouble(), y.toDouble())
        return getHeight(x1, y1)
    }

    override fun isPath(x: Double, y: Double): Boolean {
        return path.getValue(x, y) < THRESHOLD_PATH
    }

    override fun pathLevel(x: Double, y: Double): Double {
        return path.getValue(x, y)
    }

    override fun isRiver(x: Double, y: Double): Boolean {
        return river.getValue(x, y) < THRESHOLD_RIVER
    }

    override fun riverLevel(x: Double, y: Double): Double {
        return river.getValue(x, y)
    }
}