package io.github.alexiscomete.lapinousecond.entity

import io.github.alexiscomete.lapinousecond.save.CacheGetSet
import io.github.alexiscomete.lapinousecond.save.Tables

class Company(id: Long) : CacheGetSet(id, Tables.COMPANY.table), Owner {

    override val ownerType: String
        get() = "company"
    override val ownerString: String
        get() = id.toString()
}