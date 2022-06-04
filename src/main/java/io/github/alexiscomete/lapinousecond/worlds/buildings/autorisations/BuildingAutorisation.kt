package io.github.alexiscomete.lapinousecond.worlds.buildings.autorisations

import io.github.alexiscomete.lapinousecond.entity.Owner

fun interface BuildingAutorisation {
    fun isAutorise(owner: Owner?): Boolean
}