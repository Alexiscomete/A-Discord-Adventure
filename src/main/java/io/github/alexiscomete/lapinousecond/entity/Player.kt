package io.github.alexiscomete.lapinousecond.entity

import io.github.alexiscomete.lapinousecond.resources.Resource
import io.github.alexiscomete.lapinousecond.resources.ResourceManager
import io.github.alexiscomete.lapinousecond.roles.Role
import io.github.alexiscomete.lapinousecond.useful.managesave.CacheCustom
import io.github.alexiscomete.lapinousecond.useful.managesave.CacheGetSet
import io.github.alexiscomete.lapinousecond.useful.managesave.Table
import io.github.alexiscomete.lapinousecond.useful.managesave.saveManager
import io.github.alexiscomete.lapinousecond.view.AnswerEnum
import io.github.alexiscomete.lapinousecond.view.LangageEnum
import io.github.alexiscomete.lapinousecond.view.answerManager
import io.github.alexiscomete.lapinousecond.worlds.Place
import io.github.alexiscomete.lapinousecond.worlds.WorldEnum
import io.github.alexiscomete.lapinousecond.worlds.map.PixelManager
import io.github.alexiscomete.lapinousecond.worlds.places

val PLAYERS = Table("players")
val players = CacheCustom(PLAYERS) { id: Long -> Player(id) }

open class Player(id: Long) : CacheGetSet(id, PLAYERS), Owner {
    var workTime: Long
        private set
    val roles: ArrayList<Role>
    private val items = ArrayList<io.github.alexiscomete.lapinousecond.Item>()
    val resourceManagers: HashMap<Resource, ResourceManager>
    var state = 0
    val level: Level = Level(this, "xp")

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
        saveManager.setValue(PLAYERS, id, "items", itemsList.toString())
    }

    fun addRole(role: Role) {
        roles.add(role)
    }

    fun updateResources() {
        this["ressources"] = ResourceManager.toString(resourceManagers.values)
    }

    fun getAnswer(answerEnum: AnswerEnum, maj: Boolean, format: ArrayList<String> = ArrayList()): String {
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
        var answer = answerManager.formatAnswer(answerManager.getAnswer(langageEnum, answerEnum), format)
        if (maj) {
            answer = answer.substring(0, 1).uppercase() + answer.substring(1)
        }
        println("answer: $answer")
        return answer
    }

    val place: Place?
        get() {
            val worldName = world.progName
            return when (this["place_${worldName}_type"]) {
                "place" -> {
                    val placeID = this["place_${worldName}_id"]
                    return places[placeID.toLong()]
                }

                "city" -> {
                    val cityID = this["place_${worldName}_id"]
                    return places[cityID.toLong()]
                }

                else -> {
                    null
                }
            }
        }


    fun setPath(path: ArrayList<PixelManager>, type: String) {
        savePath(path)
        this["place_path_type"] = type
        this["place_path_start"] = System.currentTimeMillis().toString()
        this["place_DIBIMAP_type"] = "path"
    }

    private fun getPath(): ArrayList<PixelManager> {
        val currentPath = stringSaveToPath()
        if (currentPath.isEmpty()) {
            this["place_DIBIMAP_type"] = "unknown"
            return ArrayList()
        }
        val startTime = getString("place_path_start").toLong()
        val currentTime = System.currentTimeMillis()
        // le temps en ms pour 1 pixel est de 10000, il faut enlever tous les pixels déjà parcourus de la liste puis la sauvegarder
        val numberOfPixel = (currentTime - startTime) / 10000
        // les pixels à enlever sont au début de la liste, j'ai besoin que des pixels restants
        val remainingPath = ArrayList<PixelManager>()
        for (i in numberOfPixel.toInt() until currentPath.size) {
            remainingPath.add(currentPath[i])
        }
        savePath(remainingPath)
        this["place_path_start"] = System.currentTimeMillis().toString()
        return remainingPath
    }

    private fun savePath(remainingPath: ArrayList<PixelManager>) {
        val pathStr = StringBuilder()
        for (pixel in remainingPath) {
            pathStr.append(pixel.x)
            pathStr.append(",")
            pathStr.append(pixel.y)
            pathStr.append(";")
        }
        this["place_path"] = pathStr.toString()
    }

    private fun stringSaveToPath(): ArrayList<PixelManager> {
        val pathStr = getString("place_path")
        val path = ArrayList<PixelManager>()
        if (pathStr != "") {
            val pathSplit = pathStr.split(";")
            for (i in pathSplit.indices) {
                val pixelSplit = pathSplit[i].split(",")
                val world = WorldEnum.valueOf(getString("world"))
                path.add(world.getPixel(pixelSplit[0].toInt(), pixelSplit[1].toInt()))
            }
        }
        return path
    }

    fun positionToString(): String {
        // la première étape est de récupérer le monde
        val world = this["world"]

        // on récupère le type de lieu, on sépare encore en plusieurs possibilités
        return when (this["place_${world}_type"]) {
            "coos" -> {
                // on récupère les coordonnées
                val x = this["place_${world}_x"]
                val y = this["place_${world}_y"]
                // on retourne le résultat
                "Vous êtes dans le monde ${world}, sur des coordonnées ($x, $y)"
            }

            "place" -> {
                val placeID = this["place_${world}_id"]
                "Vous êtes dans le monde ${world}, dans le lieu $placeID"
            }

            "path" -> {
                val path = getPath()
                val firstPixel = path[0]
                val lastPixel = path[path.size - 1]
                "Vous êtes dans le monde ${world}, sur un chemin. Le premier pixel est (${firstPixel.x}, ${firstPixel.y}), le dernier pixel est (${lastPixel.x}, ${lastPixel.y})"
            }

            "city" -> {
                val cityID = this["place_${world}_id"]
                "Vous êtes dans le monde ${world}, dans la ville $cityID"
            }

            "building" -> {
                val buildingID = this["place_${world}_building_id"]
                "Vous êtes dans le monde ${world}, dans le bâtiment $buildingID"
            }

            else -> {
                "Vous êtes dans le monde ${world}, mais vous ne savez pas où vous êtes"
            }
        }

    }

    override val ownerType: String
        get() = "player"
    override val ownerString: String
        get() = id.toString()


    override fun getMoney(): Double {
        return this["bal"].toDouble()
    }

    override fun addMoney(amount: Double) {
        this["bal"] = (getMoney() + amount).toString()
    }

    override fun removeMoney(amount: Double) {
        this["bal"] = (getMoney() - amount).toString()
    }

    override fun getResource(resource: Resource): Double {
        if (resource == Resource.RABBIT_COIN) {
            return getMoney()
        }
        var resourceManager = resourceManagers[resource]
        if (resourceManager == null) {
            resourceManager = ResourceManager(resource, 0)
            resourceManagers[resource] = resourceManager
        }
        return resourceManager.quantity.toDouble()
    }

    override fun removeResource(resource: Resource, amount: Double) {
        if (resource == Resource.RABBIT_COIN) {
            removeMoney(amount)
            return
        }
        val resourceManager = resourceManagers[resource]
        if (resourceManager == null) {
            throw IllegalArgumentException("Le joueur n'a pas de ressource $resource")
        } else {
            resourceManager.quantity = resourceManager.quantity - amount.toInt()
        }
        updateResources()
    }

    override fun hasResource(resource: Resource, amount: Double): Boolean {
        if (resource == Resource.RABBIT_COIN) {
            return getMoney() >= amount
        }
        val resourceManager = resourceManagers[resource]
        return if (resourceManager == null) {
            false
        } else {
            resourceManager.quantity >= amount
        }
    }

    override fun addResource(resource: Resource, amount: Double) {
        if (resource == Resource.RABBIT_COIN) {
            addMoney(amount)
            return
        }
        val resourceManager = resourceManagers[resource]
        if (resourceManager == null) {
            resourceManagers[resource] = ResourceManager(resource, amount.toInt())
        } else {
            resourceManager.quantity = resourceManager.quantity + amount.toInt()
        }
        updateResources()
    }

    override fun hasMoney(amount: Double): Boolean {
        return getMoney() >= amount
    }

    val world
        get() = run {
            val w = this["world"]
            if (w == "") {
                WorldEnum.TUTO
                this["world"] = WorldEnum.TUTO.progName
            }
            WorldEnum.valueOf(w)
        }

    init {
        workTime = 0
        roles = ArrayList()
        resourceManagers = ResourceManager.stringToArray(this["ressources"])
    }
}