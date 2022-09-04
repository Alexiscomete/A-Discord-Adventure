package io.github.alexiscomete.lapinousecond.worlds

import java.util.*

class PlaceForTest(val name: String, override val id: Long, var x: Int, var y: Int) : Place() {
    override fun getString(row: String): String {
        return when (row) {
            "name" -> name
            "x" -> x.toString()
            "y" -> y.toString()
            else -> ""
        }
    }

    override fun getX(): Optional<Int> {
        return Optional.of(x)
    }

    override fun getY(): Optional<Int> {
        return Optional.of(y)
    }

    companion object {
        // génère aléatoirement une place pour les tests, minx = 0, maxx = voir dans Map.java, miny = 0, maxy = voir dans Map.java
        private fun generateRandomPlace(world: WorldEnum): PlaceForTest {

            // génération des coordonnées aléatoires
            val x = (Math.random() * world.mapWidth).toInt()
            val y = (Math.random() * world.mapHeight).toInt()

            // génération du nom aléatoire
            val name = StringBuilder()
            val nb = (Math.random() * 10).toInt().coerceAtLeast(3)
            for (i in 0 until nb) {
                name.append((Math.random() * 26 + 'a'.code.toDouble()).toInt().toChar())
            }

            // génération de l'id aléatoire
            val id = (Math.random() * Long.MAX_VALUE).toLong()
            return PlaceForTest(name.toString(), id, x, y)
        }

        // génération de n places aléatoires
        fun generateRandomPlaces(n: Int, world: WorldEnum): ArrayList<Place> {
            val places = ArrayList<Place>()
            for (i in 0 until n) {
                places.add(generateRandomPlace(world))
            }
            return places
        }
    }
}