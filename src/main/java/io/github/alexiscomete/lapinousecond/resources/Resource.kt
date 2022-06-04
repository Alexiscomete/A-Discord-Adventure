package io.github.alexiscomete.lapinousecond.resources

enum class Resource(val name_: String, val description: String, val progName: String, var price: Double) {
    WOOD("Bois", "C'est juste du bois, utile pendant tout le jeu", "WOOD", 0.25), STONE(
        "Pierre",
        "Très utile",
        "STONE",
        0.5
    ),
    BRANCH("Branche", "Souvent utile pour la fabrication d'objets", "BRANCH", 0.25), DIAMOND(
        "Diamant",
        "Rare",
        "DIAMOND",
        5.0
    );

}