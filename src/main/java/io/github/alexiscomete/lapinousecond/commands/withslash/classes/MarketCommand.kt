package io.github.alexiscomete.lapinousecond.commands.withslash.classes

import io.github.alexiscomete.lapinousecond.commands.withslash.Command
import io.github.alexiscomete.lapinousecond.commands.withslash.ExecutableWithArguments
import io.github.alexiscomete.lapinousecond.commands.withslash.getAccount
import io.github.alexiscomete.lapinousecond.entity.players
import io.github.alexiscomete.lapinousecond.message_event.MenuBuilder
import io.github.alexiscomete.lapinousecond.messagesManager
import io.github.alexiscomete.lapinousecond.modalManager
import io.github.alexiscomete.lapinousecond.resources.Resource
import io.github.alexiscomete.lapinousecond.useful.managesave.generateUniqueID
import io.github.alexiscomete.lapinousecond.useful.transactions.giveFromTo
import org.javacord.api.entity.message.component.ActionRow
import org.javacord.api.entity.message.component.TextInput
import org.javacord.api.entity.message.component.TextInputStyle
import org.javacord.api.event.interaction.ButtonClickEvent
import org.javacord.api.event.interaction.ModalSubmitEvent
import org.javacord.api.interaction.SlashCommandInteraction
import java.awt.Color

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
        MenuBuilder(
            "Le marché",
            "Ici est le lieu d'échanges entre les joueurs ! Avancez sur vos quêtes en trouvant ici des objets introuvables, gagnez de l'argent en vendant des objets ou des ressources .... bref c'est le lieu des joueurs",
            Color.YELLOW
        )
            .addButton(
                "Donner",
                "Donner un objet ou des ressources à un autre joueur"
            ) { messageComponentCreateEvent: ButtonClickEvent ->
                // Fonctionne de la façon suivante : -give <type d'owner> <owner> <ressource> <quantité>
                // Exemple : -give Jean WOOD 10

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
                        giveFromTo(
                            p, player, quantityDouble,
                            resource
                        )
                    }
                }
            }
            .addButton(
                "Echanger",
                "Echanger un objet ou des ressources avec un autre joueur de façon sécurisée"
            ) { messageComponentCreateEvent: ButtonClickEvent ->

                messagesManager.addListener(
                    slashCommand.channel.get(),
                    slashCommand.user.id
                ) { message ->
                    val owner = message.messageContent
                    // l'owner est au format <@id>, je vais donc extraire l'id
                    val ownerId = owner.substring(2, owner.length - 1)
                    val player =
                        players[ownerId.toLong()] ?: throw IllegalArgumentException("Le joueur n'existe pas")
                    MenuBuilder(
                        "Demande d'échange",
                        "<@${slashCommand.user.id}> vous a proposé un échange",
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

                            var accept = 0
                            var refuse = 0
                            var cancel = 0
                            var negotiate = 0

                            fun pleaseWait(modalSubmitEvent: ModalSubmitEvent, resourceWait: Resource, quantityWait: Double) {
                                wait = modalSubmitEvent
                                resource = resourceWait
                                quantity = quantityWait
                                modalSubmitEvent.interaction.createImmediateResponder()
                                    .setContent("Merci de patienter")
                                    .respond()
                            }

                            fun sendMenu(modalSubmitEvent: ModalSubmitEvent, resource: Resource, quantity: Double) {
                                // accept, refuse, cancel, negotiate are used to count the number of users who select each button
                                MenuBuilder("Suite de l'échange", "La personne avec qui vous échangez a proposé $quantity ${resource.name_}", Color.YELLOW)
                                    .addButton("Accepter", "Vous acceptez l'échange tel qu'il est actuellement. Négociation possible.") {
                                        //TODO
                                    }
                                    .addButton("Refuser", "Vous refusez l'échange mais vous autorisez la négociation.") {
                                        //TODO
                                    }
                                    .addButton("Annuler", "Vous refusez l'échange et vous interdisez la négociation.") {
                                        //TODO
                                    }
                                    .addButton("Négocier", "Vous proposer une resource et une quantité à la place des $quantity ${resource.name_}") {
                                        //TODO
                                    }
                                    .responder(modalSubmitEvent.modalInteraction)
                            }

                            buttonClickEvent.buttonInteraction.respondWithModal(id1.toString(), "Répondez aux question pour commencer l'échange",
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
                            messageComponentCreateEvent.buttonInteraction.respondWithModal(id2.toString(), "Répondez aux question pour commencer l'échange",
                                ActionRow.of(
                                    TextInput.create(
                                        TextInputStyle.SHORT,
                                        idItem2.toString(),
                                        "Quelle ressource / objet voulez-vous échanger ?",
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
                                    sendMenu(it, resource!!, quantity!!)
                                    sendMenu(wait!!, resource1, quantityDouble)
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
                                    sendMenu(it, resource!!, quantity!!)
                                    sendMenu(wait!!, resource1, quantityDouble)
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
                askWhat("offre", messageComponentCreateEvent, {
                    //TODO
                }, {
                    //TODO
                }) {
                    //TODO
                }
            }
            .addButton(
                "Recherches",
                "Les acheteurs recherchent un objet pour un certain prix"
            ) { messageComponentCreateEvent: ButtonClickEvent ->
                askWhat("recherche", messageComponentCreateEvent, {
                    //TODO
                }, {
                    //TODO
                }) {
                    //TODO
                }
            }
            .addButton(
                "Enchères",
                "Ici trouvez les objets les plus rares et chers"
            ) { messageComponentCreateEvent: ButtonClickEvent ->
                askWhat("enchère", messageComponentCreateEvent, {
                    //TODO
                }, {
                    //TODO
                }) {
                    //TODO
                }
            }
            .responder(slashCommand)

    }

    private fun askWhat(
        name: String,
        messageComponentCreateEvent: ButtonClickEvent,
        my: (ButtonClickEvent) -> Unit,
        everything: (ButtonClickEvent) -> Unit,
        create: (ButtonClickEvent) -> Unit
    ) {
        MenuBuilder("Que faire ?", "Il existe 3 possibilités pour les ${name}s", Color.YELLOW)
            .addButton(
                "Mes ${name}s",
                "Voir vos ${name}s, si elles existent"
            ) { messageComponentCreateEvent: ButtonClickEvent ->
                my(messageComponentCreateEvent)
            }
            .addButton(
                "Toutes les ${name}s",
                "Voir toutes les ${name}s. Vous pouvez interagir avec celles-ci ici"
            ) { messageComponentCreateEvent: ButtonClickEvent ->
                everything(messageComponentCreateEvent)
            }
            .addButton(
                "Créer une $name",
                "Créer une $name avec le menu interactif"
            ) { messageComponentCreateEvent: ButtonClickEvent ->
                create(messageComponentCreateEvent)
            }
            .modif(messageComponentCreateEvent)
    }

}