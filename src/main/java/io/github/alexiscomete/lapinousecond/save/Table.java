package io.github.alexiscomete.lapinousecond.save;

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
}
