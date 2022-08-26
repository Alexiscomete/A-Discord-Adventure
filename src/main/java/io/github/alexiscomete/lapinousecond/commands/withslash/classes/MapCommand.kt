package io.github.alexiscomete.lapinousecond.commands.withslash.classes

import io.github.alexiscomete.lapinousecond.commands.withslash.Command
import io.github.alexiscomete.lapinousecond.commands.withslash.ExecutableWithArguments
import io.github.alexiscomete.lapinousecond.message_event.MenuBuilder
import org.javacord.api.event.interaction.MessageComponentCreateEvent
import org.javacord.api.interaction.SlashCommandInteraction
import java.awt.Color

class MapCommand : Command(
    "map",
    "Permet de faire toutes les actions à propos des déplacements sur la carte",
    "map"
), ExecutableWithArguments {
    override val fullName: String
        get() = "map"
    override val botPerms: Array<String>?
        get() = arrayOf("PLAY")

    override fun execute(slashCommand: SlashCommandInteraction) {
        MenuBuilder("Carte 🗺", "Se déplacer est important dans le jeu ! Visitez le monde et regardez autour de vous", Color.PINK)
            .addButton("Voyager", "Permet de se déplacer de plusieurs façons sur la carte") { messageComponentCreateEvent: MessageComponentCreateEvent ->

            }
            .addButton("Retourner au hub", "Une urgence ? Bloqué dans un lieu inexistant ? Retournez au hub gratuitement !") { messageComponentCreateEvent: MessageComponentCreateEvent ->

            }
            .addButton("Cartes", "Les cartes sont disponibles ici ! De nombreuses actions complémentaires sont proposées") { messageComponentCreateEvent: MessageComponentCreateEvent ->

            }
            .responder(slashCommand)

    }

}