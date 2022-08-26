package io.github.alexiscomete.lapinousecond.commands.withslash.classes

import io.github.alexiscomete.lapinousecond.commands.withslash.Command
import io.github.alexiscomete.lapinousecond.commands.withslash.ExecutableWithArguments
import io.github.alexiscomete.lapinousecond.commands.withslash.getAccount
import io.github.alexiscomete.lapinousecond.entity.players
import io.github.alexiscomete.lapinousecond.message_event.MenuBuilder
import io.github.alexiscomete.lapinousecond.worlds.ServerBot
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
                MenuBuilder("Voyager", "Voyager est important dans ce jeu", Color.PINK)
                    .addButton("Mondes", "Permet de changer de monde") { messageComponentCreateEvent: MessageComponentCreateEvent ->

                    }
                    .addButton("Liens", "Les différents liens depuis votre lieu (ex : train)") { messageComponentCreateEvent: MessageComponentCreateEvent ->

                    }
            }
            .addButton("Retourner au hub", "Une urgence ? Bloqué dans un lieu inexistant ? Retournez au hub gratuitement !") { messageComponentCreateEvent: MessageComponentCreateEvent ->
                val p = getAccount(slashCommand)
                MenuBuilder("Confirmation requise", "Voulez-vous vraiment retourner au hub ?", Color.PINK)
                    .addButton("Oui", "Retourner au hub") { messageComponentCreateEvent: MessageComponentCreateEvent ->
                        messageComponentCreateEvent.messageComponentInteraction.createOriginalMessageUpdater().setContent("✔ Flavinou vient de vous téléporter au hub <https://discord.gg/q4hVQ6gwyx>").update()
                        p["serv"] = "854288660147994634"
                        p["world"] = "NORMAL"
                        p["place_NORMAL"] = ServerBot(854288660147994634L).getString("places")
                    }
                    .addButton("Non", "Annuler") { messageComponentCreateEvent: MessageComponentCreateEvent ->
                        messageComponentCreateEvent.messageComponentInteraction.createOriginalMessageUpdater().setContent("Annulé").update()
                    }
                    .modif(messageComponentCreateEvent)
            }
            .addButton("Cartes", "Les cartes sont disponibles ici ! De nombreuses actions complémentaires sont proposées") { messageComponentCreateEvent: MessageComponentCreateEvent ->
                MenuBuilder("Cartes 🌌", "Les cartes ... tellement de cartes !", Color.PINK)
                    .addButton("Liste des cartes", "Toutes les cartes permanentes du jeu ... remerciez Darki") { messageComponentCreateEvent: MessageComponentCreateEvent ->

                    }
                    .addButton("Ma position", "Toutes les informations sur votre position") { messageComponentCreateEvent: MessageComponentCreateEvent ->

                    }
                    .addButton("Trouver un chemin", "Un lieu ou des coordonnées ? Trouvez le chemin le plus court") { messageComponentCreateEvent: MessageComponentCreateEvent ->

                    }
                    .addButton("Zoomer", "Zoomer sur une carte") { messageComponentCreateEvent: MessageComponentCreateEvent ->

                    }
                    .addButton("Type de case", "Le biome d'une case et les informations") { messageComponentCreateEvent: MessageComponentCreateEvent ->

                    }
            }
            .responder(slashCommand)

    }

}