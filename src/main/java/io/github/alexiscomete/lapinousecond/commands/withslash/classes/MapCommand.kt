package io.github.alexiscomete.lapinousecond.commands.withslash.classes

import io.github.alexiscomete.lapinousecond.commands.withslash.Command
import io.github.alexiscomete.lapinousecond.commands.withslash.ExecutableWithArguments
import io.github.alexiscomete.lapinousecond.commands.withslash.getAccount
import io.github.alexiscomete.lapinousecond.message_event.MenuBuilder
import io.github.alexiscomete.lapinousecond.modalManager
import io.github.alexiscomete.lapinousecond.selectMenuManager
import io.github.alexiscomete.lapinousecond.useful.managesave.generateUniqueID
import io.github.alexiscomete.lapinousecond.worlds.Place
import io.github.alexiscomete.lapinousecond.worlds.ServerBot
import io.github.alexiscomete.lapinousecond.worlds.WorldEnum
import io.github.alexiscomete.lapinousecond.worlds.map.Map
import org.javacord.api.entity.message.MessageBuilder
import org.javacord.api.entity.message.component.*
import org.javacord.api.entity.message.embed.EmbedBuilder
import org.javacord.api.event.interaction.ButtonClickEvent
import org.javacord.api.event.interaction.ModalSubmitEvent
import org.javacord.api.event.interaction.SelectMenuChooseEvent
import org.javacord.api.interaction.SlashCommandInteraction
import java.awt.Color

class MapCommand : Command(
    "map",
    "Permet de faire toutes les actions à propos des déplacements sur la carte",
    "map"
), ExecutableWithArguments {
    override val fullName: String
        get() = "map"
    override val botPerms: Array<String>
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
            ) { messageComponentCreateEvent: ButtonClickEvent ->
                MenuBuilder("Voyager", "Voyager est important dans ce jeu", Color.PINK)
                    .addButton(
                        "Mondes",
                        "Permet de changer de monde"
                    ) { mcce: ButtonClickEvent ->
                        // Etape 1 : afficher la liste des mondes

                        // get all worlds
                        val worldEnums = WorldEnum.values()
                        val worlds = worldEnums.map { it.world }
                        val player = getAccount(slashCommand)

                        // create the embed builder
                        val eb = EmbedBuilder()
                            .setTitle("Mondes")
                            .setDescription("Choisissez un monde. Votre monde : ${player["world"]}")
                            .setColor(Color.PINK)

                        // for each world, add a field
                        for (world in worlds) {
                            eb.addField(
                                world.name,
                                "**Nom officiel :** ${world.progName}\n**Type de serveur :** ${world.typeOfServer}\n${world.desc}",
                                true
                            )
                        }

                        // create the list of selections
                        val options = ArrayList<SelectMenuOption>()
                        for ((i, zoneDel) in worlds.withIndex()) {
                            options.add(SelectMenuOption.create(i.toString(), zoneDel.toString()))
                        }
                        val id = generateUniqueID()
                        val actionRow = ActionRow.of(SelectMenu.create(id.toString(), "Monde où aller", options))

                        selectMenuManager.add(id) { mci: SelectMenuChooseEvent ->
                            val selectMenuInteraction = mci.selectMenuInteraction
                            if (selectMenuInteraction.user.id == slashCommand.user.id) {
                                val index = selectMenuInteraction.chosenOptions[0].label.toInt()
                                val world = worlds[index]
                                player["world"] = world.progName
                                if (player["x_${world.progName}"] == "") {
                                    player["x_${world.progName}"] = world.defaultX.toString()
                                    player["y_${world.progName}"] = world.defaultY.toString()
                                }
                                selectMenuInteraction.createOriginalMessageUpdater()
                                    .removeAllComponents()
                                    .setContent("Vous êtes maintenant dans le monde ${world.progName}")
                                    .update()

                                //TODO : prix et validation
                            }
                        }

                        mcce.buttonInteraction.createImmediateResponder()
                            .addEmbed(eb)
                            .addComponents(actionRow)
                            .respond()
                    }
                    .addButton(
                        "Liens",
                        "Les différents liens depuis votre lieu (ex : train)"
                    ) { mcce: ButtonClickEvent ->
                        //TODO
                    }
                    .addButton(
                        "Aller à",
                        "Mode de déplacement le plus simple."
                    ) { mcce: ButtonClickEvent ->
                        //TODO
                    }
                    .addButton(
                        "Pixel par pixel",
                        "Mode de déplacement maîtrisable."
                    ) { mcce: ButtonClickEvent ->
                        //TODO
                    }
                    .modif(messageComponentCreateEvent)
            }
            .addButton(
                "Retourner au hub",
                "Une urgence ? Bloqué dans un lieu inexistant ? Retournez au hub gratuitement !"
            ) { messageComponentCreateEvent: ButtonClickEvent ->
                val p = getAccount(slashCommand)
                MenuBuilder("Confirmation requise", "Voulez-vous vraiment retourner au hub ?", Color.PINK)
                    .addButton("Oui", "Retourner au hub") { mcce: ButtonClickEvent ->
                        mcce.buttonInteraction.createOriginalMessageUpdater()
                            .setContent("✔ Flavinou vient de vous téléporter au hub <https://discord.gg/q4hVQ6gwyx>")
                            .update()
                        p["serv"] = "854288660147994634"
                        p["world"] = "NORMAL"
                        p["place_NORMAL"] = ServerBot(854288660147994634L).getString("places")
                    }
                    .addButton("Non", "Annuler") { mcce: ButtonClickEvent ->
                        mcce.buttonInteraction.createOriginalMessageUpdater()
                            .setContent("Annulé").update()
                    }
                    .modif(messageComponentCreateEvent)
            }
            .addButton(
                "Cartes",
                "Les cartes sont disponibles ici ! De nombreuses actions complémentaires sont proposées"
            ) { messageComponentCreateEvent: ButtonClickEvent ->
                MenuBuilder("Cartes 🌌", "Les cartes ... tellement de cartes !", Color.PINK)
                    .addButton(
                        "Liste des cartes",
                        "Toutes les cartes permanentes du jeu ... remerciez Darki"
                    ) { mcce: ButtonClickEvent ->
                        //TODO
                    }
                    .addButton(
                        "Ma position",
                        "Toutes les informations sur votre position"
                    ) { mcce: ButtonClickEvent ->

                        val player = getAccount(slashCommand)
                        val world = player["world"]
                        val position = player.positionToString()

                        if (world == "DIBIMAP") {
                            val x = player["x_DIBIMAP"]
                            val y = player["y_DIBIMAP"]
                            val xInt = x.toInt()
                            val yInt = y.toInt()
                            val image = try {
                                Map.bigger(Map.zoom(xInt, yInt, 30), 10)
                            } catch (e: Exception) {
                                null
                            }
                            val biome = if (Map.isDirt(xInt, yInt)) "la terre" else "l'eau"

                            mcce.buttonInteraction.createOriginalMessageUpdater()
                                .removeAllComponents()
                                .removeAllEmbeds()
                                .addEmbed(
                                    if (image != null) {
                                        EmbedBuilder()
                                            .setTitle("Vous êtes dans $biome")
                                            .setImage(image)
                                            .setDescription(position)
                                            .setColor(Color.PINK)
                                    } else {
                                        EmbedBuilder()
                                            .setTitle("Vous êtes dans $biome (image indisponible : bord de map)")
                                            .setDescription(position)
                                            .setColor(Color.PINK)
                                    }
                                )
                                .update()
                        } else {
                            mcce.buttonInteraction.createOriginalMessageUpdater()
                                .setContent("Vous n'êtes pas dans le monde DIBIMAP donc les informations de pixel sont indisponibles")
                                .removeAllComponents()
                                .removeAllEmbeds()
                                .addEmbed(
                                    EmbedBuilder()
                                        .setTitle("Position")
                                        .setDescription(position)
                                )
                                .update()
                        }
                    }
                    .addButton(
                        "Trouver un chemin",
                        "Un lieu ou des coordonnées ? Trouvez le chemin le plus court"
                    ) { mcce: ButtonClickEvent ->
                        val id = generateUniqueID()
                        val idX1 = generateUniqueID()
                        val idX2 = generateUniqueID()
                        val idY1 = generateUniqueID()
                        val idY2 = generateUniqueID()
                        mcce.buttonInteraction
                            .respondWithModal(
                                id.toString(), "Trouver un chemin",
                                ActionRow.of(
                                    TextInput.create(
                                        TextInputStyle.SHORT,
                                        idX1.toString(),
                                        "Le x du point de départ",
                                        true
                                    )
                                ),
                                ActionRow.of(
                                    TextInput.create(
                                        TextInputStyle.SHORT,
                                        idY1.toString(),
                                        "Le y du point de départ",
                                        true
                                    )
                                ),
                                ActionRow.of(
                                    TextInput.create(
                                        TextInputStyle.SHORT,
                                        idX2.toString(),
                                        "Le x du point d'arrivée",
                                        true
                                    )
                                ),
                                ActionRow.of(
                                    TextInput.create(
                                        TextInputStyle.SHORT,
                                        idY2.toString(),
                                        "Le y du point d'arrivée",
                                        true
                                    )
                                )
                            )

                        modalManager.add(id) { messageComponentCreateEvent: ModalSubmitEvent ->
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
                    ) { mcce: ButtonClickEvent ->

                        val id = generateUniqueID()
                        val idX = generateUniqueID()
                        val idY = generateUniqueID()
                        val idZoom = generateUniqueID()

                        mcce.buttonInteraction
                            .respondWithModal(
                                id.toString(), "Informations pour zoomer sur la carte",
                                ActionRow.of(
                                    TextInput.create(
                                        TextInputStyle.SHORT,
                                        idX.toString(),
                                        "Le x de la case",
                                        true
                                    )
                                ),
                                ActionRow.of(
                                    TextInput.create(
                                        TextInputStyle.SHORT,
                                        idY.toString(),
                                        "Le y de la case",
                                        true
                                    )
                                ),
                                ActionRow.of(
                                    TextInput.create(
                                        TextInputStyle.SHORT,
                                        idZoom.toString(),
                                        "Le zoom de 1 à 60 (plus petit rayon de case visibles, attention à rester dans la carte)",
                                        true
                                    )
                                )
                            )

                        modalManager.add(id) { messageComponentCreateEvent: ModalSubmitEvent ->
                            val modalInteraction = messageComponentCreateEvent.modalInteraction
                            val opX = modalInteraction.getTextInputValueByCustomId(idX.toString())
                            val opY = modalInteraction.getTextInputValueByCustomId(idY.toString())
                            val opZoom = modalInteraction.getTextInputValueByCustomId(idZoom.toString())

                            // transform optionals to strings
                            val x = opX.orElse("n")
                            val y = opY.orElse("n")
                            val zoom = opZoom.orElse("n")

                            // check if the arguments are numbers
                            val xInt = try {
                                x.toInt()
                            } catch (e: NumberFormatException) {
                                throw IllegalArgumentException("Le x de la case n'est pas un nombre")
                            }
                            val yInt = try {
                                y.toInt()
                            } catch (e: NumberFormatException) {
                                throw IllegalArgumentException("Le y de la case n'est pas un nombre")
                            }
                            val zoomInt = try {
                                zoom.toInt()
                            } catch (e: NumberFormatException) {
                                throw IllegalArgumentException("Le zoom n'est pas un nombre")
                            }

                            // check if the arguments are in the right range
                            if (xInt < 0 || xInt > Map.MAP_WIDTH) {
                                throw IllegalArgumentException("Le x de la case n'est pas dans la carte")
                            }
                            if (yInt < 0 || yInt > Map.MAP_HEIGHT) {
                                throw IllegalArgumentException("Le y de la case n'est pas dans la carte")
                            }

                            // check if zoom is < 60 and > 0
                            if (zoomInt < 1 || zoomInt > 60) {
                                throw IllegalArgumentException("Le zoom doit être compris entre 1 et 60 (et rester dans la carte !)")
                            }

                            // send the zoom on the map
                            modalInteraction.createImmediateResponder()
                                .setContent("Création de la carte en cours et ajout des villes proches ...")
                                .respond()

                            val image = Map.bigger(
                                Map.zoom(
                                    xInt, yInt, zoomInt
                                ), 10
                            )

                            val places = Place.getPlacesWithWorld("DIBIMAP")
                            places.removeIf { place: Place ->
                                !place.getX().isPresent || !place.getY().isPresent || place.getX()
                                    .get() < xInt - zoomInt * 2 || place.getX()
                                    .get() > xInt + zoomInt * 2 || place.getY().get() < yInt - zoomInt || place.getY()
                                    .get() > yInt + zoomInt
                            }

                            Map.getMapWithNames(
                                places,
                                xInt - zoomInt * 2,
                                yInt - zoomInt,
                                zoomInt * 4,
                                zoomInt * 2,
                                image
                            )

                            modalInteraction.createImmediateResponder()
                                .addEmbed(
                                    EmbedBuilder()
                                        .setTitle("Zoom sur la carte")
                                        .setImage(image)
                                )
                                .respond()
                        }
                    }
                    .addButton(
                        "Type de case",
                        "Le biome d'une case et les informations"
                    ) { mcce: ButtonClickEvent ->
                        val id = generateUniqueID()
                        val idX = generateUniqueID()
                        val idY = generateUniqueID()
                        mcce.buttonInteraction
                            .respondWithModal(
                                id.toString(), "Type de case",
                                ActionRow.of(
                                    TextInput.create(
                                        TextInputStyle.SHORT,
                                        idX.toString(),
                                        "Le x de la case",
                                        true
                                    )
                                ),
                                ActionRow.of(
                                    TextInput.create(
                                        TextInputStyle.SHORT,
                                        idY.toString(),
                                        "Le y de la case",
                                        true
                                    )
                                )
                            )

                        modalManager.add(id) { messageComponentCreateEvent: ModalSubmitEvent ->
                            val opInt = messageComponentCreateEvent.interaction.asModalInteraction()
                            if (!opInt.isPresent) {
                                throw IllegalStateException("Interaction is not a modal interaction")
                            }

                            // get optionals text inputs from modal interaction
                            val modalInteraction = opInt.get()
                            val opX = modalInteraction.getTextInputValueByCustomId(idX.toString())
                            val opY = modalInteraction.getTextInputValueByCustomId(idY.toString())

                            // transform optionals to strings
                            val x = opX.orElse("n")
                            val y = opY.orElse("n")
                            val xInt = try {
                                x.toInt()
                            } catch (e: NumberFormatException) {
                                throw IllegalArgumentException("Le x de la case n'est pas un nombre")
                            }
                            val yInt = try {
                                y.toInt()
                            } catch (e: NumberFormatException) {
                                throw IllegalArgumentException("Le y de la case n'est pas un nombre")
                            }
                            if (xInt < 0 || xInt > Map.MAP_WIDTH) {
                                throw IllegalArgumentException("Le x de la case n'est pas dans la carte")
                            }
                            if (yInt < 0 || yInt > Map.MAP_HEIGHT) {
                                throw IllegalArgumentException("Le y de la case n'est pas dans la carte")
                            }

                            val biome = if (Map.isDirt(xInt, yInt)) {
                                "la terre"
                            } else {
                                "l'eau"
                            }

                            modalInteraction.createImmediateResponder()
                                .addEmbed(
                                    EmbedBuilder()
                                        .setTitle("Type de case de [$xInt:$yInt]")
                                        .setDescription("🌱 La case est de $biome")
                                        .setColor(Color.BLUE)
                                )
                                .respond()
                        }
                    }
                    .modif(messageComponentCreateEvent)
            }
            .responder(slashCommand)

    }

}