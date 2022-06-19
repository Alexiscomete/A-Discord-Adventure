package io.github.alexiscomete.lapinousecond.entity

interface Owner {
    val ownerType: String
    val ownerString: String
    fun addMoney(amount: Double)
    fun getMoney() : Double
    fun removeMoney(amount: Double)
}