package io.github.alexiscomete.lapinousecond.entity

import io.github.alexiscomete.lapinousecond.resources.Resource

val ownerInfinity = OwnerInfinity()

class OwnerInfinity : Owner {
    override val ownerType: String
        get() = "Infinity"
    override val ownerString: String
        get() = "Infinity"

    override fun addMoney(amount: Double) {
        // do nothing
    }

    override fun getMoney(): Double {
        return Double.POSITIVE_INFINITY
    }

    override fun removeMoney(amount: Double) {
        // do nothing
    }

    override fun addResource(resource: Resource, amount: Double) {
        // do nothing
    }

    override fun getResource(resource: Resource): Double {
        return Double.POSITIVE_INFINITY
    }

    override fun removeResource(resource: Resource, amount: Double) {
           // do nothing
    }

    override fun hasResource(resource: Resource, amount: Double): Boolean {
        return true
    }

    override fun hasMoney(amount: Double): Boolean {
        return true
    }

    override fun askValidation(
        owner1: Owner,
        amount0: Double,
        ressource0: Resource,
        amount1: Double,
        ressource1: Resource,
        validation: (Boolean) -> Unit
    ) {
        TODO("Not yet implemented")
    }

}