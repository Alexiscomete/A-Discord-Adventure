package io.github.alexiscomete.lapinousecond.useful.transactions

import io.github.alexiscomete.lapinousecond.entity.Player
import io.github.alexiscomete.lapinousecond.entity.players
import io.github.alexiscomete.lapinousecond.resources.Resource
import io.github.alexiscomete.lapinousecond.useful.managesave.CacheCustom
import io.github.alexiscomete.lapinousecond.useful.managesave.CacheGetSet
import io.github.alexiscomete.lapinousecond.useful.managesave.Table

val OFFERS = Table("offers")
val offers = CacheCustom(OFFERS) { Offer(it) }

class Offer(id: Long) : CacheGetSet(id, OFFERS), BaseTransaction {
    override val who: Player
        get() = players[this["who"].toLong()]!!
    override val amount: Int
        get() = this["amount"].toInt()
    override val what: Resource
        get() = Resource.valueOf(this["what"].uppercase())
    override val amountRB: Double
        get() = this["amountRB"].toDouble()
}