package worlds

import kotlin.math.abs
import kotlin.math.sqrt

class WorldAbstract(travelPrice: Int, name: String?, nameRP: String?, progName: String?, desc: String?) :
    World(travelPrice, name!!, nameRP!!, progName!!, desc!!) {
    override fun getPriceForDistance(distance: Double, place: Boolean): Double {
        return distance * travelPrice / 100
    }

    override fun getDistance(place1: Place?, place2: Place?): Double {
        val idPl1 = place1!!.getServerID()
        val idPl2 = place2!!.getServerID()
        return if (idPl1.isPresent && idPl2.isPresent) {
            sqrt(abs(idPl1.get() - idPl2.get()).toDouble()) / 100000
        } else {
            500.0
        }
    }
}