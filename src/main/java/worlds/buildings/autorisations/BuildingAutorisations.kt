package worlds.buildings.autorisations

import entity.Company
import entity.Player
import worlds.Place
import org.json.JSONArray
import org.json.JSONObject

class BuildingAutorisations : AutList {
    constructor(buildingAutorisationArrayList: ArrayList<BuildingAutorisation>) {
        buildingAutorisations = buildingAutorisationArrayList
    }

    constructor(jsonArray: JSONArray) {
        val buildingAutorisations = ArrayList<BuildingAutorisation>()
        for (i in 0 until jsonArray.length()) {
            buildingAutorisations.add(toAutorisation(jsonArray.getJSONObject(i)))
        }
        this.buildingAutorisations = buildingAutorisations
    }

    companion object {
        fun toAutorisation(jsonObject: JSONObject): BuildingAutorisation {
            return when (jsonObject.getString("name")) {
                "joueurs" -> TypeAutorisation(Player::class.java)
                "entreprise" -> TypeAutorisation(Company::class.java)
                "ville" -> TypeAutorisation(Place::class.java)
                "habitants" -> Inhabitants()
                "choix_ville" -> ChoicePlaceAutorisation(jsonObject.getJSONArray("possibilites"))
                else -> AllAutorisation()
            }
        }

        fun toAutorisation(string: String?): BuildingAutorisation {
            return when (string) {
                "joueurs" -> TypeAutorisation(Player::class.java)
                "entreprise" -> TypeAutorisation(Company::class.java)
                "ville" -> TypeAutorisation(Place::class.java)
                "habitants" -> Inhabitants()
                else -> AllAutorisation()
            }
        }
    }
}