package io.github.alexiscomete.lapinousecond.worlds

import io.github.alexiscomete.lapinousecond.useful.managesave.CacheCustom
import io.github.alexiscomete.lapinousecond.useful.managesave.CacheGetSet
import io.github.alexiscomete.lapinousecond.useful.managesave.Table

val SERVERS = Table("guilds")
val servers = CacheCustom(SERVERS) { id: Long -> ServerBot(id) }

/**
 * Représente un serveur discord dans la base de données
 * Pour créer un server utilisez le CacheCustom
 * @param id l'identifiant discord du serveur
 */
class ServerBot(id: Long) : CacheGetSet(id, SERVERS) {

    fun addPlace(id: Long) {
        val places = this["places"]
        if (places == "") {
            this["places"] = id.toString()
        } else {
            this["places"] += ",$id"
        }
    }

    fun removePlace(id: Long) : Boolean {
        val places = this["places"]
        return if (places == "") {
            false
        } else if (places.startsWith("$id,")) {
            this["places"] = places.replace("$id,", "")
            true
        } else if (places == id.toString()) {
            this["places"] = ""
            true
        } else {
            this["places"] = places.replace(",$id,", ",")
            true
        }
    }

    fun getPlaces() : List<Long> {
        val places = this["places"]
        return if (places == "") {
            listOf()
        } else {
            places.split(",").map { it.toLong() }
        }
    }
}