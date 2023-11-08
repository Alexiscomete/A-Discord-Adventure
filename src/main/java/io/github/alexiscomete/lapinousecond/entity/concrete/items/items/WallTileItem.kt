package io.github.alexiscomete.lapinousecond.entity.concrete.items.items

import io.github.alexiscomete.lapinousecond.entity.concrete.items.Item

class WallTileItem(id: Long) : Item(id) {
    init {
        if (this["type"] == "") {
            this["type"] = "WallTileItem"
        }
        if (name == "") {
            name = "Tuile de mur"
        }
        if (description == "") {
            description = "Une mur temporaire que vous pourrez poser dans le monde"
        }
    }
}

