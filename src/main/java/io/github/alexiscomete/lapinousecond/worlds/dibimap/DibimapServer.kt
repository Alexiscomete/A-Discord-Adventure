package io.github.alexiscomete.lapinousecond.worlds.dibimap

import io.github.alexiscomete.lapinousecond.worlds.WorldEnum
import io.github.alexiscomete.lapinousecond.worlds.Zone

fun isDibimap(id: Long) : Boolean {
    DibimapServer.values().forEach {
        if (it.serverId == id) {
            return true
        }
    }
    return false
}

enum class DibimapServer(val serverId: Long = 0, val zones: List<Zone> = listOf(), val name_: String = "") {
    LIGNE224(906247039703195658, listOf(Zone(1, 224, WorldEnum.DIBIMAP.mapWidth, 224)), "Ligne 224"),
    COEUR_HISTORIQUE(905024934517047307, listOf(Zone(1, 1, 110, 50)), "Coeur Historique"),
    CONTREES_MAGMATIQUES(
        941423362339659826, listOf(
            Zone(147, 291, 210, 430),
            Zone(211, 336, 290, 430)
        ), "Contrées Magmatiques"
    ),
    SALINS(905082424055173171, listOf(Zone(291, 120, 430, 146)), "Salins"),
    TERRE_DU_KRAKEN(905886055226552401, listOf(Zone(431, 80, 510, 120)), "Terre du Kraken"),
    GRANDE_CLAIRIERE(906138533595594783, listOf(Zone(431, 51, 470, 79)), "Grande Clairière"),
    TOUNDRA(907720881139236965, listOf(Zone(471, 1, 550, 50)), "Toundra"),
    FORET_ABYSSALE(905097275506835456, listOf(Zone(331, 1, 430, 119)), "Forêt Abyssale"),
    ARCHIPEL_DES_CRASHAPAGOS(
        905099121524887625, listOf(
            Zone(1, 51, 196, 100),
            Zone(111, 1, 196, 100)
        ), "Archipel des Crashapagos"
    );

    fun isInZones(x: Int, y: Int): Boolean {
        return zones.any { it.contains(x, y) }
    }

    override fun toString(): String {
        return "zones " + zones.joinToString { it.toString() + ", " } + " sur le serveur $name_"
    }
}