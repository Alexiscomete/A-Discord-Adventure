package io.github.alexiscomete.lapinousecond.save

import java.sql.*

var saveManager: SaveManager? = null

class SaveManager(path: String) {
    private var co: Connection? = null
    private var st: Statement? = null

    init {
        try {
            Class.forName("org.sqlite.JDBC")
            co = DriverManager.getConnection(path)
            st = co!!.createStatement()
            saveManager = this
        } catch (throwable: SQLException) {
            throwable.printStackTrace()
            if (co != null) {
                try {
                    co!!.close()
                } catch (ignore: SQLException) {
                }
            }
        } catch (throwable: ClassNotFoundException) {
            throwable.printStackTrace()
            if (co != null) {
                try {
                    co!!.close()
                } catch (ignore: SQLException) {
                }
            }
        }
    }

    /**
     * A method that return the type of anything without create a new object. Useful if you must return a different type of object when the type in the database is different.
     */
    fun typeOf(id: Long, tableName: String): String {
        try {
            val resultSet = st!!.executeQuery("SELECT type FROM $tableName WHERE id = $id")
            if (resultSet.next()) {
                return resultSet.getString("type")
            }
            resultSet.close()
        } catch (throwables: SQLException) {
            throwables.printStackTrace()
        }
        return ""
    }

    fun randomElements(from: Table): ArrayList<Long> =
        try {
            val resultSet = st!!.executeQuery("SELECT id FROM ${from.name} ORDER BY RAND() LIMIT 6")
            val longs = ArrayList<Long>()
            while (resultSet.next()) {
                longs.add(resultSet.getString("id").toLong())
            }
            resultSet.close()
            longs
        } catch (throwables: SQLException) {
            throwables.printStackTrace()
            ArrayList()
        }

    fun setValue(where: String, which: String, whichValue: String, valueName: String, value: String) {
        try {
            st!!.executeUpdate("UPDATE $where SET $valueName = '$value' WHERE $which = $whichValue")
        } catch (throwables: SQLException) {
            throwables.printStackTrace()
        }
    }

    fun setValue(table: Table, id: Long, row: String, value: String) {
        setValue(table.name, "id", id.toString(), row, value)
    }

    fun setValue(table: Table, id: Long, row: String, value: String, type: String) {
        execute("ALTER TABLE " + table.name + " ADD COLUMN " + row + " " + type, false)
        setValue(table, id, row, value)
    }

    fun insert(where: String, what: HashMap<String, String>) {
        val values = StringBuilder("(")
        val keys = StringBuilder("(")
        for (i in 0 until what.size) {
            val key = what.keys.toTypedArray()[i]
            keys.append(key)
            values.append(what[key])
            if (i != what.size - 1) {
                keys.append(", ")
                values.append(", ")
            }
        }
        values.append(")")
        keys.append(")")
        try {
            st!!.executeUpdate("INSERT INTO $where $keys VALUES $values")
        } catch (e: SQLException) {
            e.printStackTrace()
        }
    }

    @JvmOverloads
    fun execute(ex: String, bo: Boolean = true) {
        try {
            st!!.executeUpdate(ex)
        } catch (e: SQLException) {
            if (bo) {
                e.printStackTrace()
            }
        }
    }

    fun executeQuery(ex: String?, bo: Boolean): ResultSet? {
        return try {
            st!!.executeQuery(ex)
        } catch (e: SQLException) {
            if (bo) {
                e.printStackTrace()
            }
            null
        }
    }

    fun getString(table: Table, row: String, type: String, id: Long): String {
        execute("ALTER TABLE " + table.name + " ADD COLUMN " + row + " " + type, false)
        val resultSet: ResultSet
        var str = ""
        try {
            resultSet = st!!.executeQuery("SELECT " + row + " FROM " + table.name + " WHERE id=" + id)
            str = resultSet.getString(row)
            resultSet.close()
        } catch (e: SQLException) {
            e.printStackTrace()
        }
        return str
    }

    companion object {
        fun toBoolean(s: Int): Boolean {
            return s == 1
        }

        fun toBooleanString(b: Boolean): String {
            return if (b) "1" else "0"
        }
    }
}