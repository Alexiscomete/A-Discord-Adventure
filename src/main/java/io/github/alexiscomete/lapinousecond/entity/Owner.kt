package io.github.alexiscomete.lapinousecond.entity

import io.github.alexiscomete.lapinousecond.resources.Resource

interface Owner {
    val ownerType: String
    val ownerString: String
    fun addMoney(amount: Double)
    fun getMoney() : Double
    fun removeMoney(amount: Double)
    fun addResource(resource: Resource, amount: Double)
    fun getResource(resource: Resource) : Double
    fun removeResource(resource: Resource, amount: Double)
    fun hasResource(resource: Resource, amount: Double) : Boolean
    fun hasMoney(amount: Double) : Boolean
    fun askValidation(owner1: Owner, amount0: Double, ressource0: Resource, amount1: Double, ressource1: Resource, validation: (Boolean) -> Unit)
    fun askAmount(owner0: Owner, function: (Double) -> Unit)
    fun askRessource(owner1: Owner, function: (Resource) -> Unit)
}