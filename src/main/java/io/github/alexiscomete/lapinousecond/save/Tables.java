package io.github.alexiscomete.lapinousecond.save;

public enum Tables {
    PLAYERS(new Table("players", new TableRow[]{new TableRow("id", "ID INTEGER PRIMARY KEY NOT NULL"), new TableRow("bal", "INTEGER"), new TableRow("server", "INTEGER"), new TableRow("tuto", "INTEGER"), new TableRow("items", "TEXT")})),
    PERMS(new Table("perms", new TableRow[]{new TableRow("id", "ID INTEGER PRIMARY KEY NOT NULL"), new TableRow("play", "INTEGER"), new TableRow("create_server", "INTEGER"), new TableRow("manage_perms", "INTEGER")})),
    SERVERS(new Table("guilds", new TableRow[]{new TableRow("id", "ID INTEGER PRIMARY KEY NOT NULL"), new TableRow("x", "INTEGER"), new TableRow("y", "INTEGER"), new TableRow("z", "INTEGER"), new TableRow("descr", "TEXT"), new TableRow("namerp", "TEXT"), new TableRow("train", "TEXT"), new TableRow("traout", "TEXT"), new TableRow("travel", "TEXT")}));

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
