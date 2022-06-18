package io.github.alexiscomete.lapinousecond.entity

import alexiscomete.managesave.CacheCustom
import alexiscomete.managesave.CacheGetSet
import alexiscomete.managesave.Table

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