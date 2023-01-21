package io.github.alexiscomete.lapinousecond.entity.items

import io.github.alexiscomete.lapinousecond.useful.managesave.CacheCustom
import io.github.alexiscomete.lapinousecond.useful.managesave.CacheGetSet
import io.github.alexiscomete.lapinousecond.useful.managesave.Table
import io.github.alexiscomete.lapinousecond.useful.managesave.save

val ITEMS = Table("items")
val items = CacheCustom<Item>(ITEMS) { id: Long ->
    val type = save!!.getString(ITEMS, "type", "TEXT", id, false)
    if (type == "normal") {
        NormalItem(id)
    } else {
        throw IllegalStateException("Unknown item type")
    }
}

abstract class Item(id: Long) : CacheGetSet(id, ITEMS) {
    var name: String
        get() = this["name"]
        set(value) {
            this["name"] = value
        }
}
