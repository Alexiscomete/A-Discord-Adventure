package io.github.alexiscomete.lapinousecond.worlds.buildings.autorisations

import io.github.alexiscomete.lapinousecond.entity.entities.Owner

fun interface BuildingAutorisation {
    fun isAutorise(owner: Owner?): Boolean
}