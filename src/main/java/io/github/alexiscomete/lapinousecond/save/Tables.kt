package io.github.alexiscomete.lapinousecond.save

enum class Tables(val table: Table) {
    PLAYERS(
        Table(
            "players", arrayOf(
                TableRow("id", "INTEGER PRIMARY KEY"),
                TableRow("bal", "INTEGER"),
                TableRow("serv", "INTEGER"),
                TableRow("tuto", "INTEGER"),
                TableRow("items", "TEXT"),
                TableRow("x", "INTEGER"),
                TableRow("y", "INTEGER"),
                TableRow("is_verify", "INTEGER"),
                TableRow("has_account", "INTEGER"),
                TableRow("roles", "TEXT"),
                TableRow("resources", "TEXT"),
                TableRow("current_world", "TEXT")
            )
        )
    ),
    PERMS(
        Table(
            "perms", arrayOf(
                TableRow("id", "INTEGER PRIMARY KEY"),
                TableRow("play", "INTEGER"),
                TableRow("create_server", "INTEGER"),
                TableRow("manage_perms", "INTEGER"),
                TableRow("manage_roles", "INTEGER")
            )
        )
    ),
    SERVERS(
        Table(
            "guilds", arrayOf(
                TableRow("id", "INTEGER PRIMARY KEY"),
                TableRow("places", "TEXT")
            )
        )
    ),
    PLACES(
        Table(
            "places", arrayOf(
                TableRow("id", "INTEGER PRIMARY KEY"),
                TableRow("serv", "INTEGER"),
                TableRow("x", "INTEGER"),
                TableRow("y", "INTEGER"),
                TableRow("traout", "TEXT"),
                TableRow("connections", "TEXT"),
                TableRow("type", "TEXT") // océan, ville, serveur ...
            )
        )
    ),
    BUILDINGS(
        Table(
            "buildings", arrayOf(
                TableRow("id", "INTEGER PRIMARY KEY")
            )
        )
    ),
    COMPANY(
        Table(
            "company", arrayOf(
                TableRow("id", "INTEGER PRIMARY KEY")
            )
        )
    );

    companion object {
        fun testTables() {
            for (tables in values()) {
                tables.table.configTable()
            }
        }
    }
}