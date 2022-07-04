package worlds.buildings.evolution

import worlds.buildings.Building
import worlds.buildings.Buildings
import org.json.JSONObject
import java.util.*

class Evolution {
    private val evolutionTarget: Buildings
    private val cost: Double

    constructor(evolutionTarget: Buildings, cost: Double) {
        this.evolutionTarget = evolutionTarget
        this.cost = cost
    }

    constructor(jsonObject: JSONObject) {
        evolutionTarget = Buildings.valueOf(jsonObject.getString("name").uppercase(Locale.getDefault()))
        cost = jsonObject.getDouble("cost")
    }

    fun evolute(building: Building) {
        val buildMethods = evolutionTarget[building]
        building.evolute(buildMethods)
        building["build_status"] = "evolute"
        building["collect_value"] = "0.0"
        building["collect_target"] = "0.0"
    }
}