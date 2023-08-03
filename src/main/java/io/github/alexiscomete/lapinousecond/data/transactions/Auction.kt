package io.github.alexiscomete.lapinousecond.data.transactions

import io.github.alexiscomete.lapinousecond.data.managesave.CacheCustom
import io.github.alexiscomete.lapinousecond.data.managesave.CacheGetSet
import io.github.alexiscomete.lapinousecond.data.managesave.Table
import io.github.alexiscomete.lapinousecond.entity.concrete.resources.Resource
import io.github.alexiscomete.lapinousecond.entity.entities.PlayerData
import io.github.alexiscomete.lapinousecond.entity.entities.PlayerManager

val AUCTIONS = Table("auctions")
val auctions = CacheCustom(AUCTIONS) { Auction(it) }

class Auction(id: Long) : CacheGetSet(id, AUCTIONS), Transaction {
    override val who: PlayerData
        get() = PlayerManager[this["who"].toLong()].playerData
    override val amount: Double
        get() = this["amount"].toDouble()
    override val what: Resource
        get() = Resource.valueOf(this["what"].uppercase())
    override val amountRB: Double
        get() = this["amountRB"].toDouble()
    val whoMax: PlayerData
        get() = PlayerManager[this["whoMax"].toLong()].playerData
}