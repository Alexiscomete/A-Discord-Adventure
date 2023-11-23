package io.github.alexiscomete.lapinousecond.entity.entities.managers

import io.github.alexiscomete.lapinousecond.data.managesave.saveManager
import io.github.alexiscomete.lapinousecond.entity.concrete.items.ContainsItems
import io.github.alexiscomete.lapinousecond.entity.concrete.items.Item
import io.github.alexiscomete.lapinousecond.entity.concrete.resources.Resource
import io.github.alexiscomete.lapinousecond.entity.concrete.resources.ResourceManager
import io.github.alexiscomete.lapinousecond.entity.entities.Owner
import io.github.alexiscomete.lapinousecond.entity.entities.PlayerData

class PlayerOwnerManager(
    val playerData: PlayerData
) : Owner, ContainsItems {
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
        playerData.updateResources(resourceManagers)
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
        playerData.updateResources(resourceManagers)
    }

    override fun hasMoney(amount: Double): Boolean {
        return getMoney() >= amount
    }

    override fun getAllItems(): ArrayList<Item> {
        // query of all items in the inventory of the player
        val query = "SELECT * FROM items WHERE containsItemsType = 'player' AND containsItemsId = '${ownerString}'"
        val preparedStatement = saveManager.preparedStatement(query)
        val result = saveManager.executeMultipleQueryKey(preparedStatement)
        // list of all items
        val items = ArrayList<Item>()
        // for each item, we create an item object and add it to the list
        for (itemId in result) {
            Item.getOrNull(itemId)?.let {
                items.add(it)
            }
        }
        return items
    }

    override fun addItem(item: Item) {
        // query to add an item to the inventory of the player
        item.data["containsItemsType"] = ownerType
        item.data["containsItemsId"] = ownerString
    }
}
