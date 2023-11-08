package io.github.alexiscomete.lapinousecond.entity.concrete.items.items

import io.github.alexiscomete.lapinousecond.entity.concrete.items.Item

class ComputerItem(id: Long) : Item(id) {
    init {
        if (this["type"] == "") {
            this["type"] = "ComputerItem"
        }
        if (name == "") {
            name = "Ordinateur"
        }
        if (description == "") {
            description = "Un ordinateur, pour le moment il ne sert à rien"
        }
    }
}
