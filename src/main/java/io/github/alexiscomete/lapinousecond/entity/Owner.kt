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
}