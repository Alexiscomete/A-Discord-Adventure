package io.github.alexiscomete.lapinousecond.commands.withslash.classes

import io.github.alexiscomete.lapinousecond.buttonsManager
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
import org.javacord.api.event.interaction.MessageComponentCreateEvent
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
    override val botPerms: Array<String>?
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

                messageComponentCreateEvent.messageComponentInteraction.respondWithModal(
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
                    val opInt = mcce.interaction.asModalInteraction()
                    if (!opInt.isPresent) {
                        throw IllegalStateException("Interaction is not a modal interaction")
                    }

                    // get optionals text inputs from modal interaction
                    val modalInteraction = opInt.get()
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
                //TODO
            }
            .addButton(
                "Offres",
                "Les vendeurs proposent un prix"
            ) { messageComponentCreateEvent: ButtonClickEvent ->
                //TODO
            }
            .addButton(
                "Recherches",
                "Les acheteurs recherchent un objet pour un certain prix"
            ) { messageComponentCreateEvent: ButtonClickEvent ->
                //TODO
            }
            .addButton(
                "Enchères",
                "Ici trouvez les objets les plus rares et chers"
            ) { messageComponentCreateEvent: ButtonClickEvent ->
                //TODO
            }
            .responder(slashCommand)

    }
}