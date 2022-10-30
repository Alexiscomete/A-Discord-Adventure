package io.github.alexiscomete.lapinousecond.useful.managesave

import java.sql.SQLException

open class CacheCustom<U>(private val table: Table, protected val function: (Long) -> U) {
    val hashMap = HashMap<Long, U>()
    operator fun get(l: Long): U? {
        var u = hashMap[l]
        if (u == null) {
            try {
                val resultSet = saveManager.executeQuery("SELECT * FROM " + table.name + " WHERE id = " + l, true)
                if (resultSet != null) {
                    if (resultSet.next()) {
                        u = function(l)
                        hashMap[l] = u
                    }
                }
                resultSet?.close()
            } catch (throwables: SQLException) {
                throwables.printStackTrace()
            }
        }
        return u
    }

    fun add(id: Long) {
        val hashMap = HashMap<String, String>()
        hashMap["id"] = id.toString()
        saveManager.insert(table.name, hashMap)
    }
}

class CacheCustomWithArgument<U, V, W>(private val table: Table, private val function2: (Long, V) -> U, ) {
    private val hashMap2 = HashMap<Long, U>()

    operator fun get(l: Long, v: V): U? {
        var u = hashMap2[l]
        if (u == null) {
            try {
                val resultSet = saveManager.executeQuery("SELECT * FROM " + table.name + " WHERE id = " + l, true)
                if (resultSet != null) {
                    if (resultSet.next()) {
                        u = function2(l, v)
                        hashMap2[l] = u
                    }
                }
                resultSet?.close()
            } catch (throwables: SQLException) {
                throwables.printStackTrace()
            }
        }
        return u
    }
}