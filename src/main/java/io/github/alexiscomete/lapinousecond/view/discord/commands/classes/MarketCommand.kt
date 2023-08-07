package io.github.alexiscomete.lapinousecond.view.discord.commands.classes

import io.github.alexiscomete.lapinousecond.data.managesave.generateUniqueID
import io.github.alexiscomete.lapinousecond.data.managesave.saveManager
import io.github.alexiscomete.lapinousecond.data.transactions.*
import io.github.alexiscomete.lapinousecond.entity.concrete.resources.Resource
import io.github.alexiscomete.lapinousecond.view.contextFor
import io.github.alexiscomete.lapinousecond.view.discord.commands.Command
import io.github.alexiscomete.lapinousecond.view.discord.commands.ExecutableWithArguments
import io.github.alexiscomete.lapinousecond.view.discord.commands.getAccount
import io.github.alexiscomete.lapinousecond.view.ui.longuis.EmbedPagesWithInteractions
import io.github.alexiscomete.lapinousecond.view.ui.longuis.MenuBuilderFactoryUI
import io.github.alexiscomete.lapinousecond.view.ui.longuis.MenuBuilderUI
import io.github.alexiscomete.lapinousecond.view.ui.playerui.*
import org.javacord.api.interaction.Interaction
import org.javacord.api.interaction.SlashCommandInteraction

const val LEVEL_FOR_MARKET = 2
const val MIN_AUCTION_ADDING = 100.0

fun askWhat(
    name: String,
    playerUI: PlayerUI,
    my: (PlayerUI) -> Question?,
    everything: (PlayerUI) -> Question?,
    create: (PlayerUI) -> Question?
): Question? {
    playerUI.setLongCustomUI(MenuBuilderUI(
        "Que faire ?",
        "Il existe 3 possibilités pour les ${name}s",
        playerUI
    ).addButton(
        "Mes ${name}s",
        "Voir vos ${name}s, si elles existent. Dans le cas des offres et recherches, vous pouvez les supprimer. Dans le cas des enchères, vous pouvez finir une enchère.",
    ) { _ ->
        my(playerUI)
    }.addButton(
        "Toutes les ${name}s",
        "Voir toutes les ${name}s. Vous pouvez interagir avec celles-ci ici (répondre à une enchère, acheter une offre, etc.)",
    ) { _ ->
        everything(playerUI)
    }.addButton(
        "Créer une $name", "Créer une $name avec le menu interactif"
    ) { _ ->
        create(playerUI)
    })
    return null
}

fun renderTransaction(
    start: Int,
    num: Int,
    transactionsArrayList: ArrayList<out Transaction>,
    name: String
): ArrayList<Pair<String, String>> {
    val pairs = arrayListOf<Pair<String, String>>()
    for (i in start until start + num) {
        val research = transactionsArrayList[i]
        pairs.add(
            Pair(
                "$name ${i - start + 1}",
                "${research.amountRB} -> ${research.amount} ${research.what.show}"
            )
        )
    }
    return pairs
}

val MAIN_MARKET_MENU = MenuBuilderFactoryUI(
    "Le marché",
    "Ici est le lieu d'échanges entre les joueurs ! Avancez sur vos quêtes en trouvant ici des objets introuvables, gagnez de l'argent en vendant des objets ou des ressources .... bref c'est le lieu des joueurs"
).addButton(
    "Donner", "Donner un objet ou des ressources à un autre joueur"
) { ui ->
    Question(
        "Répondez aux questions pour donner", QuestionField(
            "Quelle ressource / objet voulez-vous donner ?", shortAnswer = true, required = true
        ), QuestionField(
            "Combien voulez-vous donner ?", shortAnswer = true, required = true
        )
    ) {
        // get optionals text inputs from modal interaction

        // get text inputs
        val ressource = it.field0.answer
        val quantity = it.field1!!.answer

        // On vérifie que la ressource est bien une ressource valide
        val resource = Resource.valueOf(ressource.uppercase())

        // On vérifie que la quantité est bien un nombre
        val quantityDouble = quantity.toDouble()

        // On vérifie que la quantité est bien positive
        if (quantityDouble <= 0) {
            throw IllegalArgumentException("La quantité doit être positive")
        }

        ui.addMessage(
            Message("RENFONTE EN COURS - ceci ne fonctionne pas encore. Utilisez des offres pour le moment")
        )

        // TODO - Faire un système de mention

        null
    }
}.addButton(
    "Echanger", "Echanger un objet ou des ressources avec un autre joueur de façon sécurisée"
) { ui ->
    ui.addMessage(
        Message("Le système d'échange est pour le moment en refonte totale. Désolé.") // TODO - refonte de l'interface et du système d'échange
    )
    null
}.addButton(
    "Offres", "Les vendeurs proposent un prix"
) { ui ->
    askWhat("offre", ui, ::myOffers, ::everyOffer, ::createOffer)
}.addButton(
    "Recherches", "Les acheteurs recherchent un objet pour un certain prix"
) { ui ->
    askWhat("recherche", ui, ::myResearches, ::everyResearches, ::createResearche)
}.addButton(
    "Enchères", "Ici trouvez les objets les plus rares et chers"
) { ui ->
    askWhat("enchère", ui, ::myAuctions, ::everyAuctions, ::createAuction)
}

private fun createResearche(ui: PlayerUI) = Question(
    "Création d'une recherche", QuestionField(
        "Quelle ressource / objet cherchez vous ?", shortAnswer = true, required = true
    ), QuestionField(
        "Combien en cherchez vous ?", shortAnswer = true, required = true
    ), QuestionField(
        "Combien voulez-vous donner de RB en échange ?", shortAnswer = true, required = true
    )
) {

    // get text inputs

    // On vérifie que la ressource est bien une ressource valide
    val resource = Resource.valueOf(it.field0.answer.uppercase())

    // On vérifie que la quantité est bien un nombre
    val quantityDouble = it.field1!!.answer.toDouble()

    // On vérifie que la quantité est bien positive
    if (quantityDouble <= 0) {
        throw IllegalArgumentException("La quantité doit être positive")
    }

    // On vérifie que le prix est bien un nombre
    val costDouble = it.field2!!.answer.toDouble()

    // On vérifie que le prix est bien positif
    if (costDouble <= 0) {
        throw IllegalArgumentException("Le prix doit être positif")
    }

    val p = ui.getPlayer()
    val pM = ui.getPlayerManager()
    val rManager = pM.ownerManager
    if (!rManager.hasMoney(costDouble)) {
        throw IllegalArgumentException("Vous n'avez pas assez de ${Resource.RABBIT_COIN.show}")
    }
    rManager.removeMoney(costDouble)
    val researchId = generateUniqueID()
    researches.add(researchId)
    val research = Research(researchId)
    research["who"] = p.id.toString()
    research["what"] = resource.name
    research["amount"] = quantityDouble.toString()
    research["amountRB"] = costDouble.toString()

    ui.addMessage(
        Message(
            "Votre recherche a bien été enregistrée :  $quantityDouble ${resource.name} -> $costDouble ${Resource.RABBIT_COIN.show}"
        )
    )
    null
}

private fun createAuction(ui: PlayerUI) = Question(
    "Création d'une enchère", QuestionField(
        "Ressource / objet à mettre aux enchères", shortAnswer = true, required = true
    ), QuestionField(
        "Combien voulez-vous en mettre ?", shortAnswer = true, required = true
    ), QuestionField(
        "Prix de base de l'enchère", shortAnswer = true, required = true
    )
) {

    // get text inputs
    val ressource = it.field0.answer
    val quantity = it.field1!!.answer
    val cost = it.field2!!.answer

    // On vérifie que la ressource est bien une ressource valide
    val resource = Resource.valueOf(ressource.uppercase())

    // On vérifie que la quantité est bien un nombre
    val quantityDouble = quantity.toDouble()

    // On vérifie que la quantité est bien positive
    if (quantityDouble <= 0) {
        throw IllegalArgumentException("La quantité doit être positive")
    }

    // On vérifie que le prix est bien un nombre
    val costDouble = cost.toDouble()

    // On vérifie que le prix est bien positif
    if (costDouble <= 0) {
        throw IllegalArgumentException("Le prix de départ doit être positif")
    }

    val p = ui.getPlayer()
    val rManager = ui.getPlayerManager().ownerManager
    if (!rManager.hasResource(resource, quantityDouble)) {
        throw IllegalArgumentException("Vous n'avez pas assez de ${resource.name}")
    }
    rManager.removeResource(resource, quantityDouble)
    val auctionId = generateUniqueID()
    auctions.add(auctionId)
    val auction = Auction(auctionId)
    auction["who"] = p.id.toString()
    auction["what"] = resource.name
    auction["amount"] = quantityDouble.toString()
    auction["amountRB"] = costDouble.toString()
    auction["whoMax"] = p.id.toString()

    ui.addMessage(
        Message("Votre enchère a bien été enregistrée :  $costDouble ${Resource.RABBIT_COIN.show} -> $quantityDouble ${resource.name}")
    )
    null
}

private fun everyAuctions(ui: PlayerUI): Nothing? {
    val result = saveManager.executeQuery("SELECT id FROM auctions", true) ?: throw IllegalStateException(
        "No auctions found"
    )
    val auctions = arrayListOf<Auction>()
    while (result.next()) {
        auctions.add(Auction(result.getLong("id")))
    }
    result.close()
    ui.setLongCustomUI(
        EmbedPagesWithInteractions(
            auctions,
            { start: Int, num: Int, auctionArrayList: ArrayList<Auction> ->
                val pairs = arrayListOf<Pair<String, String>>()
                for (i in start until start + num) {
                    val research = auctionArrayList[i]
                    pairs.add(
                        Pair(
                            "Enchère ${i - start} par <@${research.who.id}>",
                            "${research.amountRB} de <@${research.whoMax.id}> -> ${research.amount} ${research.what.show} "
                        )
                    )
                }
                pairs
            },
            { auction: Auction, playerUI: PlayerUI ->
                val player2 = playerUI.getPlayer()
                val playerManager2 = playerUI.getPlayerManager()
                val rManager2 = playerManager2.ownerManager
                // On vérifie si l'offre existe encore dans la base de données
                val resultSet = saveManager.executeQuery("SELECT id FROM auctions WHERE id = ${auction.id}", true)
                if (resultSet == null || !resultSet.next()) {
                    throw IllegalStateException("L'enchère n'existe plus")
                }
                if (auction.who.id == player2.id || auction.whoMax.id == player2.id) {
                    throw IllegalArgumentException("Vous ne pouvez pas répondre à votre propre enchère")
                }
                if (!rManager2.hasMoney(auction.amountRB + MIN_AUCTION_ADDING)) {
                    throw IllegalArgumentException("Vous n'avez pas assez d'argent pour répondre à cette enchère (il faut enchérir de 100 ${Resource.RABBIT_COIN.show})")
                }
                rManager2.removeMoney(auction.amountRB + MIN_AUCTION_ADDING)
                auction.whoMax.ownerManager.addMoney(auction.amountRB)
                auction["whoMax"] = player2.id.toString()
                auction["amountRB"] = (auction.amountRB + MIN_AUCTION_ADDING).toString()
                // respond
                playerUI.addMessage(Message("Vous avez répondu à l'enchère de ${auction.amountRB} ${Resource.RABBIT_COIN.show}"))
                return@EmbedPagesWithInteractions null
            },
            null,
            null,
            "Les enchères",
            "Voici les enchères en cours. Les objets rares sont ici !",
            ui
        )
    )
    return null
}

private fun myAuctions(ui: PlayerUI): Nothing? {
    val result = saveManager.executeQuery(
        "SELECT id FROM auctions WHERE who = ${ui.getPlayer().id}", true
    ) ?: throw IllegalArgumentException("No auctions")
    val auctions = arrayListOf<Auction>()
    while (result.next()) {
        auctions.add(Auction(result.getLong("id")))
    }
    result.close()
    ui.setLongCustomUI(
        EmbedPagesWithInteractions(
            auctions,
            { start: Int, num: Int, auctionArrayList: ArrayList<Auction> ->
                renderTransaction(start, num, auctionArrayList, "Enchère")
            },
            { auction: Auction, playerUI: PlayerUI ->
                val player2 = playerUI.getPlayer()
                val rManager2 = playerUI.getPlayerManager().ownerManager
                // On vérifie si l'enchère existe encore dans la base de données
                val resultSet =
                    saveManager.executeQuery("SELECT id FROM auctions WHERE id = ${auction.id}", true)
                if (resultSet == null || !resultSet.next()) {
                    throw IllegalStateException("L'enchère n'existe plus")
                }
                if (auction.who.id != player2.id) {
                    throw IllegalArgumentException("Cette enchère ne vous appartient pas. Vous ne pouvez pas la terminer et elle ne devrait pas apparaître dans cette liste")
                }
                if (auction.who.id == auction.whoMax.id) {
                    throw IllegalArgumentException("Vous êtes le meilleur enchérisseur. Vous ne pouvez pas terminer cette enchère")
                }
                // give the auction money
                rManager2.addMoney(auction.amountRB)
                // and the resources
                auction.whoMax.ownerManager.addResource(auction.what, auction.amount)
                // remove the auction from the database
                saveManager.execute("DELETE FROM auctions WHERE id = ${auction.id}", true)
                // respond
                playerUI.addMessage(Message("Vous avez terminé l'enchère ${auction.id} et récupéré l'argent"))
                return@EmbedPagesWithInteractions null
            },
            null,
            null,
            "Vos enchères",
            "Vous avez ${auctions.size} offres en cours. Vous pouvez **supprimer** une offre en cliquant sur le bouton correspondant et ainsi **récupérer vos ressources**",
            ui
        )
    )
    return null
}

private fun myResearches(
    ui: PlayerUI
): Question? {
    val result = saveManager.executeQuery(
        "SELECT id FROM researches WHERE who = ${ui.getPlayer().id}", true
    ) ?: throw IllegalArgumentException("No researches")
    val researches = arrayListOf<Research>()
    while (result.next()) {
        researches.add(Research(result.getLong("id")))
    }
    result.close()
    ui.setLongCustomUI(
        EmbedPagesWithInteractions(
            researches,
            { start: Int, num: Int, researchArrayList: ArrayList<Research> ->
                renderTransaction(start, num, researchArrayList, "Recherche")
            },
            { offer: Research, playerUI: PlayerUI ->
                val player2 = playerUI.getPlayer()
                val rManager2 = playerUI.getPlayerManager().ownerManager
                // On vérifie si l'offre existe encore dans la base de données
                val resultSet = saveManager.executeQuery(
                    "SELECT id FROM researches WHERE id = ${offer.id}", true
                )
                if (resultSet == null || !resultSet.next()) {
                    throw IllegalStateException("La recherche n'existe plus")
                }
                if (offer.who.id != player2.id) {
                    throw IllegalArgumentException("Cette recherche ne vous appartient pas. Vous ne pouvez pas la supprimer et elle ne devrait pas apparaître dans cette liste")
                }
                // give back resources
                rManager2.addMoney(offer.amountRB)
                // remove the offer from the database
                saveManager.execute("DELETE FROM researches WHERE id = ${offer.id}", true)
                // respond
                playerUI.addMessage(Message("Vous avez supprimé la recherche ${offer.id} et récupéré votre argent"))
                return@EmbedPagesWithInteractions null
            },
            null,
            null,
            "Vos recherches",
            "Vous avez ${researches.size} recherches en cours. Vous pouvez **supprimer** une recherche en cliquant sur le bouton correspondant et ainsi **récupérer votre argent**",
            ui
        )
    )
    return null
}

private fun everyResearches(newUI: PlayerUI): Question? {
    val result = saveManager.executeQuery("SELECT id FROM researches", true) ?: throw IllegalStateException(
        "No researches found"
    )
    val researches = arrayListOf<Research>()
    while (result.next()) {
        researches.add(Research(result.getLong("id")))
    }
    result.close()
    newUI.setLongCustomUI(
        EmbedPagesWithInteractions(
            researches,
            { start: Int, num: Int, researchArrayList: ArrayList<Research> ->
                val pairs = arrayListOf<Pair<String, String>>()
                for (i in start until start + num) {
                    val research = researchArrayList[i]
                    pairs.add(
                        Pair(
                            "Recherche ${i - start} par <@${research.who.id}>",
                            "${research.amount} ${research.what.show} -> ${research.amountRB}"
                        )
                    )
                }
                pairs
            },
            { research: Research, playerUI: PlayerUI ->
                val player2 = playerUI.getPlayer()
                val rManager2 = playerUI.getPlayerManager().ownerManager
                // On vérifie si l'offre existe encore dans la base de données
                val resultSet = saveManager.executeQuery(
                    "SELECT id FROM researches WHERE id = ${research.id}", true
                )
                if (resultSet == null || !resultSet.next()) {
                    throw IllegalStateException("La recherche n'existe plus")
                }
                if (research.who.id == player2.id) {
                    throw IllegalArgumentException("Vous ne pouvez pas répondre à votre propre recherche")
                }
                if (!rManager2.hasResource(research.what, research.amount)) {
                    throw IllegalArgumentException("Vous n'avez pas assez de ${research.what.show} pour répondre à cette recherche")
                }
                rManager2.removeResource(research.what, research.amount)
                research.who.ownerManager.addResource(research.what, research.amount)
                research.who.ownerManager.addMoney(research.amountRB)
                // remove the research from the database
                saveManager.execute("DELETE FROM researches WHERE id = ${research.id}", true)
                // respond
                playerUI.addMessage(Message("Vous avez répondu à la recherche ${research.id}"))
                return@EmbedPagesWithInteractions null
            },
            null,
            null,
            "Les recherches",
            "Voici les recherches en cours",
            newUI
        )
    )
    return null
}

private fun createOffer(ui: PlayerUI) = Question(
    "Création d'une offre", QuestionField(
        "Quelle ressource / objet voulez-vous vendre ?", shortAnswer = true, required = true
    ), QuestionField(
        "Combien voulez-vous en vendre ?", shortAnswer = true, required = true
    ), QuestionField(
        "Combien voulez-vous de RB en échange ?", shortAnswer = true, required = true
    )
) {

    // get text inputs

    // On vérifie que la ressource est bien une ressource valide
    val resource = Resource.valueOf(it.field0.answer.uppercase())

    // On vérifie que la quantité est bien un nombre
    val quantityDouble = it.field1!!.answer.toDouble()

    // On vérifie que la quantité est bien positive
    if (quantityDouble <= 0) {
        throw IllegalArgumentException("La quantité doit être positive")
    }

    // On vérifie que le prix est bien un nombre
    val costDouble = it.field2!!.answer.toDouble()

    // On vérifie que le prix est bien positif
    if (costDouble <= 0) {
        throw IllegalArgumentException("Le prix doit être positif")
    }

    val p = ui.getPlayer()
    val rManager = ui.getPlayerManager().ownerManager
    if (!rManager.hasResource(resource, quantityDouble)) {
        throw IllegalArgumentException("Vous n'avez pas assez de ${resource.name}")
    }
    rManager.removeResource(resource, quantityDouble)
    val offerId = generateUniqueID()
    offers.add(offerId)
    val offer = Offer(offerId)
    offer["who"] = p.id.toString()
    offer["what"] = resource.name
    offer["amount"] = quantityDouble.toString()
    offer["amountRB"] = costDouble.toString()

    ui.addMessage(Message("Votre offre a bien été enregistrée :  $costDouble ${Resource.RABBIT_COIN.show} -> $quantityDouble ${resource.name}"))
    null
}

private fun everyOffer(newUI: PlayerUI): Question? {
    val result = saveManager.executeQuery("SELECT id FROM offers", true) ?: throw IllegalStateException(
        "No offers"
    )
    val offers = arrayListOf<Offer>()
    while (result.next()) {
        offers.add(Offer(result.getLong("id")))
    }
    result.close()
    newUI.setLongCustomUI(
        EmbedPagesWithInteractions(
            offers,
            { start: Int, num: Int, offerArrayList: ArrayList<Offer> ->
                val pairs = arrayListOf<Pair<String, String>>()
                for (i in start until start + num) {
                    val offer = offerArrayList[i]
                    pairs.add(
                        Pair(
                            "Offre ${i - start + 1} de <@${offer.who.id}>",
                            "${offer.amountRB} -> ${offer.amount} ${offer.what.show}"
                        )
                    )
                }
                pairs
            },
            { offer: Offer, playerUI: PlayerUI ->
                val player2 = playerUI.getPlayer()
                val rManager2 = playerUI.getPlayerManager().ownerManager
                // On vérifie si l'offre existe encore dans la base de données
                val resultSet = saveManager.executeQuery("SELECT id FROM offers WHERE id = ${offer.id}", true)
                if (resultSet == null || !resultSet.next()) {
                    throw IllegalStateException("L'offre n'existe plus")
                }
                if (offer.who.id == player2.id) {
                    throw IllegalArgumentException("Vous ne pouvez pas acheter vos propres offres")
                }
                if (!rManager2.hasMoney(offer.amountRB)) {
                    throw IllegalArgumentException("Vous n'avez pas assez d'argent pour acheter cette offre")
                }
                rManager2.removeMoney(offer.amountRB)
                offer.who.ownerManager.addMoney(offer.amountRB)
                rManager2.addResource(offer.what, offer.amount)
                // remove the offer from the database
                saveManager.execute("DELETE FROM offers WHERE id = ${offer.id}", true)
                // respond
                playerUI.addMessage(Message("Vous avez acheté l'offre ${offer.id} de <@${offer.who.id}>"))
                return@EmbedPagesWithInteractions null
            },
            null,
            null,
            "Offres",
            "Voici les offres disponibles. Le bouton correspondant vous permet d'accepter une offre et ainsi **acheter les ressources**",
            newUI
        )
    )
    return null
}

private fun myOffers(ui: PlayerUI): Question? {
    val result = saveManager.executeQuery(
        "SELECT id FROM offers WHERE who = ${ui.getPlayer().id}", true
    ) ?: throw IllegalArgumentException("No offers")
    val offers = arrayListOf<Offer>()
    while (result.next()) {
        offers.add(Offer(result.getLong("id")))
    }
    result.close()
    ui.setLongCustomUI(
        EmbedPagesWithInteractions(
            offers,
            { start: Int, num: Int, offerArrayList: ArrayList<Offer> ->
                renderTransaction(start, num, offerArrayList, "Offre")
            },
            { offer: Offer, playerUI: PlayerUI ->
                val player2 = playerUI.getPlayer()
                val rManager2 = playerUI.getPlayerManager().ownerManager
                // On vérifie si l'offre existe encore dans la base de données
                val resultSet = saveManager.executeQuery("SELECT id FROM offers WHERE id = ${offer.id}", true)
                if (resultSet == null || !resultSet.next()) {
                    throw IllegalStateException("L'offre n'existe plus")
                }
                if (offer.who.id != player2.id) {
                    throw IllegalArgumentException("Cette offre ne vous appartient pas. Vous ne pouvez pas la supprimer et elle ne devrait pas apparaître dans cette liste")
                }
                // give back resources
                rManager2.addResource(offer.what, offer.amount)
                // remove the offer from the database
                saveManager.execute("DELETE FROM offers WHERE id = ${offer.id}", true)
                // respond
                playerUI.addMessage(Message("Vous avez supprimé l'offre ${offer.id} et récupéré vos ressources"))
                return@EmbedPagesWithInteractions null
            },
            null,
            null,
            "Vos offres",
            "Vous avez ${offers.size} offres en cours. Vous pouvez **supprimer** une offre en cliquant sur le bouton correspondant et ainsi **récupérer vos ressources**",
            ui
        )
    )
    return null
}

class MarketCommand : Command(
    "market", "Permet de faire des transactions entre les joueurs", inDms = false
), ExecutableWithArguments {

    override val fullName: String
        get() = "market"
    override val botPerms: Array<String>
        get() = arrayOf("PLAY")

    override fun execute(slashCommand: SlashCommandInteraction) {
        AUCTIONS
        OFFERS
        RESEARCHES

        val player = getAccount(slashCommand)
        val context = contextFor(getAccount(slashCommand.user))

        if (player.level.level < LEVEL_FOR_MARKET) {
            slashCommand.createImmediateResponder()
                .setContent("Vous devez être niveau $LEVEL_FOR_MARKET pour accéder au marché. Utilisez la commande `/shop` pour monter commercer et la commande `/work` pour gagner de l'xp.")
                .respond()
            return
        }

        val ui = DiscordPlayerUI(context, slashCommand as Interaction)

        ui.setLongCustomUI(MAIN_MARKET_MENU.build(ui))
        ui.updateOrSend()
        context.ui(ui)
    }
}