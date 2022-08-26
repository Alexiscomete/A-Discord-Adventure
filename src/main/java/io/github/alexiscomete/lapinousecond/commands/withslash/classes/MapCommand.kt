package io.github.alexiscomete.lapinousecond.commands.withslash.classes

import io.github.alexiscomete.lapinousecond.buttonsManager
import io.github.alexiscomete.lapinousecond.commands.withoutslash.classes.sendPath
import io.github.alexiscomete.lapinousecond.commands.withslash.Command
import io.github.alexiscomete.lapinousecond.commands.withslash.ExecutableWithArguments
import io.github.alexiscomete.lapinousecond.commands.withslash.getAccount
import io.github.alexiscomete.lapinousecond.message_event.MenuBuilder
import io.github.alexiscomete.lapinousecond.useful.managesave.generateUniqueID
import io.github.alexiscomete.lapinousecond.worlds.ServerBot
import io.github.alexiscomete.lapinousecond.worlds.map.Map
import org.javacord.api.entity.message.MessageBuilder
import org.javacord.api.entity.message.component.ActionRow
import org.javacord.api.entity.message.component.TextInput
import org.javacord.api.entity.message.component.TextInputStyle
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
        MenuBuilder(
            "Carte 🗺",
            "Se déplacer est important dans le jeu ! Visitez le monde et regardez autour de vous",
            Color.PINK
        )
            .addButton(
                "Voyager",
                "Permet de se déplacer de plusieurs façons sur la carte"
            ) { messageComponentCreateEvent: MessageComponentCreateEvent ->
                MenuBuilder("Voyager", "Voyager est important dans ce jeu", Color.PINK)
                    .addButton(
                        "Mondes",
                        "Permet de changer de monde"
                    ) { messageComponentCreateEvent: MessageComponentCreateEvent ->

                    }
                    .addButton(
                        "Liens",
                        "Les différents liens depuis votre lieu (ex : train)"
                    ) { messageComponentCreateEvent: MessageComponentCreateEvent ->

                    }
                    .addButton(
                        "Aller à",
                        "Mode de déplacement le plus simple."
                    ) { messageComponentCreateEvent: MessageComponentCreateEvent ->

                    }
                    .addButton(
                        "Pixel par pixel",
                        "Mode de déplacement maîtrisable."
                    ) { messageComponentCreateEvent: MessageComponentCreateEvent ->

                    }

            }
            .addButton(
                "Retourner au hub",
                "Une urgence ? Bloqué dans un lieu inexistant ? Retournez au hub gratuitement !"
            ) { messageComponentCreateEvent: MessageComponentCreateEvent ->
                val p = getAccount(slashCommand)
                MenuBuilder("Confirmation requise", "Voulez-vous vraiment retourner au hub ?", Color.PINK)
                    .addButton("Oui", "Retourner au hub") { messageComponentCreateEvent: MessageComponentCreateEvent ->
                        messageComponentCreateEvent.messageComponentInteraction.createOriginalMessageUpdater()
                            .setContent("✔ Flavinou vient de vous téléporter au hub <https://discord.gg/q4hVQ6gwyx>")
                            .update()
                        p["serv"] = "854288660147994634"
                        p["world"] = "NORMAL"
                        p["place_NORMAL"] = ServerBot(854288660147994634L).getString("places")
                    }
                    .addButton("Non", "Annuler") { messageComponentCreateEvent: MessageComponentCreateEvent ->
                        messageComponentCreateEvent.messageComponentInteraction.createOriginalMessageUpdater()
                            .setContent("Annulé").update()
                    }
                    .modif(messageComponentCreateEvent)
            }
            .addButton(
                "Cartes",
                "Les cartes sont disponibles ici ! De nombreuses actions complémentaires sont proposées"
            ) { messageComponentCreateEvent: MessageComponentCreateEvent ->
                MenuBuilder("Cartes 🌌", "Les cartes ... tellement de cartes !", Color.PINK)
                    .addButton(
                        "Liste des cartes",
                        "Toutes les cartes permanentes du jeu ... remerciez Darki"
                    ) { messageComponentCreateEvent: MessageComponentCreateEvent ->

                    }
                    .addButton(
                        "Ma position",
                        "Toutes les informations sur votre position"
                    ) { messageComponentCreateEvent: MessageComponentCreateEvent ->

                    }
                    .addButton(
                        "Trouver un chemin",
                        "Un lieu ou des coordonnées ? Trouvez le chemin le plus court"
                    ) { messageComponentCreateEvent: MessageComponentCreateEvent ->
                        val id = generateUniqueID()
                        val idX1 = generateUniqueID()
                        val idX2 = generateUniqueID()
                        val idY1 = generateUniqueID()
                        val idY2 = generateUniqueID()
                        messageComponentCreateEvent.messageComponentInteraction.respondWithModal(
                            id.toString(), "Trouver un chemin", ActionRow.of(
                                TextInput.create(
                                    TextInputStyle.SHORT,
                                    idX1.toString(),
                                    "Le x du point de départ",
                                    true
                                ),
                                TextInput.create(
                                    TextInputStyle.SHORT,
                                    idY1.toString(),
                                    "Le y du point de départ",
                                    true
                                ),
                                TextInput.create(
                                    TextInputStyle.SHORT,
                                    idX2.toString(),
                                    "Le x du point d'arrivée",
                                    true
                                ),
                                TextInput.create(TextInputStyle.SHORT, idY2.toString(), "Le y du point d'arrivée", true)
                            )
                        )
                        buttonsManager.addButton(id) { messageComponentCreateEvent: MessageComponentCreateEvent ->
                            val opInt = messageComponentCreateEvent.interaction.asModalInteraction()
                            if (!opInt.isPresent) {
                                throw IllegalStateException("Interaction is not a modal interaction")
                            }

                            // get optionals text inputs from modal interaction
                            val modalInteraction = opInt.get()
                            val opX1 = modalInteraction.getTextInputValueByCustomId(idX1.toString())
                            val opY1 = modalInteraction.getTextInputValueByCustomId(idY1.toString())
                            val opX2 = modalInteraction.getTextInputValueByCustomId(idX2.toString())
                            val opY2 = modalInteraction.getTextInputValueByCustomId(idY2.toString())

                            // transform optionals to strings
                            val x1 = opX1.orElse("n")
                            val y1 = opY1.orElse("n")
                            val x2 = opX2.orElse("n")
                            val y2 = opY2.orElse("n")

                            // check if the strings are numbers
                            val x1Int = try {
                                x1.toInt()
                            } catch (e: NumberFormatException) {
                                throw IllegalArgumentException("Le x du point de départ n'est pas un nombre")
                            }
                            val y1Int = try {
                                y1.toInt()
                            } catch (e: NumberFormatException) {
                                throw IllegalArgumentException("Le y du point de départ n'est pas un nombre")
                            }
                            val x2Int = try {
                                x2.toInt()
                            } catch (e: NumberFormatException) {
                                throw IllegalArgumentException("Le x du point d'arrivée n'est pas un nombre")
                            }
                            val y2Int = try {
                                y2.toInt()
                            } catch (e: NumberFormatException) {
                                throw IllegalArgumentException("Le y du point d'arrivée n'est pas un nombre")
                            }

                            // check if the arguments are in the right range
                            if (x1Int < 0 || x1Int > Map.MAP_WIDTH) {
                                throw IllegalArgumentException("The first argument must be between 0 and " + Map.MAP_WIDTH)
                            }
                            if (y1Int < 0 || y1Int > Map.MAP_HEIGHT) {
                                throw IllegalArgumentException("The second argument must be between 0 and " + Map.MAP_HEIGHT)
                            }
                            if (x2Int < 0 || x2Int > Map.MAP_WIDTH) {
                                throw IllegalArgumentException("The third argument must be between 0 and " + Map.MAP_WIDTH)
                            }
                            if (y2Int < 0 || y2Int > Map.MAP_HEIGHT) {
                                throw IllegalArgumentException("The fourth argument must be between 0 and " + Map.MAP_HEIGHT)
                            }

                            // send a later responder
                            modalInteraction.createImmediateResponder()
                                .setContent("📍 Calcul en cours ...")
                                .respond()

                            // send the path
                            val path = Map.findPath(
                                Map.getNode(
                                    x1Int, y1Int, ArrayList()
                                ), Map.getNode(
                                    x2Int, y2Int, ArrayList()
                                )
                            )

                            modalInteraction.createImmediateResponder()
                                .setContent("📍 Chemin trouvé : " + path.size + " étapes")
                                .respond()

                            val sb = StringBuilder()
                            for (pixel in path) {
                                sb.append(pixel)
                            }
                            MessageBuilder()
                                .addAttachment(Map.drawPath(path), "path.png")
                                .setContent(sb.toString())
                                .send(modalInteraction.channel.get())
                        }
                    }
                    .addButton(
                        "Zoomer",
                        "Zoomer sur une carte"
                    ) { messageComponentCreateEvent: MessageComponentCreateEvent ->

                    }
                    .addButton(
                        "Type de case",
                        "Le biome d'une case et les informations"
                    ) { messageComponentCreateEvent: MessageComponentCreateEvent ->

                    }
            }
            .responder(slashCommand)

    }

}