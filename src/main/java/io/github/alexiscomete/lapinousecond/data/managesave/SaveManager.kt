package io.github.alexiscomete.lapinousecond.data.managesave

import io.github.alexiscomete.lapinousecond.entity.concrete.items.ITEMS
import java.sql.*

var save: SaveManager? = null
var saveManager: SaveManager
    get() = if (save == null) {
        throw IllegalStateException("SaveManager is not initialized")
    } else {
        save!!
    }
    set(value) {
        save = value
    }

fun toBoolean(s: Int): Boolean {
    return s == 1
}

fun fromBooleanToString(b: Boolean): String {
    return if (b) "1" else "0"
}

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
            // init a prepared statement
            val sql = "SELECT type FROM $tableName WHERE id = ?"
            val pQ = co!!.prepareStatement(sql)
            pQ.setLong(1, id)
            val resultSet = pQ.executeQuery()
            if (resultSet.next()) {
                return resultSet.getString("type")
            }
            resultSet.close()
        } catch (throwable: SQLException) {
            throwable.printStackTrace()
        }
        return ""
    }

    /**
     * It returns an array list of 6 random elements from the table specified in the parameter
     *
     * @param from Table - The table to get the random elements from
     */
    fun randomElements(from: Table): ArrayList<Long> =
        try {
            val resultSet = st!!.executeQuery("SELECT id FROM ${from.name} ORDER BY RAND() LIMIT 6")
            val longs = ArrayList<Long>()
            while (resultSet.next()) {
                longs.add(resultSet.getString("id").toLong())
            }
            resultSet.close()
            longs
        } catch (throwable: SQLException) {
            throwable.printStackTrace()
            ArrayList()
        }

    /**
     * It sets a value in a table
     *
     * @param where The table you want to update
     * @param which The column name of the row you want to change.
     * @param whichValue The value of the column which you want to change.
     * @param valueName The name of the value you want to change.
     * @param value The value to be set.
     */
    fun setValue(where: String, which: String, whichValue: String, valueName: String, value: String) {
        val va = value.replace("\\", " ").replace("'", " ").replace("\"", " ").replace("--", " ")
        try {
            val sql = "UPDATE $where SET $valueName = ? WHERE $which = ?"
            val pQ = co!!.prepareStatement(sql)
            pQ.setString(1, va)
            pQ.setString(2, whichValue)
            pQ.executeUpdate()
        } catch (throwables: SQLException) {
            throwables.printStackTrace()
        }
    }

    /**
     * This function sets the value of a row in a table, given the table name, the id of the row, the name of the row, and
     * the value to set.
     *
     * @param table The table to update
     * @param id The id of the row you want to change
     * @param row The row you want to set the value of
     * @param value The value to set the row to.
     */
    fun setValue(table: Table, id: Long, row: String, value: String) {
        setValue(table.name, "id", id.toString(), row, value)
    }

    /**
     * If the column doesn't exist, create it, then set the value.
     *
     * @param table The table you want to set the value in.
     * @param id The id of the row you want to set the value of.
     * @param row The name of the row you want to set the value of.
     * @param value The value you want to set
     * @param type The type of the column.
     */
    fun setValue(table: Table, id: Long, row: String, value: String, type: String) {
        execute("ALTER TABLE " + table.name + " ADD COLUMN " + row + " " + type, false)
        setValue(table, id, row, value)
    }

    fun addColumn(table: Table, row: String) {
        execute("ALTER TABLE " + table.name + " ADD COLUMN " + row + " TEXT", false)
    }

    /**
     * It executes a query and returns true if the query has a result
     *
     * @param query The query to be executed.
     * @return A boolean value
     */
    fun hasResult(query: String): Boolean {
        try {
            val resultSet = st!!.executeQuery(query)
            val next = resultSet.next()
            resultSet.close()
            return next
        } catch (throwables: SQLException) {
            throwables.printStackTrace()
        }
        return false
    }

    /**
     * It takes a table name and a hashmap of column names and values, and inserts the values into the table
     *
     * @param where The table you want to insert into
     * @param what HashMap<String, String>
     */
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
    /**
     * It executes a SQL statement
     *
     * @param ex The SQL statement to execute
     * @param bo Boolean, if true, it will print the stack trace if an error occurs.
     */
    fun execute(ex: String, bo: Boolean = true) {
        try {
            st!!.executeUpdate(ex)
        } catch (e: SQLException) {
            if (bo) {
                e.printStackTrace()
            }
        }
    }

    /**
     * It executes a query and returns the result set.
     *
     * @param ex The query you want to execute
     * @param bo Boolean - If true, it will print the stack trace of the exception.
     * @return A ResultSet object
     */
    fun executeQuery(ex: String, bo: Boolean): ResultSet? {
        return try {
            st!!.executeQuery(ex)
        } catch (e: SQLException) {
            if (bo) {
                e.printStackTrace()
            }
            null
        }
    }

    fun preparedStatement(query: String): PreparedStatement {
        return co!!.prepareStatement(query)
    }

    /**
     * Thanks to https://stackoverflow.com/questions/9696572/queries-returning-multiple-result-sets
     */
    fun executeMultipleQueryKey(
        statement: PreparedStatement,
        bo: Boolean = false,
        key: String = "id"
    ): ArrayList<Long> {
        val ids = ArrayList<Long>()
        try {
            val isResultSet = statement.execute()

            var inWhile = true
            while (inWhile) {
                if (isResultSet) {
                    val resultSet = statement.resultSet
                    while (resultSet.next()) {
                        ids.add(resultSet.getLong(key))
                    }
                } else {
                    if (statement.updateCount == -1) {
                        break
                    }
                }
                inWhile = statement.moreResults
            }
        } catch (e: SQLException) {
            if (bo) {
                e.printStackTrace()
            }
        }
        return ids
    }

    fun executeMultipleQuery(
        statement: PreparedStatement,
        bo: Boolean = false
    ) : ArrayList<ArrayList<String>> {
        val results = ArrayList<ArrayList<String>>()

        try {
            val isResultSet = statement.execute()

            var inWhile = true
            while (inWhile) {
                if (isResultSet) {
                    val resultSet = statement.resultSet
                    val columnNames = ArrayList<String>()
                    for (i in 1..resultSet.metaData.columnCount) {
                        columnNames.add(resultSet.metaData.getColumnName(i))
                    }
                    results.add(columnNames)
                    while (resultSet.next()) {
                        val row = ArrayList<String>()
                        for (i in 1..resultSet.metaData.columnCount) {
                            row.add(resultSet.getString(i))
                        }
                        results.add(row)
                    }
                } else {
                    if (statement.updateCount == -1) {
                        break
                    }
                }
                inWhile = statement.moreResults
            }
        } catch (e: SQLException) {
            if (bo) {
                e.printStackTrace()
            }
        }

        return results
    }

    /**
     * It adds a column to a table, then returns the value of that column for a specific row
     *
     * @param table The table you want to get the string from.
     * @param row The name of the row you want to get the value of.
     * @param type The type of the column.
     * @param id The id of the row you want to get the value from.
     * @param log If true, it will print the stack trace if an error occurs.
     * @return A string
     */
    fun getString(table: Table, row: String, type: String, id: Long, log: Boolean = false): String {
        execute("ALTER TABLE " + table.name + " ADD COLUMN " + row + " " + type, false)
        val resultSet: ResultSet
        var str: String? = ""
        try {
            resultSet = st!!.executeQuery("SELECT " + row + " FROM " + table.name + " WHERE id=" + id)
            str = resultSet.getString(row)
            resultSet.close()
        } catch (e: SQLException) {
            if (log) {
                e.printStackTrace()
            }
        }
        if (str == null) {
            str = ""
        }
        return str
    }

    /**
     * Delete an element from a table
     *
     * @param table The table you want to delete from
     * @param id The id of the row you want to delete
     * @param log If true, it will print the stack trace if an error occurs.
     */
    fun delete(table: Table, id: Long, log: Boolean = false) {
        execute("DELETE FROM " + table.name + " WHERE id=" + id, log)
    }

    fun addCriticalColumns() {
        addColumn(ITEMS, "containsItemsType")
        addColumn(ITEMS, "containsItemsId")
    }
}
