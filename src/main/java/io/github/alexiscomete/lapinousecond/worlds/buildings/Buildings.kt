package io.github.alexiscomete.lapinousecond.worlds.buildings

import io.github.alexiscomete.lapinousecond.useful.managesave.saveManager
import io.github.alexiscomete.lapinousecond.worlds.buildings.autorisations.BuildingAutorisations
import io.github.alexiscomete.lapinousecond.worlds.buildings.evolution.Evolution
import io.github.alexiscomete.lapinousecond.worlds.buildings.interactions.*
import org.json.JSONObject

enum class Buildings(private val getBuildingM: (Building) -> BuildingInteraction, val name_: String) {
    ARMURERIE({ building: Building -> Armurerie(building) }, "armurerie"
    ),
    ARRET_BUS(
        { building: Building -> ArretBus(building) }, "arret_bus"
    ),
    AUBERGE(
        { building: Building -> Auberge(building) }, "auberge"
    ),
    BANQUE(
        { building: Building -> Banque(building) }, "banque"
    ),
    BAR(
        { building: Building -> Bar(building) }, "bar"
    ),
    BIBLIOTHEQUE(
        { building: Building -> Bibliotheque(building) }, "bibliotheque"
    ),
    BOULANGERIE(
        { building: Building -> Boulangerie(building) }, "boulangerie"
    ),
    BOUTIQUE(
        { building: Building -> Boutique(building) }, "boutique"
    ),
    CASINO(
        { building: Building -> Casino(building) }, "casino"
    ),
    HOPITAL(
        { building: Building -> Hopital(building) }, "hopital"
    ),
    JOURNAL(
        { building: Building? -> Journal(building) }, "journal"
    ),
    MAIRIE(
        { building: Building? -> Mairie(building) }, "mairie"
    ),
    MAISON(
        { building: Building? -> Maison(building) }, "maison"
    ),
    PHARMACIE(
        { building: Building -> Pharmacie(building) }, "pharmacie"
    );

    var basePrice = 0.0
        private set
    val evol: ArrayList<Evolution> = ArrayList()
    var isBuild = true
        private set
    var buildingAutorisations: BuildingAutorisations? = null

    init {
        Building.jsonObject?.let { setModelWithJson(it.getJSONObject(name_)) }
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
                    println("null")
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