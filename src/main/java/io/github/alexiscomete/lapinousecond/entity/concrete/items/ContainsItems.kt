package io.github.alexiscomete.lapinousecond.entity.concrete.items

interface ContainsItems {
    val ownerType: String
    val ownerString: String

    fun getAllItems(): List<Item>
    fun addItem(item: Item)
}