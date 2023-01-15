package io.github.alexiscomete.lapinousecond.entity.items

import io.github.alexiscomete.lapinousecond.useful.managesave.CacheCustom
import io.github.alexiscomete.lapinousecond.useful.managesave.CacheGetSet
import io.github.alexiscomete.lapinousecond.useful.managesave.Table

val ITEMS = Table("items")
val items = CacheCustom(ITEMS) { id: Long -> Item(id) }

class Item(id: Long) : CacheGetSet(id, ITEMS) {

}
