package io.github.alexiscomete.lapinousecond.worlds.buildings.autorisations

import io.github.alexiscomete.lapinousecond.entity.entities.Owner

class AllAutorisation : BuildingAutorisation {
    override fun isAutorise(owner: Owner?): Boolean {
        return true
    }
}