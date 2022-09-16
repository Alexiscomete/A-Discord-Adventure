package io.github.alexiscomete.lapinousecond.worlds.buildings

import io.github.alexiscomete.lapinousecond.useful.managesave.CacheCustom
import io.github.alexiscomete.lapinousecond.useful.managesave.Table
import io.github.alexiscomete.lapinousecond.useful.managesave.generateUniqueID
import io.github.alexiscomete.lapinousecond.buttonsManager
import io.github.alexiscomete.lapinousecond.entity.Owner
import io.github.alexiscomete.lapinousecond.entity.Player
import io.github.alexiscomete.lapinousecond.useful.managesave.CacheGetSet
import io.github.alexiscomete.lapinousecond.resources.Resource
import io.github.alexiscomete.lapinousecond.useful.ProgressionBar
import io.github.alexiscomete.lapinousecond.view.AnswerEnum
import io.github.alexiscomete.lapinousecond.worlds.Place
import org.javacord.api.entity.message.MessageBuilder
import org.javacord.api.entity.message.component.ActionRow
import org.javacord.api.entity.message.component.Button
import org.javacord.api.entity.message.embed.EmbedBuilder
import org.json.JSONObject
import java.awt.Color
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
        set("type", buildings1.name_)
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

    fun infos(p: Player): EmbedBuilder? {
        return if (getString("build_status") == "building") {
            inBuildInfos(p)
        } else {
            getInfos(p)
        }
    }

    fun title(): String {
        return "${getString("nameRP")} - ${getString("type")}"
    }

    fun descriptionShort(): String {
        return "ID : $id\nPropriétaire : ${getString("owner")}\n${(if (getString("build_status") == "building") "En construction : \n" else "")}${
            getString(
                "description"
            )
        }"
    }

    fun completeInfos(p: Player): MessageBuilder? {
        return if (getString("build_status") == "building") {
            inBuildCompleteInfos(p)
        } else {
            getCompleteInfos(p)
        }
    }

    private fun inBuildInfos(p: Player): EmbedBuilder {
        return EmbedBuilder()
            .setColor(Color.CYAN)
            .setTitle(p.getAnswer(AnswerEnum.BUILDING_BA, true))
            .setDescription(
                """Type : ${getString("type")}
 ID : $id"""
            )
            .addInlineField(
                p.getAnswer(AnswerEnum.OWNER, true), """
     Type : ${getString("type")}
     Identification : ${getString("owner")}
     """.trimIndent()
            )
            .addInlineField(
                p.getAnswer(AnswerEnum.PROGRESSION, true),
                progressionBar.bar + "\n" + getString("collect_value") + "/" + getString("collect_target")
            )
    }

    private fun inBuildCompleteInfos(p: Player): MessageBuilder {
        val id: Long = generateUniqueID()
        val messageBuilder: MessageBuilder = MessageBuilder()
            .addEmbed(inBuildInfos(p))
            .addComponents(
                ActionRow.of(
                    Button.success(id.toString(), "Donner de l'argent")
                )
            )
        buttonsManager.addButton(id) { messageComponentCreateEvent ->
            val msg = messageComponentCreateEvent.buttonInteraction
            /*
            val transaction = FullTransactionWithVerification(
                { aDouble: Double ->
                    set("collect_value", (getString("collect_value").toDouble() + aDouble).toString())
                    if (Objects.equals(getString("collect_value"), getString("collect_target"))) {
                        msg.channel.get().sendMessage("Build terminé")
                        set("build_status", "finish")
                        configBuilding()
                    } else {
                        msg.channel.get().sendMessage(inBuildInfos(p))
                    }
                },
                { aDouble: Double -> p["bal"] = (p["bal"].toDouble() - aDouble).toString() },
                { p["bal"].toDouble() },
                p,
                { getString("collect_target").toDouble() - getString("collect_value").toDouble() })
            transaction.full(msg)

             */
            TODO("todo")
        }
        return messageBuilder
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

    override fun getInfos(p: Player?): EmbedBuilder? {
        return buildingInteraction!!.getInfos(p)
    }

    override fun getCompleteInfos(p: Player?): MessageBuilder? {
        return buildingInteraction!!.getCompleteInfos(p)
    }

    companion object {
        // the json file who contains buildings type information
        var jsonObject: JSONObject? = null

        // load the json file
        init {
            val inputStream = Building::class.java.classLoader.getResourceAsStream("buildings_config.json")
            println(inputStream)
            jsonObject = if (inputStream == null) {
                println("eeee")
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
        }
    }

    override val ownerType: String
        get() = TODO("Not yet implemented")
    override val ownerString: String
        get() = TODO("Not yet implemented")

    override fun addMoney(amount: Double) {
        TODO("Not yet implemented")
    }

    override fun getMoney(): Double {
        TODO("Not yet implemented")
    }

    override fun removeMoney(amount: Double) {
        TODO("Not yet implemented")
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
        TODO("Not yet implemented")
    }

}