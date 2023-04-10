package io.github.alexiscomete.lapinousecond.entity.entities

import io.github.alexiscomete.lapinousecond.entity.concrete.resources.Resource

val ownerInfinity = OwnerInfinity()

// simule un owner avec des ressources infinis. La banque
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

}
