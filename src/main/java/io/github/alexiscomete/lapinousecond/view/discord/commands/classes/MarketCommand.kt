package io.github.alexiscomete.lapinousecond.view.discord.commands.classes

import io.github.alexiscomete.lapinousecond.data.managesave.generateUniqueID
import io.github.alexiscomete.lapinousecond.data.managesave.saveManager
import io.github.alexiscomete.lapinousecond.data.transactions.*
import io.github.alexiscomete.lapinousecond.entity.concrete.resources.Resource
import io.github.alexiscomete.lapinousecond.entity.entities.Owner
import io.github.alexiscomete.lapinousecond.entity.entities.Player
import io.github.alexiscomete.lapinousecond.entity.entities.players
import io.github.alexiscomete.lapinousecond.messagesManager
import io.github.alexiscomete.lapinousecond.view.Context
import io.github.alexiscomete.lapinousecond.view.contextFor
import io.github.alexiscomete.lapinousecond.view.contextmanager.ModalContextManager
import io.github.alexiscomete.lapinousecond.view.discord.commands.Command
import io.github.alexiscomete.lapinousecond.view.discord.commands.ExecutableWithArguments
import io.github.alexiscomete.lapinousecond.view.discord.commands.getAccount
import io.github.alexiscomete.lapinousecond.view.ui.longuis.EmbedPagesWithInteractions
import io.github.alexiscomete.lapinousecond.view.ui.longuis.MenuBuilderUI
import io.github.alexiscomete.lapinousecond.view.ui.old.MenuBuilder
import io.github.alexiscomete.lapinousecond.view.ui.playerui.*
import org.javacord.api.entity.message.component.ActionRow
import org.javacord.api.entity.message.component.TextInput
import org.javacord.api.entity.message.component.TextInputStyle
import org.javacord.api.event.interaction.ButtonClickEvent
import org.javacord.api.event.interaction.ModalSubmitEvent
import org.javacord.api.interaction.Interaction
import org.javacord.api.interaction.SlashCommandInteraction
import java.awt.Color

fun giveFromTo(from: Owner, to: Owner, amount: Double, resource: Resource): Boolean {
    return if (from.hasResource(resource, amount)) {
        from.removeResource(resource, amount)
        to.addResource(resource, amount)
        true
    } else {
        false
    }
}

class MarketCommand : Command(
    "market",
    "Permet de faire des transactions entre les joueurs",
    inDms = false
), ExecutableWithArguments {

    override val fullName: String
        get() = "market"
    override val botPerms: Array<String>
        get() = arrayOf("PLAY")

    @Deprecated("Use the new version")
    class M1(name: String) : ModalContextManager(name) {
        override fun ex(smce: ModalSubmitEvent, c: Context) {

            // get optionals text inputs from modal interaction
            val modalInteraction = smce.modalInteraction
            val opResource = modalInteraction.getTextInputValueByCustomId("cresourceid")
            val opQuantity = modalInteraction.getTextInputValueByCustomId("cquantityid")

            // check if all text inputs are present
            if (!opResource.isPresent || !opQuantity.isPresent) {
                throw IllegalArgumentException("Missing text inputs")
            }

            // get text inputs
            val ressource = opResource.get()
            val quantity = opQuantity.get()

            // On vérifie que la ressource est bien une ressource valide
            val resource = Resource.valueOf(ressource.uppercase())

            // On vérifie que la quantité est bien un nombre
            val quantityDouble = quantity.toDouble()

            // On vérifie que la quantité est bien positive
            if (quantityDouble <= 0) {
                throw IllegalArgumentException("La quantité doit être positive")
            }

            modalInteraction.createImmediateResponder()
                .setContent("Continuons. Mentionnez le nom du joueur à qui vous souhaitez donner")
                .respond()

            messagesManager.addListener(
                smce.modalInteraction.channel.get(),
                smce.modalInteraction.user.id
            ) {
                val owner = it.messageContent
                // l'owner est au format <@id>, je vais donc extraire l'id
                val ownerId = owner.substring(2, owner.length - 1)
                val player =
                    players[ownerId.toLong()] ?: throw IllegalArgumentException("Le joueur n'existe pas")

                // on fait la transaction sécurisée avec GiveFromTo
                if (!giveFromTo(
                        c.players.player.player, player, quantityDouble,
                        resource
                    )
                ) {
                    throw IllegalArgumentException("Vous n'avez pas assez de ressources pour effectuer cette transaction")
                } else {
                    modalInteraction.createImmediateResponder()
                        .setContent("Vous avez bien donné $quantity $resource à $owner")
                        .respond()
                }
            }
        }
    }

    class Wait23 {

        private var accept = false
        private var cancel = false

        var end1 = false
        var end2 = false

        var wait: ModalSubmitEvent? = null
        var resource: Resource? = null
        var quantity: Double? = null

        fun sendMenu(
            resource: Resource,
            quantity: Double,
            r2: Resource,
            quantity2: Double,
            p1: Player,
            p2: Player,
            pui: PlayerUI
        ) {
            // accept, cancel are used to count the number of users who select each button
            pui.setLongCustomUI(
                MenuBuilderUI(
                    "Suite de l'échange",
                    "La personne avec qui vous échangez a proposé $quantity ${resource.show}",
                    pui
                )
                    .addButton(
                        "Accepter",
                        "Vous acceptez l'échange."
                    ) { _ ->
                        if (accept) {
                            pui.addMessage(
                                Message(
                                    "Echange accepté. Transaction en cours..."
                                )
                            )
                            if (!p1.hasResource(resource, quantity) || !p2.hasResource(r2, quantity2)) {
                                throw IllegalArgumentException("Une des personnes n'a pas assez de ressources")
                            }
                            p1.removeResource(resource, quantity)
                            p2.removeResource(r2, quantity2)
                            p1.addResource(r2, quantity2)
                            p2.addResource(resource, quantity)

                            pui.addMessage(Message("Transaction effectuée"))
                        } else if (cancel) {
                            pui.addMessage(Message("L'échange est annulé"))
                        } else {
                            accept = true
                            pui.addMessage(
                                Message(
                                    "Vous avez accepté l'échange. Merci de patienter."
                                )
                            )
                        }
                        null
                    }
                    .addButton("Annuler", "Vous refusez l'échange") { _ ->
                        pui.addMessage(
                            Message("L'échange est annulé")
                        )
                        cancel = true
                        null
                    }
            )
        }

        /**
         * It takes a modalSubmitEvent, a resource and a quantity, and then sets the wait, resource and
         * quantity variables to the values of the parameters. And say to the user to wait
         *
         * @param modalSubmitEvent The event that triggered the interaction.
         * @param resourceWait The resource that the user is waiting for
         * @param quantityWait The quantity of the resource you want to wait for.
         */
        fun pleaseWait(
            modalSubmitEvent: ModalSubmitEvent,
            resourceWait: Resource,
            quantityWait: Double
        ) {
            wait = modalSubmitEvent
            resource = resourceWait
            quantity = quantityWait
            modalSubmitEvent.interaction.createImmediateResponder()
                .setContent("Merci de patienter")
                .respond()
        }
    }

    class M2(name: String, private val wait: Wait23, private val otherC: Context) : ModalContextManager(name) {
        override fun ex(smce: ModalSubmitEvent, c: Context) {
            val modalInteraction = smce.modalInteraction
            val opResource = modalInteraction.getTextInputValueByCustomId("cresource1id")
            val opQuantity = modalInteraction.getTextInputValueByCustomId("cquantity1id")

            // check if all text inputs are present
            if (!opResource.isPresent || !opQuantity.isPresent) {
                throw IllegalArgumentException("Missing text inputs")
            }

            // get text inputs
            val ressource = opResource.get()
            val quantity1 = opQuantity.get()

            // On vérifie que la ressource est bien une ressource valide
            val resource1 = Resource.valueOf(ressource.uppercase())

            // On vérifie que la quantité est bien un nombre
            val quantityDouble = quantity1.toDouble()

            // On vérifie que la quantité est bien positive
            if (quantityDouble <= 0) {
                throw IllegalArgumentException("La quantité doit être positive")
            }

            if (wait.end2) {
                val p1 = players[smce.modalInteraction.user.id]!!
                val p2 = players[wait.wait!!.modalInteraction.user.id]!!
                wait.sendMenu(wait.resource!!, wait.quantity!!, resource1, quantityDouble, p2, p1, c)
                wait.sendMenu(resource1, quantityDouble, wait.resource!!, wait.quantity!!, p1, p2, otherC)
            } else {
                wait.pleaseWait(smce, resource1, quantityDouble)
            }
            wait.end1 = true
        }
    }

    class M3(name: String, private val wait: Wait23, private val otherC: Context) : ModalContextManager(name) {
        override fun ex(smce: ModalSubmitEvent, c: Context) {
            val modalInteraction = smce.modalInteraction
            val opResource = modalInteraction.getTextInputValueByCustomId("cresource2id")
            val opQuantity = modalInteraction.getTextInputValueByCustomId("cquantity2id")

            // check if all text inputs are present
            if (!opResource.isPresent || !opQuantity.isPresent) {
                throw IllegalArgumentException("Missing text inputs")
            }

            // get text inputs
            val ressource = opResource.get()
            val quantity1 = opQuantity.get()

            // On vérifie que la ressource est bien une ressource valide
            val resource1 = Resource.valueOf(ressource.uppercase())

            // On vérifie que la quantité est bien un nombre
            val quantityDouble = quantity1.toDouble()

            // On vérifie que la quantité est bien positive
            if (quantityDouble <= 0) {
                throw IllegalArgumentException("La quantité doit être positive")
            }

            if (wait.end1) {
                val p1 = players[smce.modalInteraction.user.id]!!
                val p2 = players[wait.wait!!.modalInteraction.user.id]!!
                wait.sendMenu(wait.resource!!, wait.quantity!!, resource1, quantityDouble, p2, p1, c)
                wait.sendMenu(resource1, quantityDouble, wait.resource!!, wait.quantity!!, p1, p2, otherC)
            } else {
                wait.pleaseWait(smce, resource1, quantityDouble)
            }
            wait.end2 = true
        }
    }

    class M4(name: String) : ModalContextManager(name) {
        override fun ex(smce: ModalSubmitEvent, c: Context) {
            val modalInteraction = smce.modalInteraction
            val opResource = modalInteraction.getTextInputValueByCustomId("citemid")
            val opQuantity = modalInteraction.getTextInputValueByCustomId("cquantityid")
            val opCost = modalInteraction.getTextInputValueByCustomId("ccostid")

            // check if all text inputs are present
            if (!opResource.isPresent || !opQuantity.isPresent || !opCost.isPresent) {
                throw IllegalArgumentException("Missing text inputs")
            }

            // get text inputs
            val ressource = opResource.get()
            val quantity = opQuantity.get()
            val cost = opCost.get()

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
                throw IllegalArgumentException("Le prix doit être positif")
            }

            val p = c.players.player.player
            if (!p.hasResource(resource, quantityDouble)) {
                throw IllegalArgumentException("Vous n'avez pas assez de ${resource.name}")
            }
            p.removeResource(resource, quantityDouble)
            val offerId = generateUniqueID()
            offers.add(offerId)
            val offer = Offer(offerId)
            offer["who"] = p.id.toString()
            offer["what"] = resource.name
            offer["amount"] = quantityDouble.toString()
            offer["amountRB"] = costDouble.toString()

            smce.modalInteraction.createImmediateResponder()
                .setContent("Votre offre a bien été enregistrée :  $costDouble ${Resource.RABBIT_COIN.show} -> $quantityDouble ${resource.name}")
                .respond()
        }
    }

    class M6(name: String) : ModalContextManager(name) {
        override fun ex(smce: ModalSubmitEvent, c: Context) {
            val modalInteraction = smce.modalInteraction
            val opResource = modalInteraction.getTextInputValueByCustomId("citemid")
            val opQuantity = modalInteraction.getTextInputValueByCustomId("cquantityid")
            val opCost = modalInteraction.getTextInputValueByCustomId("ccostid")

            // check if all text inputs are present
            if (!opResource.isPresent || !opQuantity.isPresent || !opCost.isPresent) {
                throw IllegalArgumentException("Missing text inputs")
            }

            // get text inputs
            val ressource = opResource.get()
            val quantity = opQuantity.get()
            val cost = opCost.get()

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
                throw IllegalArgumentException("Le prix doit être positif")
            }

            val p = c.players.player.player
            if (!p.hasMoney(costDouble)) {
                throw IllegalArgumentException("Vous n'avez pas assez de ${Resource.RABBIT_COIN.show}")
            }
            p.removeMoney(costDouble)
            val researchId = generateUniqueID()
            researches.add(researchId)
            val research = Research(researchId)
            research["who"] = p.id.toString()
            research["what"] = resource.name
            research["amount"] = quantityDouble.toString()
            research["amountRB"] = costDouble.toString()

            smce.modalInteraction.createImmediateResponder()
                .setContent("Votre recherche a bien été enregistrée :  $quantityDouble ${resource.name} -> $costDouble ${Resource.RABBIT_COIN.show}")
                .respond()
        }
    }

    class M7(name: String) : ModalContextManager(name) {
        override fun ex(smce: ModalSubmitEvent, c: Context) {
            val modalInteraction = smce.modalInteraction
            val opResource = modalInteraction.getTextInputValueByCustomId("citemid")
            val opQuantity = modalInteraction.getTextInputValueByCustomId("cquantityid")
            val opCost = modalInteraction.getTextInputValueByCustomId("ccostid")

            // check if all text inputs are present
            if (!opResource.isPresent || !opQuantity.isPresent || !opCost.isPresent) {
                throw IllegalArgumentException("Missing text inputs")
            }

            // get text inputs
            val ressource = opResource.get()
            val quantity = opQuantity.get()
            val cost = opCost.get()

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

            val p = c.players.player.player
            if (!p.hasResource(resource, quantityDouble)) {
                throw IllegalArgumentException("Vous n'avez pas assez de ${resource.name}")
            }
            p.removeResource(resource, quantityDouble)
            val auctionId = generateUniqueID()
            auctions.add(auctionId)
            val auction = Auction(auctionId)
            auction["who"] = p.id.toString()
            auction["what"] = resource.name
            auction["amount"] = quantityDouble.toString()
            auction["amountRB"] = costDouble.toString()
            auction["whoMax"] = p.id.toString()

            smce.modalInteraction.createImmediateResponder()
                .setContent("Votre enchère a bien été enregistrée :  $costDouble ${Resource.RABBIT_COIN.show} -> $quantityDouble ${resource.name}")
                .respond()
        }

    }

    override fun execute(slashCommand: SlashCommandInteraction) {
        AUCTIONS
        OFFERS
        RESEARCHES

        val player = getAccount(slashCommand)
        val context = contextFor(getAccount(slashCommand.user))

        if (player.level.level < 2) {
            slashCommand.createImmediateResponder()
                .setContent("Vous devez être niveau 2 pour accéder au marché. Utilisez la commande `/shop` pour monter commercer et la commande `/work` pour gagner de l'xp.")
                .respond()
            return
        }

        val ui = DiscordPlayerUI(context, slashCommand as Interaction)

        MenuBuilderUI(
            "Le marché",
            "Ici est le lieu d'échanges entre les joueurs ! Avancez sur vos quêtes en trouvant ici des objets introuvables, gagnez de l'argent en vendant des objets ou des ressources .... bref c'est le lieu des joueurs",
            ui
        )
            .addButton(
                "Donner",
                "Donner un objet ou des ressources à un autre joueur"
            ) { _ ->
                val id = generateUniqueID()

                Question(
                    "Répondez aux questions pour donner",
                    QuestionField(
                        "Quelle ressource / objet voulez-vous donner ?",
                        shortAnswer = true,
                        required = true
                    ),
                    QuestionField(
                        "Combien voulez-vous donner ?",
                        shortAnswer = true,
                        required = true
                    )
                ) {
                    M1(id.toString())
                    null
                }
            }
            .addButton(
                "Echanger",
                "Echanger un objet ou des ressources avec un autre joueur de façon sécurisée"
            ) { _ ->

                // TODO - refonte de l'interface et du système d'échange

                ui.addMessage(
                    Message("Le système d'échange est pour le moment en refonte totale. Désolé.")
                )
                null
            }
            .addButton(
                "Offres",
                "Les vendeurs proposent un prix"
            ) { _ ->
                askWhat(
                    "offre",
                    ui,
                    { _ ->
                        val result = saveManager.executeQuery(
                            "SELECT id FROM offers WHERE who = ${ui.getPlayer().id}",
                            true
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
                                    val pairs = arrayListOf<Pair<String, String>>()
                                    for (i in start until start + num) {
                                        val offer = offerArrayList[i]
                                        pairs.add(
                                            Pair(
                                                "Offre ${i - start + 1}",
                                                "${offer.amountRB} -> ${offer.amount} ${offer.what.show}"
                                            )
                                        )
                                    }
                                    return@EmbedPagesWithInteractions pairs
                                },
                                { offer: Offer, playerUI: PlayerUI ->
                                    val player2 = getAccount(slashCommand)
                                    // On vérifie si l'offre existe encore dans la base de données
                                    val resultSet =
                                        saveManager.executeQuery("SELECT id FROM offers WHERE id = ${offer.id}", true)
                                    if (resultSet == null || !resultSet.next()) {
                                        throw IllegalStateException("L'offre n'existe plus")
                                    }
                                    if (offer.who.id != player2.id) {
                                        throw IllegalArgumentException("Cette offre ne vous appartient pas. Vous ne pouvez pas la supprimer et elle ne devrait pas apparaître dans cette liste")
                                    }
                                    // give back resources
                                    player2.addResource(offer.what, offer.amount)
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
                    }, { _ ->
                        val result =
                            saveManager.executeQuery("SELECT id FROM offers", true) ?: throw IllegalStateException(
                                "No offers"
                            )
                        val offers = arrayListOf<Offer>()
                        while (result.next()) {
                            offers.add(Offer(result.getLong("id")))
                        }
                        result.close()
                        ui.setLongCustomUI(
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
                                    return@EmbedPagesWithInteractions pairs
                                },
                                { offer: Offer, playerUI: PlayerUI ->
                                    val player2 = getAccount(slashCommand)
                                    // On vérifie si l'offre existe encore dans la base de données
                                    val resultSet =
                                        saveManager.executeQuery("SELECT id FROM offers WHERE id = ${offer.id}", true)
                                    if (resultSet == null || !resultSet.next()) {
                                        throw IllegalStateException("L'offre n'existe plus")
                                    }
                                    if (offer.who.id == player2.id) {
                                        throw IllegalArgumentException("Vous ne pouvez pas acheter vos propres offres")
                                    }
                                    if (!player2.hasMoney(offer.amountRB)) {
                                        throw IllegalArgumentException("Vous n'avez pas assez d'argent pour acheter cette offre")
                                    }
                                    player2.removeMoney(offer.amountRB)
                                    offer.who.addMoney(offer.amountRB)
                                    player2.addResource(offer.what, offer.amount)
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
                                ui
                            )
                        )
                    }) { _ ->
                    val id = generateUniqueID().toString()

                    Question(
                        "Création d'une offre",
                        QuestionField(
                            "Quelle ressource / objet voulez-vous vendre ?",
                            shortAnswer = true,
                            required = true
                        ),
                        QuestionField(
                            "Combien voulez-vous en vendre ?",
                            shortAnswer = true,
                            required = true
                        ),
                        QuestionField(
                            "Combien voulez-vous de RB en échange ?",
                            shortAnswer = true,
                            required = true
                        )
                    ) {
                        M4(
                            id,
                        )
                        null
                    }
                }
                null
            }
            .addButton(
                "Recherches",
                "Les acheteurs recherchent un objet pour un certain prix"
            ) { _ ->
                askWhat(
                    "recherche",
                    ui,
                    { pui ->
                        val result = saveManager.executeQuery(
                            "SELECT id FROM researches WHERE who = ${pui.getPlayer().id}",
                            true
                        ) ?: throw IllegalArgumentException("No researches")
                        val researches = arrayListOf<Research>()
                        while (result.next()) {
                            researches.add(Research(result.getLong("id")))
                        }
                        result.close()
                        pui.setLongCustomUI(
                            EmbedPagesWithInteractions(
                                researches,
                                { start: Int, num: Int, researchArrayList: ArrayList<Research> ->
                                    val pairs = arrayListOf<Pair<String, String>>()
                                    for (i in start until start + num) {
                                        val research = researchArrayList[i]
                                        pairs.add(
                                            Pair(
                                                "Offre ${i - start + 1}",
                                                "${research.amountRB} -> ${research.amount} ${research.what.show}"
                                            )
                                        )
                                    }
                                    return@EmbedPagesWithInteractions pairs
                                },
                                { offer: Research, playerUI: PlayerUI ->
                                    val player2 = getAccount(slashCommand)
                                    // On vérifie si l'offre existe encore dans la base de données
                                    val resultSet =
                                        saveManager.executeQuery(
                                            "SELECT id FROM researches WHERE id = ${offer.id}",
                                            true
                                        )
                                    if (resultSet == null || !resultSet.next()) {
                                        throw IllegalStateException("La recherche n'existe plus")
                                    }
                                    if (offer.who.id != player2.id) {
                                        throw IllegalArgumentException("Cette recherche ne vous appartient pas. Vous ne pouvez pas la supprimer et elle ne devrait pas apparaître dans cette liste")
                                    }
                                    // give back resources
                                    player2.addMoney(offer.amountRB)
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
                    }, { _ ->
                        val result =
                            saveManager.executeQuery("SELECT id FROM researches", true) ?: throw IllegalStateException(
                                "No researches found"
                            )
                        val researches = arrayListOf<Research>()
                        while (result.next()) {
                            researches.add(Research(result.getLong("id")))
                        }
                        result.close()
                        ui.setLongCustomUI(
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
                                    return@EmbedPagesWithInteractions pairs
                                },
                                { research: Research, playerUI: PlayerUI ->
                                    val player2 = getAccount(slashCommand)
                                    // On vérifie si l'offre existe encore dans la base de données
                                    val resultSet =
                                        saveManager.executeQuery(
                                            "SELECT id FROM researches WHERE id = ${research.id}",
                                            true
                                        )
                                    if (resultSet == null || !resultSet.next()) {
                                        throw IllegalStateException("La recherche n'existe plus")
                                    }
                                    if (research.who.id == player2.id) {
                                        throw IllegalArgumentException("Vous ne pouvez pas répondre à votre propre recherche")
                                    }
                                    if (!player2.hasResource(research.what, research.amount)) {
                                        throw IllegalArgumentException("Vous n'avez pas assez de ${research.what.show} pour répondre à cette recherche")
                                    }
                                    player2.removeResource(research.what, research.amount)
                                    research.who.addResource(research.what, research.amount)
                                    research.who.addMoney(research.amountRB)
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
                                ui
                            )
                        )
                    }) { _ ->
                    val id = generateUniqueID().toString()

                    Question(
                        "Création d'une recherche",
                        QuestionField(
                            "Quelle ressource / objet cherchez vous ?",
                            shortAnswer = true,
                            required = true
                        ),
                        QuestionField(
                            "Combien en cherchez vous ?",
                            shortAnswer = true,
                            required = true
                        ),
                        QuestionField(
                            "Combien voulez-vous donner de RB en échange ?",
                            shortAnswer = true,
                            required = true
                        )
                    ) {
                        M6(id)
                        null
                    }
                }
                null
            }
            .addButton(
                "Enchères",
                "Ici trouvez les objets les plus rares et chers"
            ) { _ ->
                askWhat("enchère", ui, { _ ->
                    val result = saveManager.executeQuery(
                        "SELECT id FROM auctions WHERE who = ${ui.getPlayer().id}",
                        true
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
                                val pairs = arrayListOf<Pair<String, String>>()
                                for (i in start until start + num) {
                                    val auction = auctionArrayList[i]
                                    pairs.add(
                                        Pair(
                                            "Enchère ${i - start + 1}",
                                            "${auction.amountRB} -> ${auction.amount} ${auction.what.show}"
                                        )
                                    )
                                }
                                return@EmbedPagesWithInteractions pairs
                            },
                            { auction: Auction, playerUI: PlayerUI ->
                                val player2 = getAccount(slashCommand)
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
                                player2.addMoney(auction.amountRB)
                                // and the resources
                                auction.whoMax.addResource(auction.what, auction.amount)
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
                    ui.updateOrSend()
                    context.ui(ui)
                }, { _ ->
                    val result =
                        saveManager.executeQuery("SELECT id FROM auctions", true) ?: throw IllegalStateException(
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
                                return@EmbedPagesWithInteractions pairs
                            },
                            { auction: Auction, playerUI: PlayerUI ->
                                val player2 = getAccount(slashCommand)
                                // On vérifie si l'offre existe encore dans la base de données
                                val resultSet =
                                    saveManager.executeQuery("SELECT id FROM auctions WHERE id = ${auction.id}", true)
                                if (resultSet == null || !resultSet.next()) {
                                    throw IllegalStateException("L'enchère n'existe plus")
                                }
                                if (auction.who.id == player2.id || auction.whoMax.id == player2.id) {
                                    throw IllegalArgumentException("Vous ne pouvez pas répondre à votre propre enchère")
                                }
                                if (!player2.hasMoney(auction.amountRB + 100.0)) {
                                    throw IllegalArgumentException("Vous n'avez pas assez d'argent pour répondre à cette enchère (il faut enchérir de 100 ${Resource.RABBIT_COIN.show})")
                                }
                                player2.removeMoney(auction.amountRB + 100.0)
                                auction.whoMax.addMoney(auction.amountRB)
                                auction["whoMax"] = player2.id.toString()
                                auction["amountRB"] = (auction.amountRB + 100.0).toString()
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
                }) { _ ->
                    val id = generateUniqueID().toString()

                    Question(
                        "Création d'une enchère",
                        QuestionField(
                            "Ressource / objet à mettre aux enchères",
                            shortAnswer = true,
                            required = true
                        ),
                        QuestionField(
                            "Combien voulez-vous en mettre ?",
                            shortAnswer = true,
                            required = true
                        ),
                        QuestionField(
                            "Prix de base de l'enchère",
                            shortAnswer = true,
                            required = true
                        )
                    ) {
                        M7(id)
                        null
                    }
                }
                null
            }

    }

    private fun askWhat(
        name: String,
        playerUI: PlayerUI,
        my: (PlayerUI) -> Unit,
        everything: (PlayerUI) -> Unit,
        create: (PlayerUI) -> Unit
    ) {
        playerUI.setLongCustomUI(
            MenuBuilderUI("Que faire ?", "Il existe 3 possibilités pour les ${name}s", playerUI)
                .addButton(
                    "Mes ${name}s",
                    "Voir vos ${name}s, si elles existent. Dans le cas des offres et recherches, vous pouvez les supprimer. Dans le cas des enchères, vous pouvez finir une enchère.",
                ) { _ ->
                    my(playerUI)
                    null
                }
                .addButton(
                    "Toutes les ${name}s",
                    "Voir toutes les ${name}s. Vous pouvez interagir avec celles-ci ici (répondre à une enchère, acheter une offre, etc.)",
                ) { _ ->
                    everything(playerUI)
                    null
                }
                .addButton(
                    "Créer une $name",
                    "Créer une $name avec le menu interactif"
                ) { _ ->
                    create(playerUI)
                    null
                }
        )
    }
}