package io.github.alexiscomete.lapinousecond.worlds.map

enum class FilesMapEnum(val nameOfMap: String, val urlOfMap: String, val author: String, val description: String) {
    BASE_DIBIMAP("base_dibimap", "", "Darki", "La première de toutes les cartes utilisée ... la base du jeu"),
    PIXEL_DIBIMAP("pixel_dibimap", "", "Alexiscomete", "La carte utilisée actuellement"),
    HELP_DIBIMAP("help_dibimap", "", "Alexiscomete", "La carte d'aide : quadrillage fourni"),
    BASE_TUTO("base_tuto", "", "Alexiscomete", "Carte du tuto avant le traitement ! Oui elle ressemble à rien"),
    PIXEL_TUTO("pixel_tuto", "", "Alexiscomete", "Carte du tuto utilisée dans le jeu"),
    HELP_TUTO("help_tuto", "", "Alexiscomete", "Carte d'aide du tuto avec quadrillage fourni"),
    BASE_NORMAL("base_normal", "", "Alexiscomete", "J'ai honte de cette base avant traitement ... un jour elle changera"),
    PIXEL_NORMAL("pixel_normal", "", "Alexiscomete", "Carte utilisée dans le jeu"),
    HELP_NORMAL("help_normal", "", "Alexiscomete", "Carte d'aide avec quadrillage fourni"),
    DIBISTAN("dibistan", "", "Darki", "Carte \"officielle\" du Dibistan"),
    BIOMES_DIBIMAP("biomes_dibimap", "", "Darki", "Carte des biomes du Dibistan"),
    ;
}