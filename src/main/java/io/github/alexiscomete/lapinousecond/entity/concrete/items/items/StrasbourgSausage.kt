package io.github.alexiscomete.lapinousecond.entity.concrete.items.items

import io.github.alexiscomete.lapinousecond.entity.concrete.items.Item
import io.github.alexiscomete.lapinousecond.entity.concrete.items.interfaces.*

class StrasbourgSausage(id: Long) : Item(id), CanBeBrocken, CanBeSell, CanBeUsed, DefenseEffect, PassiveEffect {
    init {
        if (this["type"] == "") {
            this["type"] = "StrasbourgSausage"
        }
        if (name == "") {
            name = "Saucisse de Strasbourg"
        }
        if (description == "") {
            description = "Une saucisse de Strasbourg (ne demandez pas d'explication, c'est Gripain qui a demandé)"
        }
    }
}