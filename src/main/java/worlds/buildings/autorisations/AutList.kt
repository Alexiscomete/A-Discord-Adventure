package worlds.buildings.autorisations

import entity.Owner

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