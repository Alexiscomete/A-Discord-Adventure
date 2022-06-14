package io.github.alexiscomete.lapinousecond.save

import io.github.alexiscomete.lapinousecond.UserPerms
import io.github.alexiscomete.lapinousecond.entity.Company
import io.github.alexiscomete.lapinousecond.worlds.Place
import io.github.alexiscomete.lapinousecond.worlds.buildings.Buildings
import java.sql.*

class SaveManager(path: String) {


    val buildings = CacheCustom(Tables.BUILDINGS.table) { aLong: Long -> Buildings.load(aLong.toString()) }
    val companies = CacheCustom(Tables.COMPANY.table) { id: Long ->
        Company(
            id
        )
    }
    private var co: Connection? = null
    private var st: Statement? = null

    init {
        try {
            Class.forName("org.sqlite.JDBC")
            co = DriverManager.getConnection(path)
            st = co!!.createStatement()
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

    fun getBuildingType(id: Long): String? {
        try {
            val resultSet = st!!.executeQuery("SELECT type FROM " + Tables.BUILDINGS.table.name + " WHERE id = " + id)
            if (resultSet.next()) {
                return resultSet.getString("type")
            }
            resultSet.close()
        } catch (throwables: SQLException) {
            throwables.printStackTrace()
        }
        return null
    }

    val travels: ArrayList<Long>
        get() = try {
            val resultSet = st!!.executeQuery("SELECT id FROM guilds ORDER BY RAND() LIMIT 6")
            val longs = ArrayList<Long>()
            while (resultSet.next()) {
                longs.add(java.lang.Long.valueOf(resultSet.getString("id")))
            }
            resultSet.close()
            longs
        } catch (throwables: SQLException) {
            throwables.printStackTrace()
            ArrayList()
        }

    fun getPlayerPerms(id: Long): UserPerms {
        try {
            val resultSet = st!!.executeQuery("SELECT * FROM perms WHERE id = $id")
            if (resultSet.next()) {
                return UserPerms(
                    toBoolean(resultSet.getInt("play")),
                    toBoolean(resultSet.getInt("create_server")),
                    toBoolean(resultSet.getInt("manage_perms")),
                    toBoolean(resultSet.getInt("manage_roles")),
                    false
                )
            }
        } catch (throwables: SQLException) {
            throwables.printStackTrace()
        }
        return UserPerms(PLAY = true, CREATE_SERVER = false, MANAGE_PERMS = false, MANAGE_ROLES = false, isDefault = true)
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
    fun execute(ex: String?, bo: Boolean = true) {
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