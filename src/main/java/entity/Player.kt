package entity

import alexiscomete.managesave.CacheCustom
import alexiscomete.managesave.CacheGetSet
import alexiscomete.managesave.Table
import Item
import resources.Resource
import resources.ResourceManager
import roles.Role
import alexiscomete.managesave.saveManager
import view.AnswerEnum
import view.LangageEnum
import view.answerManager
import worlds.Place
import worlds.ServerBot
import worlds.map.Map
import worlds.map.Pixel
import worlds.places

val PLAYERS = Table("players")
val players = CacheCustom(PLAYERS) { id: Long -> Player(id) }

class Player(id: Long) : CacheGetSet(id, PLAYERS), Owner {
    var workTime: Long
        private set
    val roles: ArrayList<Role>
    val items = ArrayList<Item>()
    val resourceManagers: HashMap<Resource, ResourceManager>
    var state = 0

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
            return places[placeID.toLong()]
        }


    fun setPath(path: ArrayList<Pixel>, type: String) {
        savePath(path)
        this["place_path_type"] = type
        this["place_path_start"] = System.currentTimeMillis().toString()
        this["place_DIBIMAP_type"] = "path"
    }

    private fun getPath(): ArrayList<Pixel> {
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
        val remainingPath = ArrayList<Pixel>()
        for (i in numberOfPixel.toInt() until currentPath.size) {
            remainingPath.add(currentPath[i])
        }
        savePath(remainingPath)
        this["place_path_start"] = System.currentTimeMillis().toString()
        return remainingPath
    }

    private fun savePath(remainingPath: ArrayList<Pixel>) {
        val pathStr = StringBuilder()
        for (pixel in remainingPath) {
            pathStr.append(pixel.x)
            pathStr.append(",")
            pathStr.append(pixel.y)
            pathStr.append(";")
        }
        this["place_path"] = pathStr.toString()
    }

    private fun stringSaveToPath(): ArrayList<Pixel> {
        val pathStr = getString("place_path")
        val path = ArrayList<Pixel>()
        if (pathStr != "") {
            val pathSplit = pathStr.split(";")
            for (i in pathSplit.indices) {
                val pixelSplit = pathSplit[i].split(",")
                path.add(Map.getPixel(pixelSplit[0].toInt(), pixelSplit[1].toInt()))
            }
        }
        return path
    }

    fun positionToString(): String {
        // la première étape est de récupérer le monde
        val world = this["current_world"]
        // si le monde est DIBIMAP c'est compliqué, sinon on peut simplement récupérer le serveur
        if (world == "DIBIMAP") {
            // on récupère le type de lieu, on sépare encore en plusieurs possibilités
            return when (this["place_DIBIMAP_type"]) {
                "coos" -> {
                    // on récupère les coordonnées
                    val x = this["place_DIBIMAP_x"]
                    val y = this["place_DIBIMAP_y"]
                    // on retourne le résultat
                    "Vous êtes dans le monde DIBIMAP, sur des coordonnées ($x, $y)"
                }
                "place" -> {
                    val placeID = this["place_DIBIMAP_id"]
                    "Vous êtes dans le monde DIBIMAP, dans le lieu $placeID"
                }
                "path" -> {
                    val path = getPath()
                    val firstPixel = path[0]
                    val lastPixel = path[path.size - 1]
                    "Vous êtes dans le monde DIBIMAP, sur un chemin. Le premier pixel est (${firstPixel.x}, ${firstPixel.y}), le dernier pixel est (${lastPixel.x}, ${lastPixel.y})"
                }
                else -> {
                    "Vous êtes dans le monde DIBIMAP, mais vous ne savez pas où vous êtes"
                }
            }
        } else {
            // on récupère le lieu
            val server = this["place_NORMAL"]
            // on récupère le lieu
            // TODO
            return "Vous êtes dans le monde $world, sur le lieu $server"
        }
    }

    override val ownerType: String
        get() = "player"
    override val ownerString: String
        get() = id.toString()


    override fun getMoney(): Double {
        return this["money"].toDouble()
    }

    override fun addMoney(amount: Double) {
        this["money"] = (getMoney() + amount).toString()
    }

    override fun removeMoney(amount: Double) {
        this["money"] = (getMoney() - amount).toString()
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

    override fun askValidation(
        owner1: Owner,
        amount0: Double,
        ressource0: Resource,
        amount1: Double,
        ressource1: Resource,
        validation: (Boolean) -> Unit
    ) {
        TODO("Not yet implemented")
    }

    override fun askAmount(owner0: Owner, function: (Double) -> Unit) {
        TODO("Not yet implemented")
    }

    override fun askRessource(owner1: Owner, function: (Resource) -> Unit) {
        TODO("Not yet implemented")
    }

    init {
        workTime = 0
        roles = ArrayList()
        resourceManagers = ResourceManager.stringToArray(getString("ressources"))
    }
}