package io.github.alexiscomete.lapinousecond.save;

public enum Tables {
    PLAYERS(new Table("PLAYERS", new TableRow[]{})),
    PERMS(new Table("PERMS", new TableRow[]{})),
    SERVERS(new Table("SERVERS", new TableRow[]{}));

    private final Table table;

    public Table getTable() {
        return table;
    }

    Tables(Table table) {
        this.table = table;
    }

    public static void testTables() {
        for (Tables tables : values()) {
            tables.table.configTable();
        }
    }
}
