package io.github.alexiscomete.lapinousecond.worlds

import alexiscomete.managesave.CacheCustom
import alexiscomete.managesave.Table
import alexiscomete.managesave.generateUniqueID
import io.github.alexiscomete.lapinousecond.entity.Owner
import alexiscomete.managesave.CacheGetSet
import alexiscomete.managesave.saveManager
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