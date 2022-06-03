package io.github.alexiscomete.lapinousecond.save

import io.github.alexiscomete.lapinousecond.Main

class Table(val name: String, private val rows: Array<TableRow>) {

    fun configTable() {
        val createTable = StringBuilder("\n(\n")
        for (i in rows.indices) {
            val row = rows[i]
            createTable.append(row.name).append(" ").append(row.type)
            if (i != rows.size - 1) {
                createTable.append(",")
            }
            createTable.append("\n")
        }
        createTable.append(")")
        Main.saveManager.execute("CREATE TABLE IF NOT EXISTS $name$createTable", false)
        for (tableRow in rows) {
            Main.saveManager.execute("ALTER TABLE $name ADD COLUMN ${tableRow.name} ${tableRow.type}", false)
        }
    }
}