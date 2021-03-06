package io.github.alexiscomete.lapinousecond.worlds

import io.github.alexiscomete.lapinousecond.useful.managesave.CacheCustom
import io.github.alexiscomete.lapinousecond.useful.managesave.Table
import io.github.alexiscomete.lapinousecond.useful.managesave.generateUniqueID
import io.github.alexiscomete.lapinousecond.entity.Owner
import io.github.alexiscomete.lapinousecond.useful.managesave.CacheGetSet
import io.github.alexiscomete.lapinousecond.useful.managesave.saveManager
import io.github.alexiscomete.lapinousecond.resources.Resource
import org.javacord.api.entity.message.embed.EmbedBuilder
import java.awt.Color
import java.sql.SQLException
import java.util.Optional
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

val PLACES = Table("places")
val places = CacheCustom(PLACES) { id: Long -> Place(id) }

open class Place : CacheGetSet, Owner {
    private var serverID: Long? = null
    private var serverBot: ServerBot? = null
    var world: World? = null
    private var x: Int? = null
    private var y: Int? = null
    lateinit var connections: LongArray

    constructor() : super(generateUniqueID(), PLACES) {
        val h = HashMap<String, String>()
        h["id"] = id.toString()
        saveManager.insert("places", h)
    }

    constructor(id: Long) : super(id, PLACES)

    fun getServerBot(): Optional<ServerBot> {
        return Optional.ofNullable(serverBot)
    }

    fun getServerID(): Optional<Long> {
        return Optional.ofNullable(serverID)
    }

    open fun getX(): Optional<Int> {
        return Optional.ofNullable(x)
    }

    open fun getY(): Optional<Int> {
        return Optional.ofNullable(y)
    }

    fun setAndGet(row: String?, value: String?): Place {
        super.set(row!!, value!!)
        return this
    }

    val placeEmbed: EmbedBuilder
        get() = EmbedBuilder()
            .setAuthor(id.toString())
            .setTitle(getString("name"))
            .setColor(Color.green)
            .setDescription(getString("descr"))
            .addField("World", getString("world"), true)
            .addField("Type", getString("type"), true)
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

    override fun askValidation(
        owner1: Owner,
        amount0: Double,
        ressource0: Resource,
        amount1: Double,
        ressource1: Resource,
        validation: (Boolean) -> Unit
    ) {
        TODO("Not yet implemented")
    }

    override fun askAmount(owner0: Owner, function: (Double) -> Unit) {
        TODO("Not yet implemented")
    }

    override fun askRessource(owner1: Owner, function: (Resource) -> Unit) {
        TODO("Not yet implemented")
    }

    companion object {
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
    }
}