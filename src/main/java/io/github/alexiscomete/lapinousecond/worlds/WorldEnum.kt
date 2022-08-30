package io.github.alexiscomete.lapinousecond.worlds

import io.github.alexiscomete.lapinousecond.worlds.map.Map

enum class WorldEnum(val world: World) {
    NORMAL(
        World(
            "Serveur normal",
            "Monde du chaos",
            "NORMAL",
            "Ce monde regroupe tous les serveurs discord qui ne sont pas sur le drapeau. (ex : Wiki, projet, etc.)",
            0,
            0,
            "NORMAL.png",
            500,
            500,
            Map.NORMAL
        )
    ),
    DIBIMAP(
        World(
            "Serveur de territoire",
            "Monde du drapeau",
            "DIBIMAP",
            "Le serveur discord a un territoire sur le drapeau du Dibistan ? Alors c'est le monde du drapeau !",
            449,
            75,
            "DIBIMAP.png",
            528,
            272,
            Map.DIBIMAP
        )
    ),
    TUTO(
        World(
            "Serveur du tutoriel",
            "Monde du tutoriel",
            "TUTO",
            "Ce monde est réservé au tutoriel du jeu",
            0,
            0,
            "TUTO.png",
            100,
            50,
            Map.TUTO
        )
    );
}