package io.github.alexiscomete.lapinousecond.commands.withslash.classes

import io.github.alexiscomete.lapinousecond.commands.withslash.Command
import io.github.alexiscomete.lapinousecond.commands.withslash.ExecutableWithArguments
import io.github.alexiscomete.lapinousecond.commands.withslash.getAccount
import io.github.alexiscomete.lapinousecond.entity.Player
import io.github.alexiscomete.lapinousecond.message_event.EmbedPages
import io.github.alexiscomete.lapinousecond.message_event.MenuBuilder
import io.github.alexiscomete.lapinousecond.modalManager
import io.github.alexiscomete.lapinousecond.resources.Resource
import io.github.alexiscomete.lapinousecond.selectMenuManager
import io.github.alexiscomete.lapinousecond.useful.managesave.generateUniqueID
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
            Color.PINK,
            slashCommand.user.id
        )
            .addButton(
                "Voyager",
                "Permet de se déplacer de plusieurs façons sur la carte"
            ) { messageComponentCreateEvent: ButtonClickEvent ->
                MenuBuilder(
                    "Voyager",
                    "Voyager est important dans ce jeu",
                    Color.PINK,
                    messageComponentCreateEvent.buttonInteraction.user.id
                )
                    .addButton(
                        "Mondes",
                        "Permet de changer de monde"
                    ) { mcce: ButtonClickEvent ->
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

                        selectMenuManager.add(id) { mci: SelectMenuChooseEvent ->
                            val selectMenuInteraction = mci.selectMenuInteraction
                            if (selectMenuInteraction.user.id == slashCommand.user.id) {
                                val index = selectMenuInteraction.chosenOptions[0].label.toInt()
                                val world = worlds[index]

                                // get the player's bal
                                verifyBal(player)

                                MenuBuilder(
                                    "Confirmer",
                                    "Confirmez-vous le voyage vers ce monde pour 100 ${Resource.RABBIT_COIN.name_} ?",
                                    Color.orange,
                                    mci.selectMenuInteraction.user.id
                                )
                                    .addButton("Oui", "Oui je veux changer de monde") {
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
                                    .addButton("Non", "Non je ne veux pas changer de monde") {
                                        it.buttonInteraction.createOriginalMessageUpdater()
                                            .removeAllComponents()
                                            .removeAllEmbeds()
                                            .setContent("Vous avez annulé le voyage")
                                            .update()
                                    }
                                    .modif(mci)
                            }
                        }

                        mcce.buttonInteraction.createImmediateResponder()
                            .addEmbed(eb)
                            .addComponents(actionRow)
                            .respond()
                    }
                    .addButton(
                        "Aller à",
                        "Mode de déplacement le plus simple. Permet de se déplacer sur le pixel de son choix"
                    ) { buttonClickEvent: ButtonClickEvent ->
                        val id = generateUniqueID()
                        val idX = generateUniqueID()
                        val idY = generateUniqueID()

                        buttonClickEvent.buttonInteraction.respondWithModal(
                            id.toString(), "Sur quel pixel se rendre ?",
                            ActionRow.of(
                                TextInput.create(TextInputStyle.SHORT, idX.toString(), "Le x du pixel")
                            ),
                            ActionRow.of(
                                TextInput.create(TextInputStyle.SHORT, idY.toString(), "Le y du pixel")
                            )
                        )

                        modalManager.add(id) { modalXY ->
                            val modalInteraction = modalXY.modalInteraction
                            val opX = modalInteraction.getTextInputValueByCustomId(idX.toString())
                            val opY = modalInteraction.getTextInputValueByCustomId(idY.toString())

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

                            val player = getAccount(slashCommand)
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
                            modalXY.modalInteraction.createImmediateResponder()
                                .setContent("Patientez un instant... calcul du trajet")
                            val path = world.findPath(nodePlayer, nodeDest)
                            val image = bigger(world.drawPath(path), 3)

                            val timeMillisToTravel = path.size * 10000L
                            val priceToTravel = path.size * 0.5

                            MenuBuilder(
                                "Comment voyager ?",
                                "Il existe 2 moyens de voyager de façon simple",
                                Color.RED,
                                modalXY.modalInteraction.user.id
                            )
                                .setImage(image)
                                .addButton(
                                    "Temps",
                                    "Vous allez prendre $timeMillisToTravel ms pour aller jusqu'à ce pixel"
                                ) { timeB ->
                                    MenuBuilder(
                                        "Confirmer",
                                        "Confirmer le voyage ?",
                                        Color.orange,
                                        timeB.buttonInteraction.user.id
                                    )
                                        .addButton("Oui", "Oui je veux aller jusqu'à ce pixel") {
                                            player.setPath(path, "default_time")
                                            it.buttonInteraction.createOriginalMessageUpdater()
                                                .removeAllComponents()
                                                .removeAllEmbeds()
                                                .setContent("Vous êtes maintenant sur le trajet vers le pixel ($x, $y)")
                                                .update()
                                        }
                                        .addButton("Non", "Non je ne veux pas aller jusqu'à ce pixel") {
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
                                ) { moneyB ->
                                    MenuBuilder(
                                        "Confirmer",
                                        "Confirmer le voyage ?",
                                        Color.orange,
                                        moneyB.buttonInteraction.user.id
                                    )
                                        .addButton("Oui", "Oui je veux aller jusqu'à ce pixel") {
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
                                        .addButton("Non", "Non je ne veux pas aller jusqu'à ce pixel") {
                                            it.buttonInteraction.createOriginalMessageUpdater()
                                                .removeAllComponents()
                                                .removeAllEmbeds()
                                                .setContent("Vous avez annulé le voyage")
                                                .update()
                                        }
                                        .modif(moneyB)
                                }
                                .responder(modalXY.modalInteraction)
                        }
                    }
                    .addButton(
                        "Pixel par pixel",
                        "Mode de déplacement maîtrisable."
                    ) { buttonClickEvent: ButtonClickEvent ->
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
            ) { messageComponentCreateEvent: ButtonClickEvent ->
                val p = getAccount(slashCommand)
                MenuBuilder(
                    "Confirmation requise",
                    "Voulez-vous vraiment retourner au hub ?",
                    Color.PINK,
                    messageComponentCreateEvent.buttonInteraction.user.id
                )
                    .addButton("Oui", "Retourner au hub") { buttonClickEvent: ButtonClickEvent ->
                        buttonClickEvent.buttonInteraction.createOriginalMessageUpdater()
                            .setContent("✔ Flavinou vient de vous téléporter au hub <https://discord.gg/q4hVQ6gwyx>")
                            .update()
                        toSpawn(p)
                    }
                    .addButton("Non", "Annuler") { buttonClickEvent: ButtonClickEvent ->
                        buttonClickEvent.buttonInteraction.createOriginalMessageUpdater()
                            .setContent("Annulé").update()
                    }
                    .modif(messageComponentCreateEvent)
            }
            .addButton(
                "Cartes",
                "Les cartes sont disponibles ici ! De nombreuses actions complémentaires sont proposées"
            ) { messageComponentCreateEvent: ButtonClickEvent ->
                MenuBuilder(
                    "Cartes 🌌",
                    "Les cartes ... tellement de cartes !",
                    Color.PINK,
                    messageComponentCreateEvent.buttonInteraction.user.id
                )
                    .addButton(
                        "Liste des cartes",
                        "Toutes les cartes permanentes du jeu ... remerciez Darki"
                    ) { buttonClickEvent: ButtonClickEvent ->
                        val maps = arrayListOf(*FilesMapEnum.values())
                        val embed = EmbedBuilder()
                        val embedPages = EmbedPages(
                            embed,
                            maps
                        ) { embedBuilder: EmbedBuilder, i: Int, i1: Int, filesMapEnums: ArrayList<FilesMapEnum> ->
                            for (j in i until i + i1) {
                                val map = filesMapEnums[j]
                                embedBuilder.addField(
                                    map.name,
                                    map.description + "\n" + map.urlOfMap + "\n de : " + map.author,
                                    false
                                )
                            }
                        }
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
                    ) { buttonClickEvent: ButtonClickEvent ->

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

                            // get optionals text inputs from modal interaction
                            val modalInteraction = messageComponentCreateEvent.modalInteraction
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

                            val player = getAccount(slashCommand)
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
                    .addButton(
                        "Zoomer",
                        "Zoomer sur une carte"
                    ) { zoom: ButtonClickEvent ->

                        val id = generateUniqueID()
                        val idX = generateUniqueID()
                        val idY = generateUniqueID()
                        val idZoom = generateUniqueID()

                        zoom.buttonInteraction
                            .respondWithModal(
                                id.toString(),
                                "Informations pour zoomer sur la carte",
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
                                        "Zoom (1-60) = hauteur/2",
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

                            val player = getAccount(slashCommand)
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

                            val player = getAccount(slashCommand)
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
                    .modif(messageComponentCreateEvent)
            }
            .responder(slashCommand)

    }

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

}