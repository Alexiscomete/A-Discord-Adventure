package io.github.alexiscomete.lapinousecond.worlds.buildings

import io.github.alexiscomete.lapinousecond.*
import io.github.alexiscomete.lapinousecond.worlds.buildings.autorisations.BuildingAutorisations
import io.github.alexiscomete.lapinousecond.worlds.buildings.evolution.Evolution
import java.util.function.Function
import io.github.alexiscomete.lapinousecond.worlds.buildings.interactions.*
import org.json.JSONObject

enum class Buildings(private val getBuildingM: Function<Building, BuildingInteraction>, val name_: String) {
    ARMURERIE(
        Function<Building, BuildingInteraction> { building: Building? ->
            Armurerie(
                building
            )
        }, "armurerie"
    ),
    ARRET_BUS(
        Function<Building, BuildingInteraction> { building: Building? -> ArretBus(building) }, "arret_bus"
    ),
    AUBERGE(
        Function<Building, BuildingInteraction> { building: Building? -> Auberge(building) }, "auberge"
    ),
    BANQUE(
        Function<Building, BuildingInteraction> { building: Building? -> Banque(building) }, "banque"
    ),
    BAR(
        Function<Building, BuildingInteraction> { building: Building? -> Bar(building) }, "bar"
    ),
    BIBLIOTHEQUE(
        Function<Building, BuildingInteraction> { building: Building? -> Bibliotheque(building) }, "bibliotheque"
    ),
    BOULANGERIE(
        Function<Building, BuildingInteraction> { building: Building? -> Boulangerie(building) }, "boulangerie"
    ),
    BOUTIQUE(
        Function<Building, BuildingInteraction> { building: Building? -> Boutique(building) }, "boutique"
    ),
    CASINO(
        Function<Building, BuildingInteraction> { building: Building? -> Casino(building) }, "casino"
    ),
    HOPITAL(
        Function<Building, BuildingInteraction> { building: Building? -> Hopital(building) }, "hopital"
    ),
    JOURNAL(
        Function<Building, BuildingInteraction> { building: Building? -> Journal(building) }, "journal"
    ),
    MAIRIE(
        Function<Building, BuildingInteraction> { building: Building? -> Mairie(building) }, "mairie"
    ),
    MAISON(
        Function<Building, BuildingInteraction> { building: Building? -> Maison(building) }, "maison"
    ),
    PHARMACIE(
        Function<Building, BuildingInteraction> { building: Building? -> Pharmacie(building) }, "pharmacie"
    );

    var basePrice = 0.0
        private set
    val evol: ArrayList<Evolution> = ArrayList()
    var isBuild = true
        private set
    private var buildingAutorisations: BuildingAutorisations? = null

    init {
        Building.jsonObject?.let { setModelWithJson(it.getJSONObject(name_)) }
    }

    private fun setModelWithJson(jsonObject: JSONObject) {
        buildingAutorisations = BuildingAutorisations(jsonObject.getJSONArray("autorisation"))
        basePrice = jsonObject.getDouble("cost")
        isBuild = jsonObject.getBoolean("build")
    }

    operator fun get(building: Building): BuildingInteraction {
        return getBuildingM.apply(building)
    }

    fun getBuildingAutorisations(): BuildingAutorisations? {
        return buildingAutorisations
    }

    companion object {
        fun load(save: String?): Building? {
            var save1 = save ?: return null
            if (save1.contains(":")) {
                save1 = save1.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[1]
            }
            return try {
                val type: String? = saveManager.getBuildingType(save1.toLong())
                if (type == null) {
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