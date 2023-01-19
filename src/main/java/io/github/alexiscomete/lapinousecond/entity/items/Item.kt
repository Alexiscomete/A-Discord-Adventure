package io.github.alexiscomete.lapinousecond.entity.items

import io.github.alexiscomete.lapinousecond.useful.managesave.*

val ITEMS = Table("items")
val items = CacheCustom<Item>(ITEMS) { id: Long ->
    val type = save!!.getString(ITEMS, "type", "TEXT", id, false)
    if (type == "normal") {
        NormalItem(id)
    } else {
        throw IllegalStateException("Unknown item type")
    }
}

abstract class Item(id: Long) : CacheGetSet(id, ITEMS)
