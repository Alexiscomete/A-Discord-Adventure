package io.github.alexiscomete.lapinousecond.entity

import io.github.alexiscomete.lapinousecond.entity.effects.Effect
import io.github.alexiscomete.lapinousecond.entity.effects.EffectEnum
import io.github.alexiscomete.lapinousecond.entity.effects.TimedEffect
import io.github.alexiscomete.lapinousecond.entity.items.ContainsItems
import io.github.alexiscomete.lapinousecond.entity.items.Item
import io.github.alexiscomete.lapinousecond.entity.items.items.StrasbourgSausage
import io.github.alexiscomete.lapinousecond.entity.items.itemsCacheCustom
import io.github.alexiscomete.lapinousecond.entity.resources.Resource
import io.github.alexiscomete.lapinousecond.entity.resources.ResourceManager
import io.github.alexiscomete.lapinousecond.roles.Role
import io.github.alexiscomete.lapinousecond.useful.managesave.*
import io.github.alexiscomete.lapinousecond.view.AnswerEnum
import io.github.alexiscomete.lapinousecond.view.LangageEnum
import io.github.alexiscomete.lapinousecond.view.answerManager
import io.github.alexiscomete.lapinousecond.worlds.Place
import io.github.alexiscomete.lapinousecond.worlds.WorldEnum
import io.github.alexiscomete.lapinousecond.worlds.map.PixelManager
import io.github.alexiscomete.lapinousecond.worlds.places

val PLAYERS = Table("players")
val players = CacheCustom(PLAYERS) { id: Long -> Player(id) }

open class Player(id: Long) : CacheGetSet(id, PLAYERS), Owner, ContainsItems {

    var workTime: Long
        private set
    val roles: ArrayList<Role>
    val resourceManagers: HashMap<Resource, ResourceManager>
    val level: Level = Level(this, "xp")
    var lastLevelUpdate = 0L

    init {
        workTime = 0
        roles = ArrayList()
        resourceManagers = ResourceManager.stringToArray(this["ressources"])
        
        //temporary
        val idTemp = generateUniqueID()
        itemsCacheCustom.add(idTemp)
        addItem(StrasbourgSausage(idTemp))
    }

    fun updateWorkTime() {
        workTime = System.currentTimeMillis()
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


    fun setPath(path: ArrayList<PixelManager>, type: String, speed: Long) {
        savePath(path)
        this["place_${this["world"]}_path_type"] = type
        this["place_${this["world"]}_path_start"] = System.currentTimeMillis().toString()
        this["place_${this["world"]}_type"] = "path"
        this["place_${this["world"]}_speed"] = speed.toString()
    }

    private fun getPath(): ArrayList<PixelManager> {
        val currentPath = stringSaveToPath()
        if (currentPath.isEmpty()) {
            this["place_${this["world"]}_type"] = "coos"
            return ArrayList()
        }
        val lastPixel = currentPath[currentPath.size - 1]
        val startTime = getString("place_${this["world"]}_path_start").toLong()
        val timeForOnePixel = try {
            getString("place_${this["world"]}_speed").toLong()
        } catch (e: NumberFormatException) {
            1000L
        }
        val currentTime = System.currentTimeMillis()
        // le temps en ms pour 1 pixel est de timeForOnePixel, il faut enlever tous les pixels déjà parcourus de la liste puis la sauvegarder
        val numberOfPixel = (currentTime - startTime) / timeForOnePixel
        // les pixels à enlever sont au début de la liste, j'ai besoin que des pixels restants
        val remainingPath = ArrayList<PixelManager>()
        for (i in numberOfPixel.toInt() until currentPath.size) {
            remainingPath.add(currentPath[i])
        }
        savePath(remainingPath)
        if (remainingPath.size < 2) {
            this["place_${this["world"]}_type"] = "coos"
            this["place_${this["world"]}_x"] = lastPixel.x.toString()
            this["place_${this["world"]}_y"] = lastPixel.y.toString()
        }
        this["place_${this["world"]}_path_start"] = System.currentTimeMillis().toString()
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
        this["place_${this["world"]}_path"] = pathStr.toString()
    }

    private fun stringSaveToPath(): ArrayList<PixelManager> {
        val pathStr = getString("place_${this["world"]}_path")
        val path = ArrayList<PixelManager>()
        if (pathStr != "") {
            val pathSplit = pathStr.split(";")
            for (i in pathSplit.indices) {
                if (pathSplit[i] != "") {
                    val pixelSplit = pathSplit[i].split(",")
                    val world = WorldEnum.valueOf(getString("world"))
                    path.add(world.getPixel(pixelSplit[0].toInt(), pixelSplit[1].toInt()))
                }
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
                if (path.isEmpty()) {
                    val x = this["place_${world}_x"]
                    val y = this["place_${world}_y"]
                    "Vous êtes dans le monde ${world}, sur des coordonnées ($x, $y)"
                } else {
                    val firstPixel = path[0]
                    val lastPixel = path[path.size - 1]
                    "Vous êtes dans le monde ${world}, sur un chemin. Le premier pixel est (${firstPixel.x}, ${firstPixel.y}), le dernier pixel est (${lastPixel.x}, ${lastPixel.y})"
                }
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

    override fun getAllItems(): ArrayList<Item> {
        // query of all items in the inventory of the player
        val query = "SELECT * FROM items WHERE containsItemsType = 'player' AND containsItemsId = '${id}'"
        val preparedStatement = saveManager.preparedStatement(query)
        val result = saveManager.executeMultipleQueryKey(preparedStatement)
        // list of all items
        val items = ArrayList<Item>()
        // for each item, we create an item object and add it to the list
        for (itemId in result) {
            items.add(itemsCacheCustom[itemId] ?: throw IllegalStateException("Votre inventaire contient un item qui n'existe pas et ne peut donc pas être ouvert. Veuillez contacter un administrateur."))
        }
        return items
    }

    override fun addItem(item: Item) {
        // query to add an item to the inventory of the player
        item["containsItemsType"] = "player"
        item["containsItemsId"] = id.toString()
    }

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

    private val effects = ArrayList<Effect>()

    fun getEffectLevel(effect: EffectEnum): Int {
        updateAndRemoveEffects()
        var level = 0
        for (currentEffect in effects) {
            if (currentEffect.type == effect) {
                if (currentEffect is TimedEffect && currentEffect.isFinished()) {
                    continue
                }
                level += currentEffect.level
            }
        }
        return level
    }

    fun getEffectsCopy(): ArrayList<Effect> {
        return ArrayList(effects)
    }

    fun updateAndRemoveEffects() {
        for (effect in effects) {
            if (effect is TimedEffect) {
                effect.update()
                if (effect.canBeRemovedAutomatically()) {
                    effects.remove(effect)
                }
            }
        }
    }

    fun addEffect(effect: Effect) {
        effects.add(effect)
    }

    fun removeEffects(effect: EffectEnum) {
        for (currentEffect in effects) {
            if (currentEffect.type == effect) {
                effects.remove(currentEffect)
            }
        }
    }
}