package io.github.alexiscomete.lapinousecond.entity

import io.github.alexiscomete.lapinousecond.save.CacheGetSet
import io.github.alexiscomete.lapinousecond.save.Tables

class Company(id: Long) : CacheGetSet(id, Tables.COMPANY.table), Owner {
    override fun getOwnerType(): String {
        return "company"
    }

    override fun getOwnerString(): String {
        return id.toString()
    }
}