package io.github.alexiscomete.lapinousecond.entity.concrete.items

import io.github.alexiscomete.lapinousecond.data.managesave.CacheCustom
import io.github.alexiscomete.lapinousecond.data.managesave.CacheGetSet
import io.github.alexiscomete.lapinousecond.data.managesave.Table
import io.github.alexiscomete.lapinousecond.view.exceptions.GameStateException

val ITEMS = Table("items")

class ItemData private constructor(
    id: Long
) : CacheGetSet(id, ITEMS) {

    companion object {
        private val itemsData = CacheCustom(ITEMS) { id: Long ->
            ItemData(id)
        }

        operator fun get(id: Long): ItemData {
            return itemsData[id]
                ?: throw NullPointerException("Item $id is does not exist.")
        }

        fun getOrNull(id: Long): ItemData? = itemsData[id]

        fun createItemData(id: Long, type: ItemTypesEnum, name: String? = null, description: String? = null): ItemData {
            val pM = itemsData[id]
            if (pM != null) {
                throw NullPointerException("Item $id already exists.")
            }
            itemsData.add(id)
            val data = ItemData(id)
            data.initCustom(type, name, description)
            return itemsData[id]
                ?: throw GameStateException("Impossible de créer votre objet")
        }

        fun clearInsideCache() {
            itemsData.clearInsideCache()
        }
    }

    fun initCustom(type: ItemTypesEnum, name: String?, description: String?) {
        this["type"] = type.typeInDatabase
        if (name != null)
            this["name"] = name
        if (description != null)
            this["description"] = description
    }
}
