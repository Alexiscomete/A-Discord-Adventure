package io.github.alexiscomete.lapinousecond.worlds

abstract class World(
    var travelPrice: Int,
    val name: String,
    val nameRP: String,
    val progName: String,
    val desc: String
) {

    abstract fun getPriceForDistance(distance: Double, place: Boolean): Double
    abstract fun getDistance(place1: Place?, place2: Place?): Double
}