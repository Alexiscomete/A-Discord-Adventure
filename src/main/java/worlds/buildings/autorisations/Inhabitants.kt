package worlds.buildings.autorisations

import entity.Owner

class Inhabitants : BuildingAutorisation {
    override fun isAutorise(owner: Owner?): Boolean {
        return false
    }
}