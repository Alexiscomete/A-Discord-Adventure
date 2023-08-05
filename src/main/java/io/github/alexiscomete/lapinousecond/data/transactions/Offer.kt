package io.github.alexiscomete.lapinousecond.data.transactions

import io.github.alexiscomete.lapinousecond.data.managesave.CacheCustom
import io.github.alexiscomete.lapinousecond.data.managesave.CacheGetSet
import io.github.alexiscomete.lapinousecond.data.managesave.Table
import io.github.alexiscomete.lapinousecond.entity.concrete.resources.Resource
import io.github.alexiscomete.lapinousecond.entity.entities.PlayerManager

val OFFERS = Table("offers")
val offers = CacheCustom(OFFERS) { Offer(it) }

class Offer(id: Long) : CacheGetSet(id, OFFERS), Transaction {
    override val who: PlayerManager
        get() = PlayerManager[this["who"].toLong()]
    override val amount: Double
        get() = this["amount"].toDouble()
    override val what: Resource
        get() = Resource.valueOf(this["what"].uppercase())
    override val amountRB: Double
        get() = this["amountRB"].toDouble()
}