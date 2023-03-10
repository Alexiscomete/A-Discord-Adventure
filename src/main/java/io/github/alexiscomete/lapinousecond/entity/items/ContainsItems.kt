package io.github.alexiscomete.lapinousecond.entity.items

interface ContainsItems {
    val ownerType: String
    val ownerString: String

    fun getAllItems(): List<Item>
    fun addItem(item: Item)
}