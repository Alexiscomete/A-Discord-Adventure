package io.github.alexiscomete.lapinousecond.worlds

import io.github.alexiscomete.lapinousecond.entity.Player
import java.util.*
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.sqrt

class TravelPrice(
    val price: Double,
    val totalPrice: Double,
    private val taxe: Int?,
    private val distanceCoos: Double?,
    private val distanceID: Long?
) {
    fun getTaxe(): Optional<Int> {
        return Optional.ofNullable(taxe)
    }

    fun getDistanceCoos(): Optional<Double> {
        return Optional.ofNullable(distanceCoos)
    }

    fun getDistanceID(): Optional<Long> {
        return Optional.ofNullable(distanceID)
    }

    companion object {
        fun getTravelPrice(serv1: ServerBot, serv2: ServerBot): TravelPrice {
            val distanceID = abs(serv1.id - serv2.id)
            val price = sqrt(distanceID.toDouble()) / 1000000
            return TravelPrice(price, price, null, null, distanceID)
        }

        fun getTravelPrice(serv1: ServerBot, serv2: ServerBot, world: World): TravelPrice {
            val distanceID = abs(serv1.id - serv2.id)
            val price = sqrt(distanceID.toDouble()) / 1000000
            val taxe = world.travelPrice
            return TravelPrice(price, price + taxe, taxe, null, distanceID)
        }

        fun getTravelPrice(p: Player?, place1: Place, place2: Place): TravelPrice {
            val price = 0.0
            var distanceCoos: Double? = null
            var taxe: Int? = null
            var distanceID: Long? = null
            if (place1.world === place2.world) {
                if (place1.world is WorldWithCoos) {
                    val x1 = if (place1.getX().isPresent) place1.getX().get() else 0
                    val x2 = if (place2.getX().isPresent) place2.getX().get() else 0
                    val y1 = if (place1.getY().isPresent) place1.getY().get() else 0
                    val y2 = if (place2.getY().isPresent) place2.getY().get() else 0
                    distanceCoos = sqrt((x1 - x2).toDouble().pow(2.0) + (y1 - y2).toDouble().pow(2.0))
                } else {
                    val id1 = if (place1.getServerID().isPresent) place1.getServerID().get() else 854288660147994634L
                    val id2 = if (place2.getServerID().isPresent) place2.getServerID().get() else 854288660147994634L
                    distanceID = abs(id1 - id2)
                }
            } else {
                taxe = place2.world!!.travelPrice
                //TODO : le joueur a des informations comme les différents lieux en fonction du monde ... le prix peut être différent, je pourrais surement enlever place1
            }
            return TravelPrice(price, price + (taxe ?: 0), taxe, distanceCoos, distanceID)
        }
    }
}