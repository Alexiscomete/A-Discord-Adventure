package io.github.alexiscomete.lapinousecond.entity

import alexiscomete.managesave.CacheCustom
import alexiscomete.managesave.CacheGetSet
import alexiscomete.managesave.Table
import io.github.alexiscomete.lapinousecond.resources.Resource

val COMPANY = (Table("company"))
val companies = CacheCustom(COMPANY) { id: Long ->
    Company(
        id
    )
}

class Company(id: Long) : CacheGetSet(id, COMPANY), Owner {

    override val ownerType: String
        get() = "company"
    override val ownerString: String
        get() = id.toString()

    override fun addMoney(amount: Double) {
        TODO("Not yet implemented")
    }

    override fun getMoney(): Double {
        TODO("Not yet implemented")
    }

    override fun removeMoney(amount: Double) {
        TODO("Not yet implemented")
    }

    override fun addResource(resource: Resource, amount: Double) {
        TODO("Not yet implemented")
    }

    override fun getResource(resource: Resource): Double {
        TODO("Not yet implemented")
    }

    override fun removeResource(resource: Resource, amount: Double) {
        TODO("Not yet implemented")
    }

    override fun hasResource(resource: Resource, amount: Double): Boolean {
        TODO("Not yet implemented")
    }

    override fun hasMoney(amount: Double): Boolean {
        TODO("Not yet implemented")
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

    override fun askAmount(owner0: Owner, function: (Double) -> Unit) {
        TODO("Not yet implemented")
    }

    override fun askRessource(owner1: Owner, function: (Resource) -> Unit) {
        TODO("Not yet implemented")
    }
}