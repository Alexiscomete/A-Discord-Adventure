package io.github.alexiscomete.lapinousecond.save;

public enum Tables {
    PLAYERS(new Table("PLAYERS", new TableRow[]{new TableRow("id", "ID INTEGER PRIMARY KEY NOT NULL"), new TableRow("BAL", "INTEGER"), new TableRow("SERVER", "INTEGER"), new TableRow("TUTO", "INTEGER"), new TableRow("ITEMS", "TEXT")})),
    PERMS(new Table("PERMS", new TableRow[]{new TableRow("id", "ID BIGINT PRIMARY KEY NOT NULL"), new TableRow("PLAY", "INTEGER"), new TableRow("CREATE_SERVER", "INTEGER"), new TableRow("MANAGE_PERMS", "INTEGER")})),
    SERVERS(new Table("SERVERS", new TableRow[]{new TableRow("id", "ID BIGINT PRIMARY KEY NOT NULL"), new TableRow("X", "INTEGER"), new TableRow("Y", "INTEGER"), new TableRow("Z", "INTEGER"), new TableRow("DESCRIPTION", "TEXT"), new TableRow("NAME", "TEXT"), new TableRow("IN", "TEXT"), new TableRow("OUT", "TEXT"), new TableRow("TRAVEL", "TEXT")}));

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
