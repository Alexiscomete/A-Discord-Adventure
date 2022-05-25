package io.github.alexiscomete.lapinousecond.save;

import io.github.alexiscomete.lapinousecond.Main;

public class Table {
    private final String name;
    private final TableRow[] rows;

    public Table(String name, TableRow[] rows) {
        this.name = name;
        this.rows = rows;
    }

    public String getName() {
        return name;
    }

    public TableRow[] getRows() {
        return rows;
    }

    public void configTable() {
        StringBuilder createTable = new StringBuilder("\n(\n");
        for (int i = 0; i < rows.length; i++) {
            TableRow row = rows[i];
            createTable.append(row.getName()).append(" ").append(row.getType());
            if (i != rows.length - 1) {
                createTable.append(",");
            }
            createTable.append("\n");
        }
        createTable.append(")");
        Main.getSaveManager().execute("CREATE TABLE IF NOT EXISTS " + name + createTable, false);
        for (TableRow tableRow : rows) {
            Main.getSaveManager().execute("ALTER TABLE " + name + " ADD COLUMN " + tableRow.getName() + " " + tableRow.getType(), false);
        }
    }
}