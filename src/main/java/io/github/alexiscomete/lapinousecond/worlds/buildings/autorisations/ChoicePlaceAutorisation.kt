package io.github.alexiscomete.lapinousecond.worlds.buildings.autorisations

import org.json.JSONArray

class ChoicePlaceAutorisation(jsonArray: JSONArray) : AutList() {
    init {
        for (i in 0 until jsonArray.length()) {
            buildingAutorisations.add(BuildingAutorisations.Companion.toAutorisation(jsonArray.getString(i)))
        }
    }
}