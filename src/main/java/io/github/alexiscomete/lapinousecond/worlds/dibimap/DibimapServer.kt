package io.github.alexiscomete.lapinousecond.worlds.dibimap

import io.github.alexiscomete.lapinousecond.worlds.WorldEnum
import io.github.alexiscomete.lapinousecond.worlds.Zone

fun isDibimap(id: Long) : Boolean {
    DibimapServer.entries.forEach {
        if (it.serverId == id) {
            return true
        }
    }
    return false
}

fun getValueById(id: Long) : DibimapServer {
    DibimapServer.entries.forEach {
        if (it.serverId == id) {
            return it
        }
    }
    throw IllegalArgumentException("No server with id $id")
}

fun checkById(id: Long) {
    DibimapServer.entries.forEach {
        if (it.serverId == id) {
            return
        }
    }
    throw IllegalArgumentException("No server with id $id")
}

const val DIBIMAP_START_X = 1
const val DIBIMAP_START_Y = 1

enum class DibimapServer(val serverId: Long = 0, val zones: List<Zone> = listOf(), val displayName: String = "") {
    LIGNE224(906247039703195658, listOf(Zone(DIBIMAP_START_X, 224, WorldEnum.DIBIMAP.mapWidth, 224)), "Ligne 224"),
    COEUR_HISTORIQUE(905024934517047307, listOf(Zone(DIBIMAP_START_X, DIBIMAP_START_Y, 110, 50)), "Coeur Historique"),
    CONTREES_MAGMATIQUES(
        941423362339659826, listOf(
            Zone(291, 147, 430, 210),
            Zone(336, 211, 430, 290)
        ), "Contrées Magmatiques"
    ),
    SALINS(905082424055173171, listOf(Zone(291, 120, 430, 146)), "Salins"),
    TERRE_DU_KRAKEN(905886055226552401, listOf(Zone(431, 80, 510, 120)), "Terre du Kraken"),
    GRANDE_CLAIRIERE(906138533595594783, listOf(Zone(431, 51, 470, 79)), "Grande Clairière"),
    TOUNDRA(907720881139236965, listOf(Zone(471, DIBIMAP_START_Y, 550, 50)), "Toundra"),
    FORET_ABYSSALE(905097275506835456, listOf(Zone(331, DIBIMAP_START_Y, 430, 119)), "Forêt Abyssale"),
    ARCHIPEL_DES_CRASHAPAGOS(
        905099121524887625, listOf(
            Zone(DIBIMAP_START_X, 51, 196, 100),
            Zone(111, DIBIMAP_START_Y, 196, 100)
        ), "Archipel des Crashapagos"
    );

    fun isInZones(x: Int, y: Int): Boolean {
        return zones.any { it.contains(x, y) }
    }

    override fun toString(): String {
        return "zones " + zones.joinToString { "$it, " } + " sur le serveur $displayName"
    }
}