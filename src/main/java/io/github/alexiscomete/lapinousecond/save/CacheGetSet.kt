package io.github.alexiscomete.lapinousecond.save

import io.github.alexiscomete.lapinousecond.Main
import java.util.*

open class CacheGetSet(val id: Long, private val table: Table) {
    private val cache = HashMap<String, CacheValue>()
    open fun getString(row: String): String? {
        return if (cache.containsKey(row)) {
            cache[row]!!.string
        } else {
            var str = Main.getSaveManager().getString(table, row, "TEXT", id)
            if (str == null) {
                str = ""
            }
            cache[row] = CacheValue(str)
            str
        }
    }

    operator fun set(row: String, value: String?) {
        if (cache.containsKey(row)) {
            cache[row]!!.set(value!!)
        } else {
            cache[row] = CacheValue(value!!)
        }
        Main.getSaveManager().setValue(table, id, row, value, "TEXT")
    }

    fun getArray(row: String): Array<String> {
        var str = getString(row)
        if (str == null) {
            str = ""
        }
        return str.split(";".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
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
}