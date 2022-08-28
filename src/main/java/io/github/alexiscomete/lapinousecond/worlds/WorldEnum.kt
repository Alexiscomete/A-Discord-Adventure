package io.github.alexiscomete.lapinousecond.worlds

enum class WorldEnum(val world: World) {
    NORMAL(
        WorldAbstract(
            "Serveur normal",
            "Monde du chaos",
            "NORMAL",
            "Ce monde regroupe tous les serveurs discord sans territoire et qui ne rentrent dans aucune catégorie"
        )
    ),
    DIBIMAP(
        WorldWithCoos(
            "Serveur de territoire",
            "Monde du drapeau",
            "DIBIMAP",
            "Le serveur est un département (avec villes) ? Une région (avec départements ?) ? Une ville ? Ou tout autre chose avec un territoire ? Alors vous le trouverez ici.",
            0,
            0
        )
    );

}