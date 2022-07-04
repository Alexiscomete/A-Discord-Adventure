package worlds.buildings.autorisations

import entity.Owner

class TypeAutorisation<U>(private val uClass: Class<U>) : BuildingAutorisation {
    override fun isAutorise(owner: Owner?): Boolean {
        return uClass.isInstance(owner)
    }
}