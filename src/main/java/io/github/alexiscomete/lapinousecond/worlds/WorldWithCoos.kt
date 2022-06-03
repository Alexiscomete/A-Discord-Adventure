package io.github.alexiscomete.lapinousecond.worlds

class WorldWithCoos(travelPrice: Int, name: String?, nameRP: String?, progName: String?, desc: String?) :
    World(travelPrice, name!!, nameRP!!, progName!!, desc!!) {
    override fun getPriceForDistance(distance: Double, place: Boolean): Double {
        return 0.0
    }

    override fun getDistance(place1: Place?, place2: Place?): Double {
        return 0.0
    }
}