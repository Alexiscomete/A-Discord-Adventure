package io.github.alexiscomete.lapinousecond.commands.withslash.classes

import io.github.alexiscomete.lapinousecond.commands.withslash.Command
import io.github.alexiscomete.lapinousecond.commands.withslash.ExecutableWithArguments
import io.github.alexiscomete.lapinousecond.commands.withslash.getAccount
import io.github.alexiscomete.lapinousecond.entity.Owner
import io.github.alexiscomete.lapinousecond.entity.Player
import io.github.alexiscomete.lapinousecond.entity.players
import io.github.alexiscomete.lapinousecond.message_event.EmbedPagesWithInteractions
import io.github.alexiscomete.lapinousecond.message_event.MenuBuilder
import io.github.alexiscomete.lapinousecond.messagesManager
import io.github.alexiscomete.lapinousecond.modalManager
import io.github.alexiscomete.lapinousecond.resources.Resource
import io.github.alexiscomete.lapinousecond.useful.managesave.generateUniqueID
import io.github.alexiscomete.lapinousecond.useful.managesave.saveManager
import io.github.alexiscomete.lapinousecond.useful.transactions.*
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

    override fun execute(slashCommand: SlashCommandInteraction) {
        AUCTIONS
        OFFERS
        RESEARCHES
        MenuBuilder(
            "Le marché",
            "Ici est le lieu d'échanges entre les joueurs ! Avancez sur vos quêtes en trouvant ici des objets introuvables, gagnez de l'argent en vendant des objets ou des ressources .... bref c'est le lieu des joueurs",
            Color.YELLOW
        )
            .addButton(
                "Donner",
                "Donner un objet ou des ressources à un autre joueur"
            ) { messageComponentCreateEvent: ButtonClickEvent ->

                val p = getAccount(slashCommand)
                val id = generateUniqueID()
                val idResource = generateUniqueID()
                val idQuantity = generateUniqueID()

                messageComponentCreateEvent.buttonInteraction.respondWithModal(
                    id.toString(),
                    "Répondez aux questions pour donner",
                    ActionRow.of(
                        TextInput.create(
                            TextInputStyle.SHORT,
                            idResource.toString(),
                            "Quelle ressource / objet voulez-vous donner ?",
                            true
                        )
                    ),
                    ActionRow.of(
                        TextInput.create(
                            TextInputStyle.SHORT,
                            idQuantity.toString(),
                            "Combien voulez-vous donner ?",
                            true
                        )
                    )
                )

                modalManager.add(id) { mcce: ModalSubmitEvent ->

                    // get optionals text inputs from modal interaction
                    val modalInteraction = mcce.modalInteraction
                    val opResource = modalInteraction.getTextInputValueByCustomId(idResource.toString())
                    val opQuantity = modalInteraction.getTextInputValueByCustomId(idQuantity.toString())

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
                        mcce.modalInteraction.channel.get(),
                        mcce.modalInteraction.user.id
                    ) {
                        val owner = it.messageContent
                        // l'owner est au format <@id>, je vais donc extraire l'id
                        val ownerId = owner.substring(2, owner.length - 1)
                        val player =
                            players[ownerId.toLong()] ?: throw IllegalArgumentException("Le joueur n'existe pas")

                        // on fait la transaction sécurisée avec GiveFromTo
                        if (!giveFromTo(
                                p, player, quantityDouble,
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
            .addButton(
                "Echanger",
                "Echanger un objet ou des ressources avec un autre joueur de façon sécurisée"
            ) { messageComponentCreateEvent: ButtonClickEvent ->
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
                        Color.YELLOW
                    )
                        .addButton(
                            "Accepter",
                            "Vous acceptez de négociez avec lui"
                        ) { buttonClickEvent: ButtonClickEvent ->
                            val id1 = generateUniqueID()
                            val idItem1 = generateUniqueID()
                            val idQuantity1 = generateUniqueID()

                            val id2 = generateUniqueID()
                            val idItem2 = generateUniqueID()
                            val idQuantity2 = generateUniqueID()

                            var end1 = false
                            var end2 = false

                            var wait: ModalSubmitEvent? = null
                            var resource: Resource? = null
                            var quantity: Double? = null

                            var accept = false
                            var cancel = false

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

                            fun sendMenu(
                                modalSubmitEvent: ModalSubmitEvent,
                                resource: Resource,
                                quantity: Double,
                                r2: Resource,
                                quantity2: Double,
                                p1: Player,
                                p2: Player
                            ) {
                                // accept, cancel are used to count the number of users who select each button
                                MenuBuilder(
                                    "Suite de l'échange",
                                    "La personne avec qui vous échangez a proposé $quantity ${resource.name_}",
                                    Color.YELLOW
                                )
                                    .addButton(
                                        "Accepter",
                                        "Vous acceptez l'échange."
                                    ) {
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
                                    .addButton("Annuler", "Vous refusez l'échange") {
                                        it.buttonInteraction.createOriginalMessageUpdater()
                                            .removeAllComponents()
                                            .removeAllEmbeds()
                                            .setContent("L'échange est annulé")
                                            .update()
                                        cancel = true
                                    }
                                    .responder(modalSubmitEvent.modalInteraction)
                            }

                            buttonClickEvent.buttonInteraction.respondWithModal(
                                id1.toString(), "Répondez aux question pour commencer l'échange",
                                ActionRow.of(
                                    TextInput.create(
                                        TextInputStyle.SHORT,
                                        idItem1.toString(),
                                        "Quelle ressource / objet voulez-vous échanger ?",
                                        true
                                    )
                                ),
                                ActionRow.of(
                                    TextInput.create(
                                        TextInputStyle.SHORT,
                                        idQuantity1.toString(),
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
                                        idItem2.toString(),
                                        "Ressource / objet à échanger",
                                        true
                                    )
                                ),
                                ActionRow.of(
                                    TextInput.create(
                                        TextInputStyle.SHORT,
                                        idQuantity2.toString(),
                                        "Combien voulez-vous échanger ?",
                                        true
                                    )
                                )
                            )

                            modalManager.add(id1) {

                                val modalInteraction = it.modalInteraction
                                val opResource = modalInteraction.getTextInputValueByCustomId(idItem1.toString())
                                val opQuantity = modalInteraction.getTextInputValueByCustomId(idQuantity1.toString())

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

                                if (end2) {
                                    val p1 = players[it.modalInteraction.user.id]!!
                                    val p2 = players[wait!!.modalInteraction.user.id]!!
                                    sendMenu(it, resource!!, quantity!!, resource1, quantityDouble, p2, p1)
                                    sendMenu(wait!!, resource1, quantityDouble, resource!!, quantity!!, p1, p2)
                                } else {
                                    pleaseWait(it, resource1, quantityDouble)
                                }
                                end1 = true
                            }

                            modalManager.add(id2) {

                                val modalInteraction = it.modalInteraction
                                val opResource = modalInteraction.getTextInputValueByCustomId(idItem1.toString())
                                val opQuantity = modalInteraction.getTextInputValueByCustomId(idQuantity1.toString())

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

                                if (end1) {
                                    val p1 = players[it.modalInteraction.user.id]!!
                                    val p2 = players[wait!!.modalInteraction.user.id]!!
                                    sendMenu(it, resource!!, quantity!!, resource1, quantityDouble, p2, p1)
                                    sendMenu(wait!!, resource1, quantityDouble, resource!!, quantity!!, p1, p2)
                                } else {
                                    pleaseWait(it, resource1, quantityDouble)
                                }
                                end2 = true
                            }
                        }
                        .addButton(
                            "Refuser",
                            "Vous refusez de négocier avec lui"
                        ) { messageComponentCreateEvent: ButtonClickEvent ->
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
            ) { messageComponentCreateEvent: ButtonClickEvent ->
                askWhat(
                    "offre",
                    messageComponentCreateEvent,
                    {
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
                            }
                        ) { offer: Offer, buttonClickEvent: ButtonClickEvent ->
                            val player = getAccount(slashCommand)
                            // On vérifie si l'offre existe encore dans la base de données
                            val resultSet =
                                saveManager.executeQuery("SELECT id FROM offers WHERE id = ${offer.id}", true)
                            if (resultSet == null || !resultSet.next()) {
                                throw IllegalStateException("L'offre n'existe plus")
                            }
                            if (offer.who.id != player.id) {
                                throw IllegalArgumentException("Cette offre ne vous appartient pas. Vous ne pouvez pas la supprimer et elle ne devrait pas apparaître dans cette liste")
                            }
                            // give back resources
                            player.addResource(offer.what, offer.amount)
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
                    }, {
                        println("offre")
                        val result =
                            saveManager.executeQuery("SELECT id FROM offers", true) ?: throw IllegalStateException(
                                "No offers"
                            )
                        val offers = arrayListOf<Offer>()
                        while (result.next()) {
                            offers.add(Offer(result.getLong("id")))
                        }
                        result.close()
                        println("offers : $offers")
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
                            }) { offer: Offer, buttonClickEvent: ButtonClickEvent ->
                            val player = getAccount(slashCommand)
                            // On vérifie si l'offre existe encore dans la base de données
                            val resultSet =
                                saveManager.executeQuery("SELECT id FROM offers WHERE id = ${offer.id}", true)
                            if (resultSet == null || !resultSet.next()) {
                                throw IllegalStateException("L'offre n'existe plus")
                            }
                            if (offer.who.id == player.id) {
                                throw IllegalArgumentException("Vous ne pouvez pas acheter vos propres offres")
                            }
                            if (!player.hasMoney(offer.amountRB)) {
                                throw IllegalArgumentException("Vous n'avez pas assez d'argent pour acheter cette offre")
                            }
                            player.removeMoney(offer.amountRB)
                            offer.who.addMoney(offer.amountRB)
                            player.addResource(offer.what, offer.amount)
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
                        println("registered")
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
                    }) { event ->
                    val id = generateUniqueID()
                    val idItem = generateUniqueID()
                    val idQuantity = generateUniqueID()
                    val idCost = generateUniqueID()

                    event.buttonInteraction.respondWithModal(
                        id.toString(), "Création d'une offre",
                        ActionRow.of(
                            TextInput.create(
                                TextInputStyle.SHORT,
                                idItem.toString(),
                                "Quelle ressource / objet voulez-vous vendre ?",
                                true
                            )
                        ),
                        ActionRow.of(
                            TextInput.create(
                                TextInputStyle.SHORT,
                                idQuantity.toString(),
                                "Combien voulez-vous en vendre ?",
                                true
                            )
                        ),
                        ActionRow.of(
                            TextInput.create(
                                TextInputStyle.SHORT,
                                idCost.toString(),
                                "Combien voulez-vous de RB en échange ?",
                                true
                            )
                        )
                    )

                    modalManager.add(id) {

                        val modalInteraction = it.modalInteraction
                        val opResource = modalInteraction.getTextInputValueByCustomId(idItem.toString())
                        val opQuantity = modalInteraction.getTextInputValueByCustomId(idQuantity.toString())
                        val opCost = modalInteraction.getTextInputValueByCustomId(idCost.toString())

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

                        val p = getAccount(slashCommand)
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

                        it.modalInteraction.createImmediateResponder()
                            .setContent("Votre offre a bien été enregistrée :  $costDouble ${Resource.RABBIT_COIN.name_} -> $quantityDouble ${resource.progName}")
                            .respond()
                    }
                }
            }
            .addButton(
                "Recherches",
                "Les acheteurs recherchent un objet pour un certain prix"
            ) { messageComponentCreateEvent: ButtonClickEvent ->
                askWhat(
                    "recherche",
                    messageComponentCreateEvent,
                    {
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
                            }
                        ) { offer: Research, buttonClickEvent: ButtonClickEvent ->
                            val player = getAccount(slashCommand)
                            // On vérifie si l'offre existe encore dans la base de données
                            val resultSet =
                                saveManager.executeQuery("SELECT id FROM researches WHERE id = ${offer.id}", true)
                            if (resultSet == null || !resultSet.next()) {
                                throw IllegalStateException("La recherche n'existe plus")
                            }
                            if (offer.who.id != player.id) {
                                throw IllegalArgumentException("Cette recherche ne vous appartient pas. Vous ne pouvez pas la supprimer et elle ne devrait pas apparaître dans cette liste")
                            }
                            // give back resources
                            player.addMoney(offer.amountRB)
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
                    }, {
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
                            }
                        ) { research: Research, buttonClickEvent: ButtonClickEvent ->
                            val player = getAccount(slashCommand)
                            // On vérifie si l'offre existe encore dans la base de données
                            val resultSet =
                                saveManager.executeQuery("SELECT id FROM researches WHERE id = ${research.id}", true)
                            if (resultSet == null || !resultSet.next()) {
                                throw IllegalStateException("La recherche n'existe plus")
                            }
                            if (research.who.id == player.id) {
                                throw IllegalArgumentException("Vous ne pouvez pas répondre à votre propre recherche")
                            }
                            if (!player.hasResource(research.what, research.amount)) {
                                throw IllegalArgumentException("Vous n'avez pas assez de ${research.what.name_} pour répondre à cette recherche")
                            }
                            player.removeResource(research.what, research.amount)
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
                    }) { event ->
                    val id = generateUniqueID()
                    val idItem = generateUniqueID()
                    val idQuantity = generateUniqueID()
                    val idCost = generateUniqueID()

                    event.buttonInteraction.respondWithModal(
                        id.toString(), "Création d'une recherche",
                        ActionRow.of(
                            TextInput.create(
                                TextInputStyle.SHORT,
                                idItem.toString(),
                                "Quelle ressource / objet cherchez vous ?",
                                true
                            )
                        ),
                        ActionRow.of(
                            TextInput.create(
                                TextInputStyle.SHORT,
                                idQuantity.toString(),
                                "Combien en cherchez vous ?",
                                true
                            )
                        ),
                        ActionRow.of(
                            TextInput.create(
                                TextInputStyle.SHORT,
                                idCost.toString(),
                                "Combien voulez-vous donner de RB en échange ?",
                                true
                            )
                        )
                    )

                    modalManager.add(id) {

                        val modalInteraction = it.modalInteraction
                        val opResource = modalInteraction.getTextInputValueByCustomId(idItem.toString())
                        val opQuantity = modalInteraction.getTextInputValueByCustomId(idQuantity.toString())
                        val opCost = modalInteraction.getTextInputValueByCustomId(idCost.toString())

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

                        val p = getAccount(slashCommand)
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

                        it.modalInteraction.createImmediateResponder()
                            .setContent("Votre recherche a bien été enregistrée :  $quantityDouble ${resource.progName} -> $costDouble ${Resource.RABBIT_COIN.name_}")
                            .respond()
                    }
                }
            }
            .addButton(
                "Enchères",
                "Ici trouvez les objets les plus rares et chers"
            ) { messageComponentCreateEvent: ButtonClickEvent ->
                askWhat("enchère", messageComponentCreateEvent, {
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
                        }
                    ) { auction: Auction, buttonClickEvent: ButtonClickEvent ->
                        val player = getAccount(slashCommand)
                        // On vérifie si l'enchère existe encore dans la base de données
                        val resultSet =
                            saveManager.executeQuery("SELECT id FROM auctions WHERE id = ${auction.id}", true)
                        if (resultSet == null || !resultSet.next()) {
                            throw IllegalStateException("L'enchère n'existe plus")
                        }
                        if (auction.who.id != player.id) {
                            throw IllegalArgumentException("Cette enchère ne vous appartient pas. Vous ne pouvez pas la terminer et elle ne devrait pas apparaître dans cette liste")
                        }
                        if (auction.who.id == auction.whoMax.id) {
                            throw IllegalArgumentException("Vous êtes le meilleur enchérisseur. Vous ne pouvez pas terminer cette enchère")
                        }
                        // give the auction money
                        player.addMoney(auction.amountRB)
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
                }, {
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
                        }
                    ) { auction: Auction, buttonClickEvent: ButtonClickEvent ->
                        val player = getAccount(slashCommand)
                        // On vérifie si l'offre existe encore dans la base de données
                        val resultSet =
                            saveManager.executeQuery("SELECT id FROM auctions WHERE id = ${auction.id}", true)
                        if (resultSet == null || !resultSet.next()) {
                            throw IllegalStateException("L'enchère n'existe plus")
                        }
                        if (auction.who.id == player.id || auction.whoMax.id == player.id) {
                            throw IllegalArgumentException("Vous ne pouvez pas répondre à votre propre enchère")
                        }
                        if (!player.hasMoney(auction.amountRB + 100.0)) {
                            throw IllegalArgumentException("Vous n'avez pas assez d'argent pour répondre à cette enchère (il faut enchérir de 100 ${Resource.RABBIT_COIN.name_})")
                        }
                        player.removeMoney(auction.amountRB + 100.0)
                        auction.whoMax.addMoney(auction.amountRB)
                        auction["whoMax"] = player.id.toString()
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
                }) { event ->
                    val id = generateUniqueID()
                    val idItem = generateUniqueID()
                    val idQuantity = generateUniqueID()
                    val idCost = generateUniqueID()

                    event.buttonInteraction.respondWithModal(
                        id.toString(), "Création d'une enchère",
                        ActionRow.of(
                            TextInput.create(
                                TextInputStyle.SHORT,
                                idItem.toString(),
                                "Ressource / objet à mettre aux enchères",
                                true
                            )
                        ),
                        ActionRow.of(
                            TextInput.create(
                                TextInputStyle.SHORT,
                                idQuantity.toString(),
                                "Combien voulez-vous en mettre ?",
                                true
                            )
                        ),
                        ActionRow.of(
                            TextInput.create(
                                TextInputStyle.SHORT,
                                idCost.toString(),
                                "Prix de base de l'enchère",
                                true
                            )
                        )
                    )

                    modalManager.add(id) {

                        val modalInteraction = it.modalInteraction
                        val opResource = modalInteraction.getTextInputValueByCustomId(idItem.toString())
                        val opQuantity = modalInteraction.getTextInputValueByCustomId(idQuantity.toString())
                        val opCost = modalInteraction.getTextInputValueByCustomId(idCost.toString())

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

                        val p = getAccount(slashCommand)
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

                        it.modalInteraction.createImmediateResponder()
                            .setContent("Votre enchère a bien été enregistrée :  $costDouble ${Resource.RABBIT_COIN.name_} -> $quantityDouble ${resource.progName}")
                            .respond()
                    }
                }
            }
            .responder(slashCommand)

    }

    private fun askWhat(
        name: String,
        buttonClickEvent: ButtonClickEvent,
        my: (ButtonClickEvent) -> Unit,
        everything: (ButtonClickEvent) -> Unit,
        create: (ButtonClickEvent) -> Unit
    ) {
        MenuBuilder("Que faire ?", "Il existe 3 possibilités pour les ${name}s", Color.YELLOW)
            .addButton(
                "Mes ${name}s",
                "Voir vos ${name}s, si elles existent. Dans le cas des offres et recherches, vous pouvez les supprimer. Dans le cas des enchères, vous pouvez finir une enchère.",
            ) { messageComponentCreateEvent: ButtonClickEvent ->
                my(messageComponentCreateEvent)
            }
            .addButton(
                "Toutes les ${name}s",
                "Voir toutes les ${name}s. Vous pouvez interagir avec celles-ci ici (répondre à une enchère, acheter une offre, etc.)",
            ) { messageComponentCreateEvent: ButtonClickEvent ->
                everything(messageComponentCreateEvent)
            }
            .addButton(
                "Créer une $name",
                "Créer une $name avec le menu interactif"
            ) { messageComponentCreateEvent: ButtonClickEvent ->
                create(messageComponentCreateEvent)
            }
            .modif(buttonClickEvent)
    }

}