package io.github.alexiscomete.lapinousecond.worlds.buildings.autorisations

import io.github.alexiscomete.lapinousecond.entity.entities.Player
import io.github.alexiscomete.lapinousecond.worlds.Place
import org.json.JSONArray
import org.json.JSONObject

class BuildingAutorisations(jsonArray: JSONArray) : AutorisationList() {
    init {
        val buildingAutorisations = ArrayList<BuildingAutorisation>()
        for (i in 0 until jsonArray.length()) {
            buildingAutorisations.add(toAutorisation(jsonArray.getJSONObject(i)))
        }
        this.buildingAutorisations = buildingAutorisations
    }
}

fun toAutorisation(jsonObject: JSONObject): BuildingAutorisation {
    return when (jsonObject.getString("name")) {
        "joueurs" -> TypeAutorisation(Player::class.java)
        "ville" -> TypeAutorisation(Place::class.java)
        "habitants" -> Inhabitants()
        "choix_ville" -> ChoicePlaceAutorisation(jsonObject.getJSONArray("possibilites"))
        else -> AllAutorisation()
    }
}

fun toAutorisation(string: String?): BuildingAutorisation {
    return when (string) {
        "joueurs" -> TypeAutorisation(Player::class.java)
        "ville" -> TypeAutorisation(Place::class.java)
        "habitants" -> Inhabitants()
        else -> AllAutorisation()
    }
}