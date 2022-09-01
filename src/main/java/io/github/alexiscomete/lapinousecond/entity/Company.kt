package io.github.alexiscomete.lapinousecond.entity

import io.github.alexiscomete.lapinousecond.useful.managesave.CacheCustom
import io.github.alexiscomete.lapinousecond.useful.managesave.CacheGetSet
import io.github.alexiscomete.lapinousecond.useful.managesave.Table
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

}