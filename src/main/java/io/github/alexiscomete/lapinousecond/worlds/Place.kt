package io.github.alexiscomete.lapinousecond.worlds

import io.github.alexiscomete.lapinousecond.entity.concrete.resources.Resource
import io.github.alexiscomete.lapinousecond.entity.entities.Owner
import io.github.alexiscomete.lapinousecond.useful.managesave.*
import java.sql.SQLException
import java.util.*

val PLACES = Table("places")
val places = CacheCustom(PLACES) { id: Long -> Place(id) }

open class Place(
    id: Long = run {
        val tempID = generateUniqueID()
        val h = HashMap<String, String>()
        h["id"] = tempID.toString()
        saveManager.insert("places", h)
        tempID
    }
) : CacheGetSet(id, PLACES), Owner {

    open fun getX(): Optional<Int> {
        return Optional.ofNullable(this["x"].toInt())
    }

    open fun getY(): Optional<Int> {
        return Optional.ofNullable(this["y"].toInt())
    }

    override val ownerType: String = ""
    override val ownerString: String = ""
    override fun addMoney(amount: Double) {
        TODO("Not yet implemented")
    }

    override fun getMoney(): Double {
        TODO("Not yet implemented")
    }

    override fun removeMoney(amount: Double) {
        TODO("Not yet implemented")
    }

    override fun addResource(resource: Resource, amount: Double) {
        TODO("Not yet implemented")
    }

    override fun getResource(resource: Resource): Double {
        TODO("Not yet implemented")
    }

    override fun removeResource(resource: Resource, amount: Double) {
        TODO("Not yet implemented")
    }

    override fun hasResource(resource: Resource, amount: Double): Boolean {
        TODO("Not yet implemented")
    }

    override fun hasMoney(amount: Double): Boolean {
        TODO("Not yet implemented")
    }

}

fun toPlaces(places: String): ArrayList<Place> {
    val str = places.split(";".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
    val places1 = ArrayList<Place>()
    for (s in str) {
        try {
            places1.add(Place(s.toLong()))
        } catch (ignore: NumberFormatException) {
        }
    }
    return places1
}

fun getPlacesWithWorld(world: String): ArrayList<Place> {
    val resultSet = saveManager.executeQuery("SELECT * FROM places WHERE world = '$world'", true)
    val places = ArrayList<Place>()
    try {

        //long id = resultSet.getLong("id"); places.add(new Place(id));
        while (resultSet!!.next()) {
            places.add(Place(resultSet.getLong("id")))
        }
    } catch (e: SQLException) {
        e.printStackTrace()
    }
    return places
}