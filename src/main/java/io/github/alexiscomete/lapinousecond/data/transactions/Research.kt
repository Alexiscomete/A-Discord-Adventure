package io.github.alexiscomete.lapinousecond.data.transactions

import io.github.alexiscomete.lapinousecond.data.managesave.CacheCustom
import io.github.alexiscomete.lapinousecond.data.managesave.CacheGetSet
import io.github.alexiscomete.lapinousecond.data.managesave.Table
import io.github.alexiscomete.lapinousecond.entity.concrete.resources.Resource
import io.github.alexiscomete.lapinousecond.entity.entities.PlayerManager

val RESEARCHES = Table("researches")
val researches = CacheCustom(RESEARCHES) { Research(it) }

class Research(id: Long) : CacheGetSet(id, RESEARCHES), Transaction {
    override val who: PlayerManager
        get() = PlayerManager[this["who"].toLong()]
    override val amount: Double
        get() = this["amount"].toDouble()
    override val what: Resource
        get() = Resource.valueOf(this["what"].uppercase())
    override val amountRB: Double
        get() = this["amountRB"].toDouble()
}