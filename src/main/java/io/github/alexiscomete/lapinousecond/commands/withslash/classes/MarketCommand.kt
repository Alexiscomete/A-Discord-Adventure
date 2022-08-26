package io.github.alexiscomete.lapinousecond.commands.withslash.classes

import io.github.alexiscomete.lapinousecond.commands.withslash.Command
import io.github.alexiscomete.lapinousecond.commands.withslash.ExecutableWithArguments
import io.github.alexiscomete.lapinousecond.message_event.MenuBuilder
import org.javacord.api.entity.message.MessageBuilder
import org.javacord.api.entity.message.component.ActionRow
import org.javacord.api.entity.message.embed.EmbedBuilder
import org.javacord.api.event.interaction.MessageComponentCreateEvent
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
            .addButton("Donner", "Donner un objet ou des ressources à un autre joueur") { messageComponentCreateEvent: MessageComponentCreateEvent ->

            }
            .addButton("Echanger", "Echanger un objet ou des ressources avec un autre joueur de façon sécurisée") { messageComponentCreateEvent: MessageComponentCreateEvent ->

            }
            .addButton("Offres", "Les vendeurs proposent un prix") { messageComponentCreateEvent: MessageComponentCreateEvent ->

            }
            .addButton("Recherches", "Les acheteurs recherchent un objet pour un certain prix") { messageComponentCreateEvent: MessageComponentCreateEvent ->

            }
            .addButton("Enchères", "Ici trouvez les objets les plus rares et chers") { messageComponentCreateEvent: MessageComponentCreateEvent ->

            }
            .responder(slashCommand)

    }
}