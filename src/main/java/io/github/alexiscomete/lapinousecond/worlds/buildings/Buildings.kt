package io.github.alexiscomete.lapinousecond.worlds.buildings

import io.github.alexiscomete.lapinousecond.data.managesave.saveManager
import io.github.alexiscomete.lapinousecond.worlds.buildings.autorisations.BuildingAutorisations
import io.github.alexiscomete.lapinousecond.worlds.buildings.evolution.Evolution
import io.github.alexiscomete.lapinousecond.worlds.buildings.interactions.*
import org.json.JSONObject

enum class Buildings(private val getBuildingM: (Building) -> BuildingInteraction) {
    ARMURERIE({ building: Building -> Armurerie(building) }),
    ARRET_BUS({ building: Building -> ArretBus(building) }),
    AUBERGE(
        { building: Building -> Auberge(building) }
    ),
    BANQUE(
        { building: Building -> Banque(building) }
    ),
    BAR(
        { building: Building -> Bar(building) },
    ),
    BIBLIOTHEQUE(
        { building: Building -> Bibliotheque(building) },
    ),
    BOULANGERIE(
        { building: Building -> Boulangerie(building) },
    ),
    BOUTIQUE(
        { building: Building -> Boutique(building) },
    ),
    CASINO(
        { building: Building -> Casino(building) },
    ),
    HOPITAL(
        { building: Building -> Hopital(building) },
    ),
    JOURNAL(
        { building: Building -> Journal(building) },
    ),
    MAIRIE(
        { building: Building -> Mairie(building) },
    ),
    MAISON(
        { building: Building -> Maison(building) },
    ),
    PHARMACIE(
        { building: Building -> Pharmacie(building) },
    );

    var basePrice = 0.0
        private set
    val evol: ArrayList<Evolution> = ArrayList()
    var isBuild = true
        private set
    var buildingAutorisations: BuildingAutorisations? = null

    init {
        setModelWithJson(jsonObject.getJSONObject(name.lowercase()))
    }

    private fun setModelWithJson(jsonObject: JSONObject) {
        buildingAutorisations = BuildingAutorisations(jsonObject.getJSONArray("autorisation"))
        basePrice = jsonObject.getDouble("cost")
        isBuild = jsonObject.getBoolean("build")
    }

    operator fun get(building: Building): BuildingInteraction {
        return getBuildingM(building)
    }

    companion object {
        fun load(save: String): Building? {
            var save1 = save
            if (save1.contains(":")) {
                save1 = save1.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[1]
            }
            return try {
                val type: String = saveManager.typeOf(save1.toLong(), "buildings")
                if (type == "") {
                    return null
                }
                val buildings = valueOf(type.uppercase())
                Building(save1.toLong(), buildings)
            } catch (e: IllegalArgumentException) {
                null
            }
        }

        fun loadBuildings(str: String): ArrayList<Building> {
            val buildings = ArrayList<Building>()
            val strings = str.split(";".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            for (s in strings) {
                val b = load(s)
                if (b != null) {
                    buildings.add(b)
                }
            }
            return buildings
        }
    }
}