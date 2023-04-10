package io.github.alexiscomete.lapinousecond.entity.concrete.resources

enum class Resource(val show: String, val description: String, var price: Double) {
    WOOD(
        "<:wood:998613516602855475>",
        "C'est juste du bois, utile pendant tout le jeu pour de nombreux objectifs",
        0.35
    ),
    STONE(
        "<:stone:999261397793902633>",
        "Très utile pour construire des objets solides qui durent dans le temps",
        0.4
    ),
    BRANCH(
        "<:branch:999261388943917056>",
        "Souvent utile pour la fabrication d'objets, en effet cette ressource est souple et peu couteuse",
        0.25
    ),
    DIAMOND(
        "<:diamond:999261392043528202>",
        "Rare, permet de montrer sa richesse mais aussi d'avoir des objets puissants et complexes",
        30.0
    ),
    IRON(
        "<:iron:999261395302498344>",
        "Base de nombreux objets du jeu comme les armes, renforce l'attaque",
        1.1
    ),
    GOLD(
        "<:gold:999261393624764468>",
        "Utile pour les armes : augmente la magie de l'arme et renforce sa défense",
        7.0
    ),
    COAL(
        "<:coal:999261390516781178>",
        "Utile pour une utilisation industrielle ou magique",
        0.8
    ),
    RABBIT_COIN(
        "<:rabbit_coin:999261396518830080>",
        "L'argent du jeu, je vous déconseille d'en acheter dans le shop car il y a des impôts",
        1.0
    );

}