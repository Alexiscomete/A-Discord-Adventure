package io.github.alexiscomete.lapinousecond.entity.concrete.items.items

import io.github.alexiscomete.lapinousecond.entity.concrete.items.Item

class BaseSwordItem(id: Long) : Item(id) {
    init {
        if (this["type"] == "") {
            this["type"] = "BaseSwordItem"
        }
        if (name == "") {
            name = "Épée de base"
        }
        if (description == "") {
            description = "Une épée de base"
        }
    }
}
