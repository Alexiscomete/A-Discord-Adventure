package io.github.alexiscomete.lapinousecond.save

enum class Tables(val table: Table) {
    PLAYERS(
        Table(
            "players"
        )
    ),
    PERMS(
        Table(
            "perms"
        )
    ),
    SERVERS(
        Table(
            "guilds"
        )
    ),
    PLACES(
        Table(
            "places"
        )
    ),
    BUILDINGS(
        Table(
            "buildings"
        )
    ),
    COMPANY(
        Table(
            "company"
        )
    );
}