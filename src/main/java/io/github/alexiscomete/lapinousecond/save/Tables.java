package io.github.alexiscomete.lapinousecond.save;

public enum Tables {
    PLAYERS(new Table("players", new TableRow[]{
            new TableRow("id", "INTEGER PRIMARY KEY"),
            new TableRow("bal", "INTEGER"),
            new TableRow("serv", "INTEGER"),
            new TableRow("tuto", "INTEGER"),
            new TableRow("items", "TEXT"),
            new TableRow("x", "INTEGER"),
            new TableRow("y", "INTEGER"),
            new TableRow("is_verify", "INTEGER"),
            new TableRow("has_account", "INTEGER"),
            new TableRow("roles", "TEXT"),
            new TableRow("resources", "TEXT"),
            new TableRow("current_world", "TEXT"),
            new TableRow("default_world_place", "INTEGER"),
            new TableRow("flag_world_place", "INTEGER")
    })),
    PERMS(new Table("perms", new TableRow[]{
            new TableRow("id", "INTEGER PRIMARY KEY"),
            new TableRow("play", "INTEGER"),
            new TableRow("create_server", "INTEGER"),
            new TableRow("manage_perms", "INTEGER"),
            new TableRow("manage_roles", "INTEGER")
    })),
    SERVERS(new Table("guilds", new TableRow[]{
            new TableRow("id", "INTEGER PRIMARY KEY"),
            new TableRow("places", "TEXT")
    })),
    PLACES(new Table("places", new TableRow[]{
            new TableRow("id", "INTEGER PRIMARY KEY"),
            new TableRow("serv", "INTEGER"),
            new TableRow("name", "TEXT"),
            new TableRow("x", "INTEGER"),
            new TableRow("y", "INTEGER"),
            new TableRow("world", "TEXT"),
            new TableRow("train", "TEXT"),
            new TableRow("traout", "TEXT"),
            new TableRow("connections", "TEXT"),
            new TableRow("type", "TEXT") // océan, ville, serveur ...
    }));

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
