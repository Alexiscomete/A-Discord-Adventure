package io.github.alexiscomete.lapinousecond.entity.concrete.items.items

import io.github.alexiscomete.lapinousecond.entity.concrete.items.Item
import io.github.alexiscomete.lapinousecond.entity.concrete.items.interfaces.*

class StrasbourgSausage(id: Long) : Item(id), CanBeBrocken, CanBeSell, CanBeUsed, DefenseEffect, PassiveEffect {
}
