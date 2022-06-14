package io.github.alexiscomete.lapinousecond.save

import io.github.alexiscomete.lapinousecond.saveManager
import java.util.*

open class CacheGetSet(open val id: Long, private val table: Table) {
    private val cache = HashMap<String, String>()
    open fun getString(row: String): String {
        return if (cache.containsKey(row)) {
            cache[row]!!
        } else {
            val str = saveManager.getString(table, row, "TEXT", id)
            cache[row] = str
            str
        }
    }

    operator fun set(row: String, value: String) {
        cache[row] = value
        saveManager.setValue(table, id, row, value, "TEXT")
    }

    fun getArray(row: String): Array<String> {
        return getString(row).split(";".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        val that = other as CacheGetSet
        return id == that.id
    }

    override fun hashCode(): Int {
        return Objects.hash(id)
    }

    operator fun get(s: String): String {
        return getString(s)
    }
}
