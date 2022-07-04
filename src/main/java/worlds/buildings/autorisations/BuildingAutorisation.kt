package worlds.buildings.autorisations

import entity.Owner

fun interface BuildingAutorisation {
    fun isAutorise(owner: Owner?): Boolean
}