package io.github.alexiscomete.lapinousecond.useful.transactions

import io.github.alexiscomete.lapinousecond.useful.managesave.CacheCustom
import io.github.alexiscomete.lapinousecond.useful.managesave.CacheGetSet
import io.github.alexiscomete.lapinousecond.useful.managesave.Table

val RESEARCHES = Table("researches")
val researches = CacheCustom(RESEARCHES) { Research(it) }

class Research(id: Long) : CacheGetSet(id, RESEARCHES) {
}