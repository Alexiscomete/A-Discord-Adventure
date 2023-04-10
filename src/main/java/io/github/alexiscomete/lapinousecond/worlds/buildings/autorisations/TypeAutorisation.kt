package io.github.alexiscomete.lapinousecond.worlds.buildings.autorisations

import io.github.alexiscomete.lapinousecond.entity.entities.Owner

class TypeAutorisation<U>(private val uClass: Class<U>) : BuildingAutorisation {
    override fun isAutorise(owner: Owner?): Boolean {
        return uClass.isInstance(owner)
    }
}