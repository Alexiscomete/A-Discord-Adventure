package io.github.alexiscomete.lapinousecond.worlds

import io.github.alexiscomete.lapinousecond.worlds.Zone.Companion.fromString

class PlaceZones : Place {
    var zones = ArrayList<Zone>()

    constructor() : super()
    constructor(id: Long) : super(id) {
        val zonesBDD = getString("zones")
        if (zonesBDD != "") {
            val zonesTab = zonesBDD.split(";".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            for (zone in zonesTab) {
                if (zone != "") {
                    zones.add(fromString(zone))
                }
            }
        }
    }

    fun addZone(zone: Zone) {
        zones.add(zone)
        updateBDD()
    }

    override fun toString(): String {
        val zonesString = StringBuilder()
        for (zone in zones) {
            zonesString.append(zone.toString()).append(";")
        }
        return zonesString.toString()
    }

    fun updateBDD() {
        set("zones", toString())
    }

    fun isInZones(x: Int, y: Int): Boolean {
        for (zone in zones) {
            if (zone.contains(x, y)) {
                return true
            }
        }
        return false
    }

    fun removeZone(index: Int) {
        zones.removeAt(index)
        updateBDD()
    }
}