package worlds.buildings.autorisations

import entity.Owner

class AllAutorisation : BuildingAutorisation {
    override fun isAutorise(owner: Owner?): Boolean {
        return true
    }
}