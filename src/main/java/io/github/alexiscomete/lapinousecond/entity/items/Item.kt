package io.github.alexiscomete.lapinousecond.entity.items

import io.github.alexiscomete.lapinousecond.entity.items.items.StrasbourgSausage
import io.github.alexiscomete.lapinousecond.entity.players
import io.github.alexiscomete.lapinousecond.useful.managesave.CacheCustom
import io.github.alexiscomete.lapinousecond.useful.managesave.CacheGetSet
import io.github.alexiscomete.lapinousecond.useful.managesave.Table
import io.github.alexiscomete.lapinousecond.useful.managesave.save

val ITEMS = Table("items")
val itemsCacheCustom = CacheCustom(ITEMS) { id: Long ->
    when (save!!.getString(ITEMS, "type", "TEXT", id, false)) {
        "normal" -> NormalItem(id)
        "StrasbourgSausage" -> StrasbourgSausage(id)
        else -> throw IllegalStateException("Unknown item type")
    }
}

abstract class Item(id: Long) : CacheGetSet(id, ITEMS) {
    var name: String
        get() = this["name"]
        set(value) {
            this["name"] = value
        }

    var description: String
        get() = this["description"]
        set(value) {
            this["description"] = value
        }

    var containsItems: ContainsItems
        get() = run {
            val containsItemsType = this["containsItemsType"]
            if (containsItemsType == "player") {
                val playerId = this["containsItemsId"].toLong()
                players[playerId]!!
            } else {
                throw IllegalStateException("Unknown containsItems type")
            }
        }
        set(value) {
            this["containsItemsType"] = value.ownerType
            this["containsItemsId"] = value.ownerString
        }
}
