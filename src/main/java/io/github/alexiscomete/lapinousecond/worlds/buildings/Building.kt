package io.github.alexiscomete.lapinousecond.worlds.buildings

import io.github.alexiscomete.lapinousecond.entity.concrete.resources.Resource
import io.github.alexiscomete.lapinousecond.entity.entities.Owner
import io.github.alexiscomete.lapinousecond.data.dataclass.ProgressionBar
import io.github.alexiscomete.lapinousecond.data.managesave.CacheCustom
import io.github.alexiscomete.lapinousecond.data.managesave.CacheGetSet
import io.github.alexiscomete.lapinousecond.data.managesave.Table
import io.github.alexiscomete.lapinousecond.data.managesave.generateUniqueID
import io.github.alexiscomete.lapinousecond.view.ui.longuis.LongCustomUI
import io.github.alexiscomete.lapinousecond.view.ui.playerui.PlayerUI
import io.github.alexiscomete.lapinousecond.worlds.Place
import org.json.JSONObject
import java.io.InputStream
import java.util.*

val BUILDINGS = (Table("buildings"))
val buildings = CacheCustom(BUILDINGS) { aLong: Long -> Buildings.load(aLong.toString()) }

class Building : CacheGetSet, BuildMethods, Owner {
    private var buildingInteraction: BuildingInteraction? = null
    private val progressionBar: ProgressionBar

    // constructor for the building if it's already exist
    constructor(id: Long, buildings: Buildings) : super(id, BUILDINGS) {
        progressionBar = ProgressionBar(
            "💰",
            "🧱",
            "🔨",
            getString("collect_target").toDouble(),
            getString("collect_value").toDouble(),
            10
        )
        buildingInteraction = buildings[this]
    }

    // constructor for the building if it's not exist yet
    @SafeVarargs
    constructor(
        buildings1: Buildings,
        owner: Owner,
        place: Place,
        vararg specialInfos: AbstractMap.SimpleEntry<String?, String?>?
    ) : super(generateUniqueID(), BUILDINGS) {
        buildings.add(id)
        var buildingsString: String = place.getString("buildings")
        buildingsString += ";$id"
        place["buildings"] = buildingsString
        set("collect_target", buildings1.basePrice.toString())
        set("type", buildings1.name.lowercase())
        set("build_status", "building")
        set("owner_type", owner.ownerType)
        set("owner", owner.ownerString)
        set("collect_value", "0.0")
        for (special in specialInfos) {
            if (special != null) {
                special.key?.let { special.value?.let { it1 -> set(it, it1) } }
            }
        }
        progressionBar = ProgressionBar("💰", "🧱", "🔨", buildings1.basePrice, 0.0, 10)
    }

    override fun title(): String {
        return "${getString("nameRP")} - ${getString("type")}"
    }

    override fun descriptionShort(): String {
        return "ID : $id\nPropriétaire : ${getString("owner")}\n${
            if (getString("build_status") == "building") "En construction : ${progressionBar.bar}\n"
            else ""
        }${
            getString(
                "description"
            )
        }"
    }

    fun evolute(buildingInteraction: BuildingInteraction?) {
        this.buildingInteraction = buildingInteraction
    }

    // --------------------------------------------------
    // --------------- BUILDING INTERACTION -------------
    // --------------------------------------------------
    override fun configBuilding() {
        buildingInteraction!!.configBuilding()
    }

    override fun interpret(args: Array<String>) {
        buildingInteraction!!.interpret(args)
    }

    override val help: String?
        get() = buildingInteraction!!.help
    override val usage: String?
        get() = buildingInteraction!!.usage

    override fun getCompleteInfos(ui: PlayerUI): LongCustomUI {
        return buildingInteraction!!.getCompleteInfos(ui)
    }

    override val ownerType: String
        get() = "building"
    override val ownerString: String
        get() = id.toString()

    override fun getMoney(): Double {
        // TODO : complex money if building is finish
        return getString("collect_value").toDouble()
    }

    override fun addMoney(amount: Double) {
        set("collect_value", (getString("collect_value").toDouble() + amount).toString())
        if (getMoney() >= getString("collect_target").toDouble()) {
            set("build_status", "finish")
            configBuilding()
        }
    }

    override fun removeMoney(amount: Double) {
        set("collect_value", (getString("collect_value").toDouble() - amount).toString())
    }

    override fun addResource(resource: Resource, amount: Double) {
        TODO("Not yet implemented")
    }

    override fun getResource(resource: Resource): Double {
        TODO("Not yet implemented")
    }

    override fun removeResource(resource: Resource, amount: Double) {
        TODO("Not yet implemented")
    }

    override fun hasResource(resource: Resource, amount: Double): Boolean {
        TODO("Not yet implemented")
    }

    override fun hasMoney(amount: Double): Boolean {
        return getString("collect_value").toDouble() >= amount
    }

}

val inputStream: InputStream? = Building::class.java.classLoader.getResourceAsStream("config_buildings.json")
    .also { println(it) }

// the json file who contains buildings type information
var jsonObject: JSONObject = if (inputStream == null) {
    JSONObject("{}")
} else {
    val sc = Scanner(inputStream)
    val stringBuilder = StringBuilder()
    sc.forEachRemaining { str: String? ->
        stringBuilder.append(
            str
        )
    }
    JSONObject(stringBuilder.toString())
}
