package io.github.alexiscomete.lapinousecond.worlds.buildings.autorisations

import io.github.alexiscomete.lapinousecond.entity.entities.Owner

open class AutList : BuildingAutorisation {
    protected var buildingAutorisations = ArrayList<BuildingAutorisation>()
    override fun isAutorise(owner: Owner?): Boolean {
        for (buildingAutorisation in buildingAutorisations) {
            if (buildingAutorisation.isAutorise(owner)) {
                return true
            }
        }
        return false
    }
}