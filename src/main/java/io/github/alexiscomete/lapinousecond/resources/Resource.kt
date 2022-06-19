package io.github.alexiscomete.lapinousecond.resources

enum class Resource(val name_: String, val description: String, val progName: String, var price: Double) {
    WOOD("Bois", "C'est juste du bois, utile pendant tout le jeu", "WOOD", 0.25),
    STONE(
        "Pierre", "Très utile", "STONE", 0.5
    ),
    BRANCH("Branche", "Souvent utile pour la fabrication d'objets", "BRANCH", 0.25),
    DIAMOND(
        "Diamant", "Rare", "DIAMOND", 5.0
    ),
    IRON("Fer", "Utile pour les armes", "IRON", 0.9),
    GOLD("Or", "Utile pour les armes", "GOLD", 4.0),
    COAL(
        "Charbon",
        "Utile pour les armes",
        "COAL",
        0.8
    ),
    RABBIT_COIN(
        "RB", "L'argent du jeu, je vous déconseille d'en acheter dans le shop car il y a des impôts", "RABBIT_COIN", 1.0
    );

}