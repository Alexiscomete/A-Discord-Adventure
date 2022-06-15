package io.github.alexiscomete.lapinousecond.entity

import io.github.alexiscomete.lapinousecond.save.CacheCustom
import io.github.alexiscomete.lapinousecond.save.CacheGetSet
import io.github.alexiscomete.lapinousecond.save.Table

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
}