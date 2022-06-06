package io.github.alexiscomete.lapinousecond.entity

import io.github.alexiscomete.lapinousecond.Item
import io.github.alexiscomete.lapinousecond.*
import io.github.alexiscomete.lapinousecond.resources.Resource
import io.github.alexiscomete.lapinousecond.resources.ResourceManager
import io.github.alexiscomete.lapinousecond.roles.Role
import io.github.alexiscomete.lapinousecond.save.CacheGetSet
import io.github.alexiscomete.lapinousecond.save.SaveManager
import io.github.alexiscomete.lapinousecond.save.Tables
import io.github.alexiscomete.lapinousecond.view.AnswerEnum
import io.github.alexiscomete.lapinousecond.view.answerManager
import io.github.alexiscomete.lapinousecond.view.LangageEnum
import io.github.alexiscomete.lapinousecond.worlds.Place
import io.github.alexiscomete.lapinousecond.worlds.ServerBot
import io.github.alexiscomete.lapinousecond.worlds.map.Pixel

class Player : CacheGetSet, Owner {
    private var bal: Double
    private var server: Long
    private var tuto: Short
    private var hasAccount: Boolean
    var workTime: Long
        private set
    val roles: ArrayList<Role>
    val items = ArrayList<Item>()
    val resourceManagers: HashMap<Resource, ResourceManager>
    var state = 0

    constructor(id: Long?) : super(id!!, Tables.PLAYERS.table) {
        bal = getString("bal").toDouble()
        server = getString("serv").toLong()
        tuto = getString("tuto").toShort()
        hasAccount = getString("has_account") == "1"
        workTime = 0
        roles = ArrayList()
        resourceManagers = ResourceManager.stringToArray(getString("ressources"))
    }

    fun updateWorkTime() {
        workTime = System.currentTimeMillis()
    }

    fun updateItems() {
        val itemsList = StringBuilder()
        for (i in items.indices) {
            val item = items[i]
            itemsList.append(item.jname)
            if (i != items.size - 1) {
                itemsList.append(";")
            }
        }
        saveManager.setValue(Tables.PLAYERS.table, id, "items", itemsList.toString())
    }

    fun getBal(): Double {
        return bal
    }

    fun setBal(bal: Double) {
        this.bal = bal
        saveManager.setValue(Tables.PLAYERS.table, id, "bal", bal.toString())
    }

    fun getServer(): Long {
        return server
    }

    fun setServer(server: Long) {
        this.server = server
        saveManager.setValue(Tables.PLAYERS.table, id, "serv", server.toString())
    }

    fun getTuto(): Short {
        return tuto
    }

    fun setTuto(tuto: Short) {
        this.tuto = tuto
        saveManager.setValue(Tables.PLAYERS.table, id, "tuto", tuto.toString())
    }

    fun hasAccount(): Boolean {
        return hasAccount
    }

    fun setHasAccount(hasAccount: Boolean) {
        this.hasAccount = hasAccount
        saveManager.setValue(Tables.PLAYERS.table, id, "has_account", SaveManager.toBooleanString(hasAccount))
    }

    constructor(
        id: Long,
        bal: Double,
        server: Long,
        tuto: Short,
        hasAccount: Boolean,
        roles: String?,
        resources: String?
    ) : super(id, Tables.PLAYERS.table) {
        this.bal = bal
        this.server = server
        this.tuto = tuto
        this.hasAccount = hasAccount
        workTime = 0
        this.roles = ArrayList()
        resourceManagers = ResourceManager.stringToArray(resources)
    }

    fun addRole(role: Role) {
        roles.add(role)
    }

    fun updateResources() {
        saveManager.setValue(Tables.PLAYERS.table, id, "ressources", ResourceManager.toString(resourceManagers.values))
    }

    fun getAnswer(answerEnum: AnswerEnum?, maj: Boolean, vararg format: Any?): String? {
        val langage = getString("langage")
        val langageEnum: LangageEnum = if (langage == "") {
            LangageEnum.FRENCH
        } else {
            try {
                LangageEnum.valueOf(langage.uppercase())
            } catch (argumentException: IllegalArgumentException) {
                LangageEnum.FRENCH
            }
        }
        var answer = answerEnum?.let { answerManager.getAnswer(langageEnum, it) }
        answer = answer?.let { answerManager.formatAnswer(it, format) }
        if (maj) {
            answer = answer?.substring(0, 1)?.uppercase() + answer?.substring(1)
        }
        return answer
    }

    val place: Place?
        get() {
            var world = getString("current_world")
            if (world == "") {
                world = "NORMAL"
                set("current_world", "NORMAL")
            }
            var placeID = getString("place_$world")
            if (placeID == "") {
                placeID = ServerBot(854288660147994634L).getString("places")
                set("place_NORMAL", ServerBot(854288660147994634L).getString("places"))
            }
            return saveManager.places[placeID.toLong()]
        }

    fun setPath(path: ArrayList<Pixel>, type: String) {
        // TODO: Gérer les chemins
    }

    override val ownerType: String
        get() = "player"
    override val ownerString: String
        get() = id.toString()
}