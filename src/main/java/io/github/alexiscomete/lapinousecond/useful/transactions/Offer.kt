package io.github.alexiscomete.lapinousecond.useful.transactions

import io.github.alexiscomete.lapinousecond.useful.managesave.CacheCustom
import io.github.alexiscomete.lapinousecond.useful.managesave.CacheGetSet
import io.github.alexiscomete.lapinousecond.useful.managesave.Table

val OFFERS = Table("offers")
val offers = CacheCustom(OFFERS) { Offer(it) }

class Offer(id: Long) : CacheGetSet(id, OFFERS) {

}