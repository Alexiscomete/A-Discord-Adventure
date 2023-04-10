package io.github.alexiscomete.lapinousecond.worlds.buildings.autorisations

import org.json.JSONArray

class ChoicePlaceAutorisation(jsonArray: JSONArray) : AutorisationList() {
    init {
        for (i in 0 until jsonArray.length()) {
            buildingAutorisations.add(toAutorisation(jsonArray.getString(i)))
        }
    }
}