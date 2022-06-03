package io.github.alexiscomete.lapinousecond.worlds

import io.github.alexiscomete.lapinousecond.Main
import io.github.alexiscomete.lapinousecond.entity.Owner
import io.github.alexiscomete.lapinousecond.save.CacheGetSet
import io.github.alexiscomete.lapinousecond.save.SaveLocation.Companion.generateUniqueID
import io.github.alexiscomete.lapinousecond.save.Tables
import org.javacord.api.entity.message.embed.EmbedBuilder
import java.awt.Color
import java.sql.SQLException
import java.util.*

open class Place : CacheGetSet, Owner {
    private var serverID: Long? = null
    private var serverBot: ServerBot? = null
    var world: World? = null
    private var x: Int? = null
    private var y: Int? = null
    var connections: LongArray

    constructor() : super(generateUniqueID(), Tables.PLACES.table) {
        val h = HashMap<String?, String?>()
        h["id"] = id.toString()
        Main.saveManager.insert("places", h)
    }

    constructor(id: Long) : super(id, Tables.PLACES.table) {}

    fun getServerBot(): Optional<ServerBot> {
        return Optional.ofNullable(serverBot)
    }

    fun getServerID(): Optional<Long> {
        return Optional.ofNullable(serverID)
    }

    fun setServerBot(serverBot: ServerBot?) {
        this.serverBot = serverBot
    }

    fun setServerID(serverID: Long?) {
        this.serverID = serverID
    }

    open fun getX(): Optional<Int?>? {
        return Optional.ofNullable(x)
    }

    open fun setX(x: Int?) {
        this.x = x
    }

    open fun getY(): Optional<Int?>? {
        return Optional.ofNullable(y)
    }

    open fun setY(y: Int?) {
        this.y = y
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
    override val ownerType: String?
        get() = null
    override val ownerString: String?
        get() = null

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
            val resultSet = Main.saveManager.executeQuery("SELECT * FROM places WHERE world = '$world'", true)
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