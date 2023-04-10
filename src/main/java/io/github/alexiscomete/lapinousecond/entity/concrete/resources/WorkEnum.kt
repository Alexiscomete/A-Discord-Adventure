package io.github.alexiscomete.lapinousecond.entity.concrete.resources

enum class WorkEnum(
    val type: String,
    val answer: String,
    var min: Int,
    var max: Int,
    val resource: Resource?,
    var coef: Int
) {
    WORK1(
        "default",
        "*Vous aidez quelqu'un à trouver le salon 'memes', vous gagnez rc ${Resource.RABBIT_COIN.show} !*",
        15,
        35,
        null,
        2
    ),
    WORK2(
        "default",
        "*Vous aider un bot à se rappeler de son préfix, vous gagnez rc ${Resource.RABBIT_COIN.show} !*",
        16,
        45,
        null,
        2
    ),
    WORK3(
        "default",
        "*Vous trouvez billet de train pour un autre serveur que vous rendez à son propriétaire, il vous récompense avec rc ${Resource.RABBIT_COIN.show} ...*",
        15,
        36,
        null,
        2
    ),
    WOOD1(
        "default",
        "Vous coupez du bois dans une forêt proche. Vous revenez avec rc ${Resource.WOOD.show}",
        100,
        200,
        Resource.WOOD,
        20
    ),
    STONE1(
        "default",
        "Vous allez récupérer rc ${Resource.STONE.show} sur une colline",
        80,
        150,
        Resource.STONE,
        20
    ),
    BRANCH1(
        "default",
        "Vous récupérez rc ${Resource.BRANCH.show} mortes dans la forêt",
        50,
        250,
        Resource.BRANCH,
        15
    ),
    BRANCH2("default", "Vous cassez rc ${Resource.BRANCH.show} sur des arbres.", 150, 250, Resource.BRANCH, 15), DIAMOND1(
        "default",
        "En allant minez vous trouvez rc diamant(s) ...",
        1,
        10,
        Resource.DIAMOND,
        1
    );

}