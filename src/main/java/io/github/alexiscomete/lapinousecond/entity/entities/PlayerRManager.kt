package io.github.alexiscomete.lapinousecond.entity.entities

import io.github.alexiscomete.lapinousecond.entity.concrete.resources.Resource
import io.github.alexiscomete.lapinousecond.entity.concrete.resources.ResourceManager

class PlayerRManager(
    val playerData: PlayerData
) : Owner {
    override val ownerType: String = "player"
    override val ownerString: String = playerData.id.toString()

    val resourceManagers: HashMap<Resource, ResourceManager> = ResourceManager.stringToArray(playerData["ressources"])

    override fun getMoney(): Double {
        return playerData["bal"].toDouble()
    }

    override fun addMoney(amount: Double) {
        playerData["bal"] = (getMoney() + amount).toString()
    }

    override fun removeMoney(amount: Double) {
        playerData["bal"] = (getMoney() - amount).toString()
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
            resourceManager.quantity -= amount.toInt()
        }
        playerData.updateResources()
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
            resourceManager.quantity += amount.toInt()
        }
        playerData.updateResources()
    }

    override fun hasMoney(amount: Double): Boolean {
        return getMoney() >= amount
    }
}