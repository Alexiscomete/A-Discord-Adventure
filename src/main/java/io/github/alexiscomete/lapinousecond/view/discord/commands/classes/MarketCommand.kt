package io.github.alexiscomete.lapinousecond.view.discord.commands.classes

import io.github.alexiscomete.lapinousecond.view.discord.commands.Command
import io.github.alexiscomete.lapinousecond.view.discord.commands.ExecutableWithArguments
import io.github.alexiscomete.lapinousecond.view.discord.commands.getAccount
import io.github.alexiscomete.lapinousecond.entity.Owner
import io.github.alexiscomete.lapinousecond.entity.Player
import io.github.alexiscomete.lapinousecond.entity.players
import io.github.alexiscomete.lapinousecond.view.ui.EmbedPagesWithInteractions
import io.github.alexiscomete.lapinousecond.view.ui.MenuBuilder
import io.github.alexiscomete.lapinousecond.messagesManager
import io.github.alexiscomete.lapinousecond.resources.Resource
import io.github.alexiscomete.lapinousecond.useful.managesave.generateUniqueID
import io.github.alexiscomete.lapinousecond.useful.managesave.saveManager
import io.github.alexiscomete.lapinousecond.useful.transactions.*
import io.github.alexiscomete.lapinousecond.view.Context
import io.github.alexiscomete.lapinousecond.view.contextFor
import io.github.alexiscomete.lapinousecond.view.contextmanager.ButtonsContextManager
import io.github.alexiscomete.lapinousecond.view.contextmanager.ModalContextManager
import org.javacord.api.entity.message.component.ActionRow
import org.javacord.api.entity.message.component.TextInput
import org.javacord.api.entity.message.component.TextInputStyle
import org.javacord.api.entity.message.embed.EmbedBuilder
import org.javacord.api.event.interaction.ButtonClickEvent
import org.javacord.api.event.interaction.ModalSubmitEvent
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
    "market",
    inDms = false
), ExecutableWithArguments {

    override val fullName: String
        get() = "market"
    override val botPerms: Array<String>
        get() = arrayOf("PLAY")

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
            modalSubmitEvent: ModalSubmitEvent,
            resource: Resource,
            quantity: Double,
            r2: Resource,
            quantity2: Double,
            p1: Player,
            p2: Player,
            c3: Context
        ) {
            // accept, cancel are used to count the number of users who select each button
            MenuBuilder(
                "Suite de l'échange",
                "La personne avec qui vous échangez a proposé $quantity ${resource.name_}",
                Color.YELLOW,
                c3
            )
                .addButton(
                    "Accepter",
                    "Vous acceptez l'échange."
                ) { it, _, _ ->
                    if (accept) {
                        it.buttonInteraction.createOriginalMessageUpdater()
                            .removeAllComponents()
                            .removeAllEmbeds()
                            .setContent("Echange accepté. Transaction en cours...")
                            .update()
                        if (!p1.hasResource(resource, quantity) || !p2.hasResource(r2, quantity2)) {
                            throw IllegalArgumentException("Une des personnes n'a pas assez de ressources")
                        }
                        p1.removeResource(resource, quantity)
                        p2.removeResource(r2, quantity2)
                        p1.addResource(r2, quantity2)
                        p2.addResource(resource, quantity)
                        it.buttonInteraction.createOriginalMessageUpdater()
                            .removeAllComponents()
                            .removeAllEmbeds()
                            .setContent("Transaction effectuée")
                            .update()
                    } else if (cancel) {
                        it.buttonInteraction.createOriginalMessageUpdater()
                            .removeAllComponents()
                            .removeAllEmbeds()
                            .setContent("L'échange est annulé")
                            .update()
                    } else {
                        accept = true
                        it.buttonInteraction.createOriginalMessageUpdater()
                            .removeAllComponents()
                            .removeAllEmbeds()
                            .setContent("Vous avez accepté l'échange. Merci de patienter.")
                            .update()
                    }
                }
                .addButton("Annuler", "Vous refusez l'échange") { it, _, _ ->
                    it.buttonInteraction.createOriginalMessageUpdater()
                        .removeAllComponents()
                        .removeAllEmbeds()
                        .setContent("L'échange est annulé")
                        .update()
                    cancel = true
                }
                .responder(modalSubmitEvent.modalInteraction)
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
                wait.sendMenu(smce, wait.resource!!, wait.quantity!!, resource1, quantityDouble, p2, p1, c)
                wait.sendMenu(wait.wait!!, resource1, quantityDouble, wait.resource!!, wait.quantity!!, p1, p2, otherC)
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
                wait.sendMenu(smce, wait.resource!!, wait.quantity!!, resource1, quantityDouble, p2, p1, c)
                wait.sendMenu(wait.wait!!, resource1, quantityDouble, wait.resource!!, wait.quantity!!, p1, p2, otherC)
            } else {
                wait.pleaseWait(smce, resource1, quantityDouble)
            }
            wait.end2 = true
        }
    }

    class M4(name: String): ModalContextManager(name) {
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
                throw IllegalArgumentException("Vous n'avez pas assez de ${resource.progName}")
            }
            p.removeResource(resource, quantityDouble)
            val offerId = generateUniqueID()
            offers.add(offerId)
            val offer = Offer(offerId)
            offer["who"] = p.id.toString()
            offer["what"] = resource.progName
            offer["amount"] = quantityDouble.toString()
            offer["amountRB"] = costDouble.toString()

            smce.modalInteraction.createImmediateResponder()
                .setContent("Votre offre a bien été enregistrée :  $costDouble ${Resource.RABBIT_COIN.name_} -> $quantityDouble ${resource.progName}")
                .respond()
        }
    }

    class M6(name: String): ModalContextManager(name) {
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
                throw IllegalArgumentException("Vous n'avez pas assez de ${Resource.RABBIT_COIN.name_}")
            }
            p.removeMoney(costDouble)
            val researchId = generateUniqueID()
            researches.add(researchId)
            val research = Research(researchId)
            research["who"] = p.id.toString()
            research["what"] = resource.progName
            research["amount"] = quantityDouble.toString()
            research["amountRB"] = costDouble.toString()

            smce.modalInteraction.createImmediateResponder()
                .setContent("Votre recherche a bien été enregistrée :  $quantityDouble ${resource.progName} -> $costDouble ${Resource.RABBIT_COIN.name_}")
                .respond()
        }
    }

    class M7(name: String): ModalContextManager(name) {
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
                throw IllegalArgumentException("Vous n'avez pas assez de ${resource.progName}")
            }
            p.removeResource(resource, quantityDouble)
            val auctionId = generateUniqueID()
            auctions.add(auctionId)
            val auction = Auction(auctionId)
            auction["who"] = p.id.toString()
            auction["what"] = resource.progName
            auction["amount"] = quantityDouble.toString()
            auction["amountRB"] = costDouble.toString()
            auction["whoMax"] = p.id.toString()

            smce.modalInteraction.createImmediateResponder()
                .setContent("Votre enchère a bien été enregistrée :  $costDouble ${Resource.RABBIT_COIN.name_} -> $quantityDouble ${resource.progName}")
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

        MenuBuilder(
            "Le marché",
            "Ici est le lieu d'échanges entre les joueurs ! Avancez sur vos quêtes en trouvant ici des objets introuvables, gagnez de l'argent en vendant des objets ou des ressources .... bref c'est le lieu des joueurs",
            Color.YELLOW,
            context
        )
            .addButton(
                "Donner",
                "Donner un objet ou des ressources à un autre joueur"
            ) { messageComponentCreateEvent: ButtonClickEvent, c1, _ ->
                val id = generateUniqueID()

                messageComponentCreateEvent.buttonInteraction.respondWithModal(
                    id.toString(),
                    "Répondez aux questions pour donner",
                    ActionRow.of(
                        TextInput.create(
                            TextInputStyle.SHORT,
                            "cresourceid",
                            "Quelle ressource / objet voulez-vous donner ?",
                            true
                        )
                    ),
                    ActionRow.of(
                        TextInput.create(
                            TextInputStyle.SHORT,
                            "cquantityid",
                            "Combien voulez-vous donner ?",
                            true
                        )
                    )
                )

                c1.modal(M1(id.toString()))
            }
            .addButton(
                "Echanger",
                "Echanger un objet ou des ressources avec un autre joueur de façon sécurisée"
            ) { messageComponentCreateEvent: ButtonClickEvent, c1, _ ->
                getAccount(slashCommand)
                messagesManager.addListener(
                    slashCommand.channel.get(),
                    slashCommand.user.id
                ) { message ->
                    val owner = message.messageContent
                    // l'owner est au format <@id>, je vais donc extraire l'id
                    val ownerId = owner.substring(2, owner.length - 1)
                    players[ownerId.toLong()] ?: throw IllegalArgumentException("Le joueur n'existe pas")
                    MenuBuilder(
                        "Demande d'échange",
                        "<@${slashCommand.user.id}> vous a proposé un échange. **Négociez avant avec lui**",
                        Color.YELLOW,
                        contextFor(getAccount(message.message.mentionedUsers[0]))
                    )
                        .addButton(
                            "Accepter",
                            "Vous acceptez de négociez avec lui"
                        ) { buttonClickEvent: ButtonClickEvent, c3, _ ->
                            val id1 = generateUniqueID()
                            val id2 = generateUniqueID()

                            buttonClickEvent.buttonInteraction.respondWithModal(
                                id1.toString(), "Répondez aux question pour commencer l'échange",
                                ActionRow.of(
                                    TextInput.create(
                                        TextInputStyle.SHORT,
                                        "cresource1id",
                                        "Quelle ressource / objet voulez-vous échanger ?",
                                        true
                                    )
                                ),
                                ActionRow.of(
                                    TextInput.create(
                                        TextInputStyle.SHORT,
                                        "cquantity1id",
                                        "Combien voulez-vous échanger ?",
                                        true
                                    )
                                )
                            )
                            messageComponentCreateEvent.buttonInteraction.respondWithModal(
                                id2.toString(), "Que donner ?",
                                ActionRow.of(
                                    TextInput.create(
                                        TextInputStyle.SHORT,
                                        "cresource2id",
                                        "Ressource / objet à échanger",
                                        true
                                    )
                                ),
                                ActionRow.of(
                                    TextInput.create(
                                        TextInputStyle.SHORT,
                                        "cquantity2id",
                                        "Combien voulez-vous échanger ?",
                                        true
                                    )
                                )
                            )

                            val wait = Wait23()

                            c1.modal(M2(id1.toString(), wait, c3))
                            c3.modal(M3(id2.toString(), wait, c1))
                        }
                        .addButton(
                            "Refuser",
                            "Vous refusez de négocier avec lui"
                        ) { messageComponentCreateEvent: ButtonClickEvent, _, _ ->
                            messageComponentCreateEvent.buttonInteraction
                                .createOriginalMessageUpdater()
                                .removeAllEmbeds()
                                .removeAllComponents()
                                .setContent("<@$ownerId> a refusé votre échange")
                                .update()
                        }
                        .messageBuilder()
                        .setContent("<@$ownerId>")
                        .send(slashCommand.channel.get())
                }

                messageComponentCreateEvent.buttonInteraction.createOriginalMessageUpdater()
                    .removeAllEmbeds()
                    .removeAllComponents()
                    .setContent("Vous demandez un échange. Mentionnez le joueur avec qui vous souhaitez négocier puis restez dans le salon pour recevoir le modal")
                    .update()
            }
            .addButton(
                "Offres",
                "Les vendeurs proposent un prix"
            ) { messageComponentCreateEvent: ButtonClickEvent, c1, _ ->
                askWhat(
                    "offre",
                    messageComponentCreateEvent,
                    c1,
                    { it, c2, _ ->
                        val result = saveManager.executeQuery(
                            "SELECT id FROM offers WHERE who = ${messageComponentCreateEvent.buttonInteraction.user.id}",
                            true
                        ) ?: throw IllegalArgumentException("No offers")
                        val offers = arrayListOf<Offer>()
                        while (result.next()) {
                            offers.add(Offer(result.getLong("id")))
                        }
                        result.close()
                        val embedBuilder = EmbedBuilder()
                            .setTitle("Vos offres")
                            .setDescription("Vous avez ${offers.size} offres en cours. Vous pouvez **supprimer** une offre en cliquant sur le bouton correspondant et ainsi **récupérer vos ressources**")
                            .setColor(Color.GREEN)
                        val embedPagesWithInteractions = EmbedPagesWithInteractions(
                            embedBuilder,
                            offers,
                            { builder: EmbedBuilder, start: Int, num: Int, offerArrayList: ArrayList<Offer> ->
                                for (i in start until start + num) {
                                    val offer = offerArrayList[i]
                                    builder.addInlineField(
                                        "Offre ${i - start + 1}",
                                        "${offer.amountRB} -> ${offer.amount} ${offer.what.name_}"
                                    )
                                }
                            },
                            c2
                        ) { offer: Offer, buttonClickEvent: ButtonClickEvent, _ ->
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
                            buttonClickEvent.buttonInteraction
                                .createOriginalMessageUpdater()
                                .removeAllEmbeds()
                                .removeAllComponents()
                                .setContent("Vous avez supprimé l'offre ${offer.id} et récupéré vos ressources")
                                .update()
                        }
                        embedPagesWithInteractions.register()
                        it.buttonInteraction
                            .createOriginalMessageUpdater()
                            .removeAllEmbeds()
                            .removeAllComponents()
                            .addEmbed(embedBuilder)
                            .addComponents(
                                embedPagesWithInteractions.components,
                                ActionRow.of(embedPagesWithInteractions.buttons)
                            )
                            .update()
                    }, { it, c2, _ ->
                        val result =
                            saveManager.executeQuery("SELECT id FROM offers", true) ?: throw IllegalStateException(
                                "No offers"
                            )
                        val offers = arrayListOf<Offer>()
                        while (result.next()) {
                            offers.add(Offer(result.getLong("id")))
                        }
                        result.close()
                        val embedBuilder = EmbedBuilder()
                            .setTitle("Offres")
                            .setDescription("Voici les offres disponibles. Le bouton correspondant vous permet d'accepter une offre et ainsi **acheter les ressources**")
                            .setColor(Color.GREEN)
                        val embedPagesWithInteractions = EmbedPagesWithInteractions(
                            embedBuilder,
                            offers,
                            { builder: EmbedBuilder, start: Int, num: Int, offerArrayList: ArrayList<Offer> ->
                                for (i in start until start + num) {
                                    val offer = offerArrayList[i]
                                    builder.addInlineField(
                                        "Offre ${i - start + 1} de <@${offer.who.id}>",
                                        "${offer.amountRB} -> ${offer.amount} ${offer.what.name_}"
                                    )
                                }
                            },
                            c2
                        ) { offer: Offer, buttonClickEvent: ButtonClickEvent, _ ->
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
                            buttonClickEvent.buttonInteraction
                                .createOriginalMessageUpdater()
                                .removeAllEmbeds()
                                .removeAllComponents()
                                .setContent("Vous avez acheté l'offre ${offer.id} de <@${offer.who.id}>")
                                .update()
                        }
                        embedPagesWithInteractions.register()
                        it.buttonInteraction
                            .createOriginalMessageUpdater()
                            .removeAllEmbeds()
                            .removeAllComponents()
                            .addEmbed(embedBuilder)
                            .addComponents(
                                embedPagesWithInteractions.components,
                                ActionRow.of(embedPagesWithInteractions.buttons)
                            )
                            .update()
                    }) { event, c2, _ ->
                    val id = generateUniqueID().toString()

                    event.buttonInteraction.respondWithModal(
                        id, "Création d'une offre",
                        ActionRow.of(
                            TextInput.create(
                                TextInputStyle.SHORT,
                                "citemid",
                                "Quelle ressource / objet voulez-vous vendre ?",
                                true
                            )
                        ),
                        ActionRow.of(
                            TextInput.create(
                                TextInputStyle.SHORT,
                                "cquantityid",
                                "Combien voulez-vous en vendre ?",
                                true
                            )
                        ),
                        ActionRow.of(
                            TextInput.create(
                                TextInputStyle.SHORT,
                                "ccostid",
                                "Combien voulez-vous de RB en échange ?",
                                true
                            )
                        )
                    )

                    c2.modal(
                        M4(
                            id,
                        )
                    )
                }
            }
            .addButton(
                "Recherches",
                "Les acheteurs recherchent un objet pour un certain prix"
            ) { messageComponentCreateEvent: ButtonClickEvent, c1, _ ->
                askWhat(
                    "recherche",
                    messageComponentCreateEvent,
                    c1,
                    { it, c2, _ ->
                        val result = saveManager.executeQuery(
                            "SELECT id FROM researches WHERE who = ${messageComponentCreateEvent.buttonInteraction.user.id}",
                            true
                        ) ?: throw IllegalArgumentException("No researches")
                        val researches = arrayListOf<Research>()
                        while (result.next()) {
                            researches.add(Research(result.getLong("id")))
                        }
                        result.close()
                        val embedBuilder = EmbedBuilder()
                            .setTitle("Vos offres")
                            .setDescription("Vous avez ${researches.size} recherches en cours. Vous pouvez **supprimer** une recherche en cliquant sur le bouton correspondant et ainsi **récupérer votre argent**")
                            .setColor(Color.GREEN)
                        val embedPagesWithInteractions = EmbedPagesWithInteractions(
                            embedBuilder,
                            researches,
                            { builder: EmbedBuilder, start: Int, num: Int, researchArrayList: ArrayList<Research> ->
                                for (i in start until start + num) {
                                    val research = researchArrayList[i]
                                    builder.addInlineField(
                                        "Offre ${i - start + 1}",
                                        "${research.amountRB} -> ${research.amount} ${research.what.name_}"
                                    )
                                }
                            },
                            c2
                        ) { offer: Research, buttonClickEvent: ButtonClickEvent, _ ->
                            val player2 = getAccount(slashCommand)
                            // On vérifie si l'offre existe encore dans la base de données
                            val resultSet =
                                saveManager.executeQuery("SELECT id FROM researches WHERE id = ${offer.id}", true)
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
                            buttonClickEvent.buttonInteraction
                                .createOriginalMessageUpdater()
                                .removeAllEmbeds()
                                .removeAllComponents()
                                .setContent("Vous avez supprimé la recherche ${offer.id} et récupéré votre argent")
                                .update()
                        }
                        embedPagesWithInteractions.register()
                        it.buttonInteraction
                            .createOriginalMessageUpdater()
                            .removeAllEmbeds()
                            .removeAllComponents()
                            .addEmbed(embedBuilder)
                            .addComponents(
                                embedPagesWithInteractions.components,
                                ActionRow.of(embedPagesWithInteractions.buttons)
                            )
                            .update()
                    }, { it, c2, _ ->
                        val result =
                            saveManager.executeQuery("SELECT id FROM researches", true) ?: throw IllegalStateException(
                                "No researches found"
                            )
                        val researches = arrayListOf<Research>()
                        while (result.next()) {
                            researches.add(Research(result.getLong("id")))
                        }
                        result.close()
                        val embedBuilder = EmbedBuilder()
                            .setTitle("Les recherches")
                            .setDescription("Voici les recherches en cours")
                            .setColor(Color.ORANGE)
                        val embedPagesWithInteractions = EmbedPagesWithInteractions(
                            embedBuilder,
                            researches,
                            { builder: EmbedBuilder, start: Int, num: Int, researchArrayList: ArrayList<Research> ->
                                for (i in start until start + num) {
                                    val research = researchArrayList[i]
                                    builder.addInlineField(
                                        "Recherche ${i - start} par <@${research.who.id}>",
                                        "${research.amount} ${research.what.name_} -> ${research.amountRB}"
                                    )
                                }
                            },
                            c2
                        ) { research: Research, buttonClickEvent: ButtonClickEvent, _ ->
                            val player2 = getAccount(slashCommand)
                            // On vérifie si l'offre existe encore dans la base de données
                            val resultSet =
                                saveManager.executeQuery("SELECT id FROM researches WHERE id = ${research.id}", true)
                            if (resultSet == null || !resultSet.next()) {
                                throw IllegalStateException("La recherche n'existe plus")
                            }
                            if (research.who.id == player2.id) {
                                throw IllegalArgumentException("Vous ne pouvez pas répondre à votre propre recherche")
                            }
                            if (!player2.hasResource(research.what, research.amount)) {
                                throw IllegalArgumentException("Vous n'avez pas assez de ${research.what.name_} pour répondre à cette recherche")
                            }
                            player2.removeResource(research.what, research.amount)
                            research.who.addResource(research.what, research.amount)
                            research.who.addMoney(research.amountRB)
                            // remove the research from the database
                            saveManager.execute("DELETE FROM researches WHERE id = ${research.id}", true)
                            // respond
                            buttonClickEvent.buttonInteraction
                                .createOriginalMessageUpdater()
                                .removeAllEmbeds()
                                .removeAllComponents()
                                .setContent("Vous avez répondu à la recherche de <@${research.who.id}>")
                                .update()
                        }
                        embedPagesWithInteractions.register()
                        it.buttonInteraction
                            .createOriginalMessageUpdater()
                            .removeAllEmbeds()
                            .removeAllComponents()
                            .addEmbed(embedBuilder)
                            .addComponents(
                                embedPagesWithInteractions.components,
                                ActionRow.of(embedPagesWithInteractions.buttons)
                            )
                            .update()
                    }) { event, c2, _ ->
                    val id = generateUniqueID().toString()

                    event.buttonInteraction.respondWithModal(
                        id, "Création d'une recherche",
                        ActionRow.of(
                            TextInput.create(
                                TextInputStyle.SHORT,
                                "citemid",
                                "Quelle ressource / objet cherchez vous ?",
                                true
                            )
                        ),
                        ActionRow.of(
                            TextInput.create(
                                TextInputStyle.SHORT,
                                "cquantityid",
                                "Combien en cherchez vous ?",
                                true
                            )
                        ),
                        ActionRow.of(
                            TextInput.create(
                                TextInputStyle.SHORT,
                                "ccostid",
                                "Combien voulez-vous donner de RB en échange ?",
                                true
                            )
                        )
                    )

                    c2.modal(
                        M6(
                            id
                        )
                    )
                }
            }
            .addButton(
                "Enchères",
                "Ici trouvez les objets les plus rares et chers"
            ) { messageComponentCreateEvent: ButtonClickEvent, c1, _ ->
                askWhat("enchère", messageComponentCreateEvent, c1, { it, c2, _ ->
                    val result = saveManager.executeQuery(
                        "SELECT id FROM auctions WHERE who = ${messageComponentCreateEvent.buttonInteraction.user.id}",
                        true
                    ) ?: throw IllegalArgumentException("No auctions")
                    val auctions = arrayListOf<Auction>()
                    while (result.next()) {
                        auctions.add(Auction(result.getLong("id")))
                    }
                    result.close()
                    val embedBuilder = EmbedBuilder()
                        .setTitle("Vos enchères")
                        .setDescription("Vous avez ${auctions.size} offres en cours. Vous pouvez **supprimer** une offre en cliquant sur le bouton correspondant et ainsi **récupérer vos ressources**")
                        .setColor(Color.GREEN)
                    val embedPagesWithInteractions = EmbedPagesWithInteractions(
                        embedBuilder,
                        auctions,
                        { builder: EmbedBuilder, start: Int, num: Int, auctionArrayList: ArrayList<Auction> ->
                            for (i in start until start + num) {
                                val auction = auctionArrayList[i]
                                builder.addInlineField(
                                    "Enchère ${i - start + 1}",
                                    "${auction.amountRB} -> ${auction.amount} ${auction.what.name_}"
                                )
                            }
                        },
                        c2
                    ) { auction: Auction, buttonClickEvent: ButtonClickEvent, _ ->
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
                        buttonClickEvent.buttonInteraction
                            .createOriginalMessageUpdater()
                            .removeAllEmbeds()
                            .removeAllComponents()
                            .setContent("Vous avez terminé l'enchère ${auction.id} et récupéré l'argent")
                            .update()
                    }
                    embedPagesWithInteractions.register()
                    it.buttonInteraction
                        .createOriginalMessageUpdater()
                        .removeAllEmbeds()
                        .removeAllComponents()
                        .addEmbed(embedBuilder)
                        .addComponents(
                            embedPagesWithInteractions.components,
                            ActionRow.of(embedPagesWithInteractions.buttons)
                        )
                        .update()
                }, { it, _, _ ->
                    val result =
                        saveManager.executeQuery("SELECT id FROM auctions", true) ?: throw IllegalStateException(
                            "No auctions found"
                        )
                    val auctions = arrayListOf<Auction>()
                    while (result.next()) {
                        auctions.add(Auction(result.getLong("id")))
                    }
                    result.close()
                    val embedBuilder = EmbedBuilder()
                        .setTitle("Les enchères")
                        .setDescription("Voici les enchères en cours. Les objets rares sont ici !")
                        .setColor(Color.ORANGE)
                    val embedPagesWithInteractions = EmbedPagesWithInteractions(
                        embedBuilder,
                        auctions,
                        { builder: EmbedBuilder, start: Int, num: Int, auctionArrayList: ArrayList<Auction> ->
                            for (i in start until start + num) {
                                val research = auctionArrayList[i]
                                builder.addInlineField(
                                    "Enchère ${i - start} par <@${research.who.id}>",
                                    "${research.amountRB} de <@${research.whoMax.id}> -> ${research.amount} ${research.what.name_} "
                                )
                            }
                        },
                        c1
                    ) { auction: Auction, buttonClickEvent: ButtonClickEvent, _ ->
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
                            throw IllegalArgumentException("Vous n'avez pas assez d'argent pour répondre à cette enchère (il faut enchérir de 100 ${Resource.RABBIT_COIN.name_})")
                        }
                        player2.removeMoney(auction.amountRB + 100.0)
                        auction.whoMax.addMoney(auction.amountRB)
                        auction["whoMax"] = player2.id.toString()
                        auction["amountRB"] = (auction.amountRB + 100.0).toString()
                        // respond
                        buttonClickEvent.buttonInteraction
                            .createOriginalMessageUpdater()
                            .removeAllEmbeds()
                            .removeAllComponents()
                            .setContent("Vous avez répondu à l'enchère de ${auction.amountRB} ${Resource.RABBIT_COIN.name_}")
                            .update()
                    }
                    embedPagesWithInteractions.register()
                    it.buttonInteraction
                        .createOriginalMessageUpdater()
                        .removeAllEmbeds()
                        .removeAllComponents()
                        .addEmbed(embedBuilder)
                        .addComponents(
                            embedPagesWithInteractions.components,
                            ActionRow.of(embedPagesWithInteractions.buttons)
                        )
                        .update()
                }) { event, c2, _ ->
                    val id = generateUniqueID().toString()

                    event.buttonInteraction.respondWithModal(
                        id, "Création d'une enchère",
                        ActionRow.of(
                            TextInput.create(
                                TextInputStyle.SHORT,
                                "citemid",
                                "Ressource / objet à mettre aux enchères",
                                true
                            )
                        ),
                        ActionRow.of(
                            TextInput.create(
                                TextInputStyle.SHORT,
                                "cquantityid",
                                "Combien voulez-vous en mettre ?",
                                true
                            )
                        ),
                        ActionRow.of(
                            TextInput.create(
                                TextInputStyle.SHORT,
                                "ccostid",
                                "Prix de base de l'enchère",
                                true
                            )
                        )
                    )

                    c2.modal(
                        M7(id)
                    )
                }
            }
            .responder(slashCommand)

    }

    private fun askWhat(
        name: String,
        buttonClickEvent: ButtonClickEvent,
        context: Context,
        my: (ButtonClickEvent, Context, ButtonsContextManager) -> Unit,
        everything: (ButtonClickEvent, Context, ButtonsContextManager) -> Unit,
        create: (ButtonClickEvent, Context, ButtonsContextManager) -> Unit
    ) {
        MenuBuilder("Que faire ?", "Il existe 3 possibilités pour les ${name}s", Color.YELLOW, context)
            .addButton(
                "Mes ${name}s",
                "Voir vos ${name}s, si elles existent. Dans le cas des offres et recherches, vous pouvez les supprimer. Dans le cas des enchères, vous pouvez finir une enchère.",
            ) { messageComponentCreateEvent: ButtonClickEvent, c1, b1 ->
                my(messageComponentCreateEvent, c1, b1)
            }
            .addButton(
                "Toutes les ${name}s",
                "Voir toutes les ${name}s. Vous pouvez interagir avec celles-ci ici (répondre à une enchère, acheter une offre, etc.)",
            ) { messageComponentCreateEvent: ButtonClickEvent, c1, b1 ->
                everything(messageComponentCreateEvent, c1, b1)
            }
            .addButton(
                "Créer une $name",
                "Créer une $name avec le menu interactif"
            ) { messageComponentCreateEvent: ButtonClickEvent, c1, b1 ->
                create(messageComponentCreateEvent, c1, b1)
            }
            .modif(buttonClickEvent)
    }
}