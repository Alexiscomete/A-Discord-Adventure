package io.github.alexiscomete.lapinousecond.worlds.map

enum class FilesMapEnum(val urlOfMap: String, val author: String, val description: String) {
    BASE_DIBIMAP("", "Darki", "La première de toutes les cartes utilisée ... la base du jeu"),
    PIXEL_DIBIMAP("", "Alexiscomete", "La carte utilisée actuellement"),
    HELP_DIBIMAP("", "Alexiscomete", "La carte d'aide : quadrillage fourni"),
    BASE_TUTO("", "Alexiscomete", "Carte du tuto avant le traitement ! Oui elle ressemble à rien"),
    PIXEL_TUTO("", "Alexiscomete", "Carte du tuto utilisée dans le jeu"),
    HELP_TUTO("", "Alexiscomete", "Carte d'aide du tuto avec quadrillage fourni"),
    BASE_NORMAL(
        "",
        "Alexiscomete",
        "J'ai honte de cette base avant traitement ... un jour elle changera"
    ),
    PIXEL_NORMAL("", "Alexiscomete", "Carte utilisée dans le jeu"),
    HELP_NORMAL("", "Alexiscomete", "Carte d'aide avec quadrillage fourni"),
    DIBISTAN("", "Darki", "Carte \"officielle\" du Dibistan"),
    BIOMES_DIBIMAP("", "Darki", "Carte des biomes du Dibistan"),
    ;
}