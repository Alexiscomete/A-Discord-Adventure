package io.github.alexiscomete.lapinousecond.data.managesave

import java.util.*

open class CacheGetSet(open val id: Long, private val table: Table) {
    private var isDeleted: Boolean = false
    private val cache = HashMap<String, String>()

    @Deprecated("Please use the get method instead", replaceWith = ReplaceWith("this[row]"))
    fun getString(row: String): String {
        return this[row]
    }

    operator fun set(row: String, value: String) {
        if (isDeleted) {
            throw IllegalStateException("This object is deleted")
        }
        cache[row] = value
        save?.setValue(table, id, row, value, "TEXT")
    }

    fun getArray(row: String): Array<String> {
        if (isDeleted) {
            throw IllegalStateException("This object is deleted")
        }
        return this[row].split(";".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
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

    operator fun get(row: String): String {
        if (isDeleted) {
            throw IllegalStateException("This object is deleted")
        }
        return if (cache.containsKey(row)) {
            cache[row]!!
        } else {
            val str = saveManager.getString(table, row, "TEXT", id, false)
            cache[row] = str
            str
        }
    }

    fun delete() {
        save?.delete(table, id)
        isDeleted = true
    }
}
