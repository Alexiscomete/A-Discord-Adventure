package io.github.alexiscomete.lapinousecond.commands.withslash.classes

import io.github.alexiscomete.lapinousecond.commands.withslash.Command
import io.github.alexiscomete.lapinousecond.commands.withslash.ExecutableWithArguments
import io.github.alexiscomete.lapinousecond.commands.withslash.getAccount
import io.github.alexiscomete.lapinousecond.entity.Player
import io.github.alexiscomete.lapinousecond.entity.PlayerWithAccount
import io.github.alexiscomete.lapinousecond.resources.Resource
import io.github.alexiscomete.lapinousecond.useful.managesave.generateUniqueID
import io.github.alexiscomete.lapinousecond.view.Context
import io.github.alexiscomete.lapinousecond.view.contextFor
import io.github.alexiscomete.lapinousecond.view.contextmanager.ModalContextManager
import io.github.alexiscomete.lapinousecond.view.ui.EmbedPages
import io.github.alexiscomete.lapinousecond.view.ui.MenuBuilder
import io.github.alexiscomete.lapinousecond.view.contextmanager.SelectMenuContextManager
import io.github.alexiscomete.lapinousecond.worlds.WorldEnum
import io.github.alexiscomete.lapinousecond.worlds.bigger
import io.github.alexiscomete.lapinousecond.worlds.map.FilesMapEnum
import org.javacord.api.entity.message.MessageBuilder
import org.javacord.api.entity.message.component.*
import org.javacord.api.entity.message.embed.EmbedBuilder
import org.javacord.api.event.interaction.ButtonClickEvent
import org.javacord.api.event.interaction.ModalSubmitEvent
import org.javacord.api.event.interaction.SelectMenuChooseEvent
import org.javacord.api.interaction.SlashCommandInteraction
import java.awt.Color

private fun verifyBal(player: Player) {
    var bal = player["bal"]
    if (bal == "") {
        player["bal"] = "0.0"
        bal = "0.0"
    }

    val balDouble = bal.toDouble()
    if (balDouble < 100.0) {
        throw IllegalStateException("La guilde des lapins de transports demande 100.0 ${Resource.RABBIT_COIN.name_} pour voyager dans un autre monde")
    }
}

class Select(name: String, val worlds: List<WorldEnum>) : SelectMenuContextManager(name) {
    override fun ex(smce: SelectMenuChooseEvent, c: Context) {
        val selectMenuInteraction = smce.selectMenuInteraction
        val index = selectMenuInteraction.chosenOptions[0].label.toInt()
        val world = worlds[index]
        val player = c.players.player.player
        // get the player's bal
        verifyBal(player)

        MenuBuilder(
            "Confirmer",
            "Confirmez-vous le voyage vers ce monde pour 100 ${Resource.RABBIT_COIN.name_} ?",
            Color.orange,
            c
        )
            .addButton("Oui", "Oui je veux changer de monde") { it, _, _ ->
                // get the player's bal
                verifyBal(player)

                player.removeMoney(100.0)

                player["world"] = world.progName
                if (player["place_${world.progName}_x"] == "") {
                    player["place_${world}_type"] = "coos"
                    player["place_${world.progName}_x"] = world.defaultX.toString()
                    player["place_${world.progName}_y"] = world.defaultY.toString()
                }
                it.buttonInteraction.createOriginalMessageUpdater()
                    .removeAllComponents()
                    .removeAllEmbeds()
                    .setContent("Vous êtes maintenant dans le monde ${world.progName}")
                    .update()
            }
            .addButton("Non", "Non je ne veux pas changer de monde") { it, _, _ ->
                it.buttonInteraction.createOriginalMessageUpdater()
                    .removeAllComponents()
                    .removeAllEmbeds()
                    .setContent("Vous avez annulé le voyage")
                    .update()
            }
            .modif(smce)
    }
}

class MapCommand : Command(
    "map",
    "Permet de faire toutes les actions à propos des déplacements sur la carte",
    "map"
), ExecutableWithArguments {
    override val fullName: String
        get() = "map"
    override val botPerms: Array<String>
        get() = arrayOf("PLAY")

    class M1(name: String) : ModalContextManager(name) {
        override fun ex(smce: ModalSubmitEvent, c: Context) {
            val modalInteraction = smce.modalInteraction
            val opX = modalInteraction.getTextInputValueByCustomId("cxid")
            val opY = modalInteraction.getTextInputValueByCustomId("cyid")

            // optional to string
            val xStr = if (opX.isPresent) {
                opX.get()
            } else {
                throw IllegalArgumentException("x is not present")
            }
            val yStr = if (opY.isPresent) {
                opY.get()
            } else {
                throw IllegalArgumentException("y is not present")
            }

            // str to int
            val x = try {
                xStr.toInt()
            } catch (e: Exception) {
                throw IllegalArgumentException("x is not an int")
            }
            val y = try {
                yStr.toInt()
            } catch (e: Exception) {
                throw IllegalArgumentException("y is not an int")
            }

            val player = c.players.player.player
            val world = try {
                WorldEnum.valueOf(player["world"])
            } catch (e: Exception) {
                throw IllegalArgumentException("world is not a valid world")
            }
            val currentX = try {
                player["place_${world.progName}_x"].toInt()
            } catch (e: Exception) {
                throw IllegalArgumentException("current x is not an int")
            }
            val currentY = try {
                player["place_${world.progName}_y"].toInt()
            } catch (e: Exception) {
                throw IllegalArgumentException("current y is not an int")
            }

            if (x < 0 || x > world.mapWidth || y < 0 || y > world.mapHeight) {
                throw IllegalArgumentException("x or y is out of bounds (bounds are ${world.mapWidth}x${world.mapHeight})")
            }

            // Etape : calcul du trajet et affichage du prix en temps ou en argent
            val nodePlayer = world.getNode(currentX, currentY, ArrayList())
            val nodeDest = world.getNode(x, y, ArrayList())
            //long !!
            smce.modalInteraction.createImmediateResponder()
                .setContent("Patientez un instant... calcul du trajet")
            val path = world.findPath(nodePlayer, nodeDest)
            val image = bigger(world.drawPath(path), 3)

            val timeMillisToTravel = path.size * 10000L
            val priceToTravel = path.size * 0.5

            MenuBuilder(
                "Comment voyager ?",
                "Il existe 2 moyens de voyager de façon simple",
                Color.RED,
                c
            )
                .setImage(image)
                .addButton(
                    "Temps",
                    "Vous allez prendre $timeMillisToTravel ms pour aller jusqu'à ce pixel"
                ) { timeB, c3, _ ->
                    MenuBuilder(
                        "Confirmer",
                        "Confirmer le voyage ?",
                        Color.orange,
                        c3
                    )
                        .addButton("Oui", "Oui je veux aller jusqu'à ce pixel") { it, _, _ ->
                            player.setPath(path, "default_time")
                            it.buttonInteraction.createOriginalMessageUpdater()
                                .removeAllComponents()
                                .removeAllEmbeds()
                                .setContent("Vous êtes maintenant sur le trajet vers le pixel ($x, $y)")
                                .update()
                        }
                        .addButton("Non", "Non je ne veux pas aller jusqu'à ce pixel") { it, _, _ ->
                            it.buttonInteraction.createOriginalMessageUpdater()
                                .removeAllComponents()
                                .removeAllEmbeds()
                                .setContent("Vous avez annulé le voyage")
                                .update()
                        }
                        .modif(timeB)
                }
                .addButton(
                    "Argent",
                    "Vous allez dépenser $priceToTravel ${Resource.RABBIT_COIN.name_} pour aller jusqu'à ce pixel"
                ) { moneyB, c3, _ ->
                    MenuBuilder(
                        "Confirmer",
                        "Confirmer le voyage ?",
                        Color.orange,
                        c3
                    )
                        .addButton("Oui", "Oui je veux aller jusqu'à ce pixel") { it, _, _ ->
                            // get the player's money
                            val money = player.getMoney()
                            if (money < priceToTravel) {
                                it.buttonInteraction.createOriginalMessageUpdater()
                                    .removeAllComponents()
                                    .removeAllEmbeds()
                                    .setContent("Vous n'avez pas assez d'argent pour aller jusqu'à ce pixel")
                                    .update()
                            } else {
                                player.removeMoney(priceToTravel)
                                player["place_${world.progName}_x"] = x.toString()
                                player["place_${world.progName}_y"] = y.toString()
                                it.buttonInteraction.createOriginalMessageUpdater()
                                    .removeAllComponents()
                                    .removeAllEmbeds()
                                    .setContent("Vous êtes maintenant sur le pixel ($x, $y)")
                                    .update()
                            }
                        }
                        .addButton("Non", "Non je ne veux pas aller jusqu'à ce pixel") { it, _, _ ->
                            it.buttonInteraction.createOriginalMessageUpdater()
                                .removeAllComponents()
                                .removeAllEmbeds()
                                .setContent("Vous avez annulé le voyage")
                                .update()
                        }
                        .modif(moneyB)
                }
                .responder(smce.modalInteraction)
        }

    }

    class M2(name: String): ModalContextManager(name) {
        override fun ex(smce: ModalSubmitEvent, c: Context) {

            // get optionals text inputs from modal interaction
            val modalInteraction = smce.modalInteraction
            val opX1 = modalInteraction.getTextInputValueByCustomId("cx1id")
            val opY1 = modalInteraction.getTextInputValueByCustomId("cy1id")
            val opX2 = modalInteraction.getTextInputValueByCustomId("cx2id")
            val opY2 = modalInteraction.getTextInputValueByCustomId("cy2id")

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

            val player = c.players.player.player
            val world = player.world

            // check if the arguments are in the right range
            if (x1Int < 0 || x1Int > world.mapWidth) {
                throw IllegalArgumentException("The first argument must be between 0 and " + world.mapWidth)
            }
            if (y1Int < 0 || y1Int > world.mapHeight) {
                throw IllegalArgumentException("The second argument must be between 0 and " + world.mapHeight)
            }
            if (x2Int < 0 || x2Int > world.mapWidth) {
                throw IllegalArgumentException("The third argument must be between 0 and " + world.mapWidth)
            }
            if (y2Int < 0 || y2Int > world.mapHeight) {
                throw IllegalArgumentException("The fourth argument must be between 0 and " + world.mapHeight)
            }

            // send a later responder
            modalInteraction.createImmediateResponder()
                .setContent("📍 Calcul en cours ...")
                .respond()

            // send the path
            val path = world.findPath(
                world.getNode(
                    x1Int, y1Int, ArrayList()
                ),
                world.getNode(
                    x2Int, y2Int, ArrayList()
                )
            )

            MessageBuilder()
                .addAttachment(world.drawPath(path), "path.png")
                .setContent("📍 Chemin trouvé : " + path.size + " étapes")
                .send(modalInteraction.channel.get())
        }
    }

    class M4(name: String): ModalContextManager(name) {
        override fun ex(smce: ModalSubmitEvent, c: Context) {
            val modalInteraction = smce.modalInteraction
            val opX = modalInteraction.getTextInputValueByCustomId("cxid")
            val opY = modalInteraction.getTextInputValueByCustomId("cyid")
            val opZoom = modalInteraction.getTextInputValueByCustomId("czoomid")

            // transform optionals to strings
            val x = opX.orElse("n")
            val y = opY.orElse("n")
            val zoomStr = opZoom.orElse("n")

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
                zoomStr.toInt()
            } catch (e: NumberFormatException) {
                throw IllegalArgumentException("Le zoom n'est pas un nombre")
            }

            val player = c.players.player.player
            val world = player.world

            // check if the arguments are in the right range
            if (xInt < 0 || xInt > world.mapWidth) {
                throw IllegalArgumentException("Le x de la case n'est pas dans la carte")
            }
            if (yInt < 0 || yInt > world.mapHeight) {
                throw IllegalArgumentException("Le y de la case n'est pas dans la carte")
            }

            // check if zoom is < 60 and > 0
            if (zoomInt < 1 || zoomInt > 60) {
                throw IllegalArgumentException("Le zoom doit être compris entre 1 et 60 (et rester dans la carte !)")
            }

            // send the zoom on the map
            val later = modalInteraction.respondLater()

            val image = world.zoomWithCity(xInt, yInt, zoomInt)

            later.thenAccept {
                it.addEmbed(
                    EmbedBuilder()
                        .setTitle("Zoom sur la carte")
                        .setImage(image)
                )
                    .update()
            }
        }
    }

    class M5(name: String): ModalContextManager(name) {
        override fun ex(smce: ModalSubmitEvent, c: Context) {
            val opInt = smce.interaction.asModalInteraction()
            if (!opInt.isPresent) {
                throw IllegalStateException("Interaction is not a modal interaction")
            }

            // get optionals text inputs from modal interaction
            val modalInteraction = opInt.get()
            val opX = modalInteraction.getTextInputValueByCustomId("cxid")
            val opY = modalInteraction.getTextInputValueByCustomId("cyid")

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

            val player = c.players.player.player
            val world = player.world

            if (xInt < 0 || xInt > world.mapWidth) {
                throw IllegalArgumentException("Le x de la case n'est pas dans la carte")
            }
            if (yInt < 0 || yInt > world.mapHeight) {
                throw IllegalArgumentException("Le y de la case n'est pas dans la carte")
            }

            val biome = if (world.isDirt(xInt, yInt)) {
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

    override fun execute(slashCommand: SlashCommandInteraction) {
        val context = contextFor(PlayerWithAccount(slashCommand.user))
        MenuBuilder(
            "Carte 🗺",
            "Se déplacer est important dans le jeu ! Visitez le monde et regardez autour de vous",
            Color.PINK,
            context
        )
            .addButton(
                "Voyager",
                "Permet de se déplacer de plusieurs façons sur la carte"
            ) { messageComponentCreateEvent: ButtonClickEvent, c1, _ ->
                MenuBuilder(
                    "Voyager",
                    "Voyager est important dans ce jeu",
                    Color.PINK,
                    c1
                )
                    .addButton(
                        "Mondes",
                        "Permet de changer de monde"
                    ) { mcce: ButtonClickEvent, _, _ ->
                        // Etape 1 : afficher la liste des mondes

                        // get all worlds
                        val worldEnums = WorldEnum.values()
                        val worlds = worldEnums.map { it }
                        val player = getAccount(slashCommand)

                        // create the embed builder
                        val eb = EmbedBuilder()
                            .setTitle("Mondes")
                            .setDescription("Choisissez un monde. Votre monde : ${player["world"]}")
                            .setColor(Color.PINK)

                        // for each world, add a field
                        for ((i, world) in worlds.withIndex()) {
                            eb.addField(
                                "(${i}) ${world.nameRP}",
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

                        context.selectMenu(Select(id.toString(), worlds))

                        mcce.buttonInteraction.createImmediateResponder()
                            .addEmbed(eb)
                            .addComponents(actionRow)
                            .respond()
                    }
                    .addButton(
                        "Aller à",
                        "Mode de déplacement le plus simple. Permet de se déplacer sur le pixel de son choix"
                    ) { buttonClickEvent: ButtonClickEvent, c2, _ ->
                        val id = generateUniqueID().toString()

                        buttonClickEvent.buttonInteraction.respondWithModal(
                            id, "Sur quel pixel se rendre ?",
                            ActionRow.of(
                                TextInput.create(TextInputStyle.SHORT, "cxid", "Le x du pixel")
                            ),
                            ActionRow.of(
                                TextInput.create(TextInputStyle.SHORT, "cyid", "Le y du pixel")
                            )
                        )

                        c2.modal(
                            M1(
                                id
                            )
                        )
                    }
                    .addButton(
                        "Pixel par pixel",
                        "Mode de déplacement maîtrisable."
                    ) { buttonClickEvent: ButtonClickEvent, _, _ ->
                        buttonClickEvent.buttonInteraction.createOriginalMessageUpdater()
                            .removeAllComponents()
                            .removeAllEmbeds()
                            .setContent("Bientôt disponible ! 😉")
                            .update()
                    }
                    .modif(messageComponentCreateEvent)
            }
            .addButton(
                "Retourner au hub",
                "Une urgence ? Bloqué dans un lieu inexistant ? Retournez au hub gratuitement !"
            ) { messageComponentCreateEvent: ButtonClickEvent, c1, _ ->
                val p = getAccount(slashCommand)
                MenuBuilder(
                    "Confirmation requise",
                    "Voulez-vous vraiment retourner au hub ?",
                    Color.PINK,
                    c1
                )
                    .addButton("Oui", "Retourner au hub") { buttonClickEvent: ButtonClickEvent, _, _ ->
                        buttonClickEvent.buttonInteraction.createOriginalMessageUpdater()
                            .setContent("✔ Flavinou vient de vous téléporter au hub <https://discord.gg/q4hVQ6gwyx>")
                            .update()
                        toSpawn(p)
                    }
                    .addButton("Non", "Annuler") { buttonClickEvent: ButtonClickEvent, _, _ ->
                        buttonClickEvent.buttonInteraction.createOriginalMessageUpdater()
                            .setContent("Annulé").update()
                    }
                    .modif(messageComponentCreateEvent)
            }
            .addButton(
                "Cartes",
                "Les cartes sont disponibles ici ! De nombreuses actions complémentaires sont proposées"
            ) { messageComponentCreateEvent: ButtonClickEvent, c1, _ ->
                MenuBuilder(
                    "Cartes 🌌",
                    "Les cartes ... tellement de cartes !",
                    Color.PINK,
                    c1
                )
                    .addButton(
                        "Liste des cartes",
                        "Toutes les cartes permanentes du jeu ... remerciez Darki"
                    ) { buttonClickEvent: ButtonClickEvent, _, _ ->
                        val maps = arrayListOf(*FilesMapEnum.values())
                        val embed = EmbedBuilder()
                        val embedPages = EmbedPages(
                            embed,
                            maps,
                            { embedBuilder: EmbedBuilder, i: Int, i1: Int, filesMapEnums: ArrayList<FilesMapEnum> ->
                                for (j in i until i + i1) {
                                    val map = filesMapEnums[j]
                                    embedBuilder.addField(
                                        map.name,
                                        map.description + "\n" + map.urlOfMap + "\n de : " + map.author,
                                        false
                                    )
                                }
                            },
                            context
                        )
                        embedPages.register()
                        buttonClickEvent.buttonInteraction.createOriginalMessageUpdater()
                            .removeAllComponents()
                            .removeAllEmbeds()
                            .setContent("Liste des cartes")
                            .addEmbed(embed)
                            .addComponents(embedPages.components)
                            .update()
                    }
                    .addButton(
                        "Ma position",
                        "Toutes les informations sur votre position"
                    ) { buttonClickEvent: ButtonClickEvent, _, _ ->

                        val player = getAccount(slashCommand)
                        val worldStr = player["world"]
                        val world = WorldEnum.valueOf(worldStr)
                        val position = player.positionToString()

                        val x = player["place_${worldStr}_x"]
                        val y = player["place_${worldStr}_y"]
                        val xInt = x.toInt()
                        val yInt = y.toInt()
                        val biome = if (world.isDirt(xInt, yInt)) "la terre" else "l'eau"

                        val later = buttonClickEvent.buttonInteraction.respondLater()
                        val image = world.zoomWithCity(xInt, yInt, 30, player)

                        later.thenAccept {
                            if (player["tuto"] == "6") {
                                it
                                    .addEmbed(
                                        EmbedBuilder()
                                            .setTitle("Vous êtes dans $biome")
                                            .setImage(image)
                                            .setDescription(position)
                                            .setColor(Color.PINK)
                                    )
                                    .setContent("> (Aurimezi) : Drôle de position ... allons voir la ville la plus proche ! Fait `/map` puis `voyager` et enfin `Aller à`. Je doit malheureusement te laisser, je dois aller voir un de tes futurs équipements pour un recrutement. Bonne chance !")
                                    .update()
                            } else {
                                it
                                    .addEmbed(
                                        EmbedBuilder()
                                            .setTitle("Vous êtes dans $biome")
                                            .setImage(image)
                                            .setDescription(position)
                                            .setColor(Color.PINK)
                                    )
                                    .update()
                            }
                        }

                    }
                    .addButton(
                        "Trouver un chemin",
                        "Un lieu ou des coordonnées ? Trouvez le chemin le plus court"
                    ) { mcce: ButtonClickEvent, c2, _ ->
                        val id = generateUniqueID().toString()
                        mcce.buttonInteraction
                            .respondWithModal(
                                id, "Trouver un chemin",
                                ActionRow.of(
                                    TextInput.create(
                                        TextInputStyle.SHORT,
                                        "cx1id",
                                        "Le x du point de départ",
                                        true
                                    )
                                ),
                                ActionRow.of(
                                    TextInput.create(
                                        TextInputStyle.SHORT,
                                        "cy1id",
                                        "Le y du point de départ",
                                        true
                                    )
                                ),
                                ActionRow.of(
                                    TextInput.create(
                                        TextInputStyle.SHORT,
                                        "cx2id",
                                        "Le x du point d'arrivée",
                                        true
                                    )
                                ),
                                ActionRow.of(
                                    TextInput.create(
                                        TextInputStyle.SHORT,
                                        "cy2id",
                                        "Le y du point d'arrivée",
                                        true
                                    )
                                )
                            )

                        c2.modal(
                            M2(id)
                        )
                    }
                    .addButton(
                        "Zoomer",
                        "Zoomer sur une carte"
                    ) { zoom: ButtonClickEvent, c2, _ ->
                        val id = generateUniqueID().toString()

                        zoom.buttonInteraction
                            .respondWithModal(
                                id,
                                "Informations pour zoomer sur la carte",
                                ActionRow.of(
                                    TextInput.create(
                                        TextInputStyle.SHORT,
                                        "cxid",
                                        "Le x de la case",
                                        true
                                    )
                                ),
                                ActionRow.of(
                                    TextInput.create(
                                        TextInputStyle.SHORT,
                                        "cyid",
                                        "Le y de la case",
                                        true
                                    )
                                ),
                                ActionRow.of(
                                    TextInput.create(
                                        TextInputStyle.SHORT,
                                        "czoomid",
                                        "Zoom (1-60) = hauteur/2",
                                        true
                                    )
                                )
                            )

                        c2.modal(
                            M4(id)
                        )
                    }
                    .addButton(
                        "Type de case",
                        "Le biome d'une case et les informations"
                    ) { mcce: ButtonClickEvent, c2, _ ->
                        val id = generateUniqueID().toString()
                        val idX = generateUniqueID()
                        val idY = generateUniqueID()
                        mcce.buttonInteraction
                            .respondWithModal(
                                id, "Type de case",
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

                        c2.modal(
                            M5(id)
                        )
                    }
                    .modif(messageComponentCreateEvent)
            }
            .responder(slashCommand)

    }
}