package io.github.alexiscomete.lapinousecond.entity.concrete.items

import io.github.alexiscomete.lapinousecond.data.managesave.CacheCustom
import io.github.alexiscomete.lapinousecond.data.managesave.generateUniqueID
import io.github.alexiscomete.lapinousecond.entity.entities.PlayerManager

abstract class Item protected constructor(val id: Long) {

    companion object {
        private val items = CacheCustom(ITEMS, ItemTypesEnum.Companion::instanciateWithData)

        operator fun get(id: Long): Item {
            return items[id]
                ?: throw NullPointerException("PlayerManager $id is null. Please create an account first with /account start.")
        }

        fun getOrNull(id: Long): Item? = items[id]

        fun createItem(id: Long, type: ItemTypesEnum, name: String? = null, description: String? = null): Item {
            val pM = items[id]
            if (pM != null) {
                throw NullPointerException("PlayerManager $id already exists.")
            }
            ItemData.createItemData(id, type, name, description)
            return items[id] ?: throw NullPointerException("WARNING")
        }

        fun createItem(type: ItemTypesEnum, name: String? = null, description: String? = null): Item {
            return createItem(generateUniqueID(), type, name, description)
        }

        fun clearInsideCache() {
            items.clearInsideCache()
        }
    }

    val data = ItemData[id]

    var customName: String
        get() = data["name"]
        set(value) {
            data["name"] = value
        }

    var description: String
        get() = data["description"]
        set(value) {
            data["description"] = value
        }

    var containsItems: ContainsItems
        get() = run {
            val containsItemsType = data["containsItemsType"]
            if (containsItemsType == "player") {
                val playerId = data["containsItemsId"].toLong()
                PlayerManager[playerId].ownerManager
            } else {
                throw IllegalStateException("Unknown containsItems type")
            }
        }
        set(value) {
            data["containsItemsType"] = value.ownerType
            data["containsItemsId"] = value.ownerString
        }
}
