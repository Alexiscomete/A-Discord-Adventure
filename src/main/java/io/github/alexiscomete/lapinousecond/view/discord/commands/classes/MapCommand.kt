package io.github.alexiscomete.lapinousecond.view.discord.commands.classes

import io.github.alexiscomete.lapinousecond.entity.concrete.resources.Resource
import io.github.alexiscomete.lapinousecond.entity.effects.priceToTravelWithEffect
import io.github.alexiscomete.lapinousecond.entity.effects.timeMillisForOnePixel
import io.github.alexiscomete.lapinousecond.entity.entities.Player
import io.github.alexiscomete.lapinousecond.entity.entities.PlayerWithAccount
import io.github.alexiscomete.lapinousecond.data.managesave.generateUniqueID
import io.github.alexiscomete.lapinousecond.view.Context
import io.github.alexiscomete.lapinousecond.view.contextFor
import io.github.alexiscomete.lapinousecond.view.contextmanager.SelectMenuContextManager
import io.github.alexiscomete.lapinousecond.view.discord.commands.Command
import io.github.alexiscomete.lapinousecond.view.discord.commands.ExecutableWithArguments
import io.github.alexiscomete.lapinousecond.view.discord.commands.getAccount
import io.github.alexiscomete.lapinousecond.view.ui.longuis.EmbedPages
import io.github.alexiscomete.lapinousecond.view.ui.longuis.MenuBuilderUI
import io.github.alexiscomete.lapinousecond.view.ui.longuis.PixelByPixelUI
import io.github.alexiscomete.lapinousecond.view.ui.playerui.DiscordPlayerUI
import io.github.alexiscomete.lapinousecond.view.ui.playerui.Message
import io.github.alexiscomete.lapinousecond.view.ui.playerui.Question
import io.github.alexiscomete.lapinousecond.view.ui.playerui.QuestionField
import io.github.alexiscomete.lapinousecond.worlds.WorldEnum
import io.github.alexiscomete.lapinousecond.worlds.Zooms
import io.github.alexiscomete.lapinousecond.worlds.bigger
import io.github.alexiscomete.lapinousecond.worlds.map.FilesMapEnum
import org.javacord.api.entity.message.MessageBuilder
import org.javacord.api.entity.message.component.*
import org.javacord.api.entity.message.embed.EmbedBuilder
import org.javacord.api.event.interaction.ModalSubmitEvent
import org.javacord.api.event.interaction.SelectMenuChooseEvent
import org.javacord.api.interaction.Interaction
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
        throw IllegalStateException("La guilde des lapins de transports demande 100.0 ${Resource.RABBIT_COIN.show} pour voyager dans un autre monde")
    }
}

class MapCommand : Command(
    "map",
    "Permet de faire toutes les actions à propos des déplacements sur la carte",
), ExecutableWithArguments {
    override val fullName: String
        get() = "map"
    override val botPerms: Array<String>
        get() = arrayOf("PLAY")

    override fun execute(slashCommand: SlashCommandInteraction) {
        val context = contextFor(PlayerWithAccount(slashCommand.user))
        val ui = DiscordPlayerUI(context, slashCommand as Interaction)
        ui.setLongCustomUI(
            MenuBuilderUI(
                "Carte 🗺",
                "Se déplacer est important dans le jeu ! Visitez le monde et regardez autour de vous",
                ui
            )
                .addButton(
                    "Voyager",
                    "Permet de se déplacer de plusieurs façons sur la carte"
                ) { _ ->
                    ui.setLongCustomUI(
                        MenuBuilderUI(
                            "Voyager",
                            "Voyager est important dans ce jeu",
                            ui
                        )
                            .addButton(
                                "Mondes",
                                "Permet de changer de monde"
                            ) { _ ->
                                // Etape 1 : afficher la liste des mondes

                                // get all worlds
                                val worldEnums = WorldEnum.values()
                                val worlds = worldEnums.map { it }
                                val player = getAccount(slashCommand)

                                val builder = MenuBuilderUI(
                                    "Mondes",
                                    "Choisissez un monde. Votre monde : ${player["world"]}",
                                    ui
                                )

                                // for each world, add a field
                                for (world in worlds) {
                                    builder.addButton(
                                        world.nameRP,
                                        "**Nom officiel :** ${world.progName}\n**Type de serveur :** ${world.typeOfServer}\n${world.desc}"
                                    ) {

                                        // get the player's bal
                                        verifyBal(player)

                                        ui.setLongCustomUI(
                                            MenuBuilderUI(
                                                "Confirmer",
                                                "Confirmez-vous le voyage vers ce monde pour 100 ${Resource.RABBIT_COIN.show} ?",
                                                ui
                                            )
                                                .addButton("Oui", "Oui je veux changer de monde") { _ ->
                                                    // get the player's bal
                                                    verifyBal(player)

                                                    player.removeMoney(100.0)

                                                    player["world"] = world.progName
                                                    if (player["place_${world.progName}_x"] == "") {
                                                        player["place_${world}_type"] = "coos"
                                                        player["place_${world.progName}_x"] = world.defaultX.toString()
                                                        player["place_${world.progName}_y"] = world.defaultY.toString()
                                                    }
                                                    ui.addMessage(
                                                        Message("Vous êtes maintenant dans le monde ${world.progName}")
                                                    )
                                                    null
                                                }
                                                .addButton("Non", "Non je ne veux pas changer de monde") { _ ->
                                                    ui.addMessage(
                                                        Message("Vous avez annulé le voyage")
                                                    )
                                                    null
                                                })
                                        null
                                    }
                                }

                                ui.setLongCustomUI(builder)

                                null
                            }
                            .addButton(
                                "Aller à",
                                "Mode de déplacement le plus simple. Permet de se déplacer sur le pixel de son choix"
                            ) { _ ->

                                val player = ui.getPlayer()
                                val world = player["world"]
                                val type = player["place_${world}_type"]
                                if (type == "") {
                                    player["place_${world}_type"] = "coos"
                                } else if (type != "coos") {
                                    throw IllegalArgumentException("Vous n'êtes pas sur des coordonnées")
                                }
                                val zooms = try {
                                    Zooms.valueOf(player["place_${world}_zoom"])
                                } catch (e: IllegalArgumentException) {
                                    player["place_${world}_zoom"] = "ZOOM_OUT"
                                    Zooms.ZOOM_OUT
                                }
                                if (zooms != Zooms.ZOOM_OUT) {
                                    throw IllegalArgumentException("Vous n'êtes pas sur le zoom de base, vous ne pouvez pas utiliser cette commande actuellement. Utilisez le déplacement pixel par pixel pour dézoomer")
                                }

                                Question(
                                    "Sur quel pixel se rendre ?",
                                    QuestionField(
                                        "Le x du pixel",
                                        shortAnswer = true,
                                        required = true
                                    ),
                                    QuestionField(
                                        "Le y du pixel",
                                        shortAnswer = true,
                                        required = true
                                    )
                                ) {
                                    val opX = it.field0.answer
                                    val opY = it.field1!!.answer

                                    // str to int
                                    val x = try {
                                        opX.toInt()
                                    } catch (e: Exception) {
                                        throw IllegalArgumentException("x is not an int")
                                    }
                                    val y = try {
                                        opY.toInt()
                                    } catch (e: Exception) {
                                        throw IllegalArgumentException("y is not an int")
                                    }

                                    val player = ui.getPlayer()
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
                                        throw IllegalArgumentException("La coordonnée x ou la coordonnée y est en dehors du monde ... attention au vide ! (Le monde a une taille de ${world.mapWidth}x${world.mapHeight})")
                                    }

                                    // Etape : calcul du trajet et affichage du prix en temps ou en argent
                                    val nodePlayer = world.getNode(currentX, currentY, ArrayList())
                                    val nodeDest = world.getNode(x, y, ArrayList())
                                    //long !!
                                    smce.modalInteraction.createImmediateResponder()
                                        .setContent("Patientez un instant... calcul du trajet")


                                    val path = world.findPath(nodePlayer, nodeDest)
                                    val image = bigger(world.drawPath(path), 3)

                                    val timeMillisOnePixel = timeMillisForOnePixel(player)
                                    val timeMillisToTravel = timeMillisOnePixel * path.size
                                    val priceToTravel = priceToTravelWithEffect(player, path.size)

                                    MenuBuilderUI(
                                        "Comment voyager ?",
                                        "Il existe 2 moyens de voyager de façon simple",
                                        Color.RED,
                                        c
                                    )
                                        .setImage(image)
                                        .addButton(
                                            "Temps",
                                            "Vous allez prendre $timeMillisToTravel ms pour aller jusqu'à ce pixel. Attention : les effets peuvent ne pas fonctionner si ils ne sont pas actifs à l'arrivée."
                                        ) { _ ->
                                            MenuBuilderUI(
                                                "Confirmer",
                                                "Confirmer le voyage ?",
                                                Color.orange,
                                                c3
                                            )
                                                .addButton("Oui", "Oui je veux aller jusqu'à ce pixel") { _ ->
                                                    player.setPath(path, "default_time", timeMillisOnePixel)
                                                    it.buttonInteraction.createOriginalMessageUpdater()
                                                        .removeAllComponents()
                                                        .removeAllEmbeds()
                                                        .setContent("Vous êtes maintenant sur le trajet vers le pixel ($x, $y)")
                                                        .update()
                                                }
                                                .addButton("Non", "Non je ne veux pas aller jusqu'à ce pixel") { _ ->
                                                    it.buttonInteraction.createOriginalMessageUpdater()
                                                        .removeAllComponents()
                                                        .removeAllEmbeds()
                                                        .setContent("Vous avez annulé le voyage")
                                                        .update()
                                                }
                                            null
                                        }
                                        .addButton(
                                            "Argent",
                                            "Vous allez dépenser $priceToTravel ${Resource.RABBIT_COIN.show} pour aller jusqu'à ce pixel. Les effets sont pris en compte."
                                        ) { _ ->
                                            MenuBuilderUI(
                                                "Confirmer",
                                                "Confirmer le voyage ?",
                                                Color.orange,
                                                c3
                                            )
                                                .addButton("Oui", "Oui je veux aller jusqu'à ce pixel") { _ ->
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
                                                .addButton("Non", "Non je ne veux pas aller jusqu'à ce pixel") { _ ->
                                                    it.buttonInteraction.createOriginalMessageUpdater()
                                                        .removeAllComponents()
                                                        .removeAllEmbeds()
                                                        .setContent("Vous avez annulé le voyage")
                                                        .update()
                                                }
                                            null
                                        }
                                    null
                                }
                            }
                            .addButton(
                                "Pixel par pixel",
                                "Mode de déplacement maîtrisable."
                            ) { _ ->

                                val player = ui.getPlayer()
                                val world = player["world"]
                                val type = player["place_${world}_type"]

                                if (type == "") {
                                    player["place_${world}_type"] = "coos"
                                } else if (type != "coos") {
                                    throw IllegalArgumentException("Non disponible pour le moment dans les villes et autres lieux sans coordonnées")
                                }

                                ui.setLongCustomUI(
                                    PixelByPixelUI(
                                        ui,
                                        null
                                    )
                                )
                                null
                            }
                    )
                    null
                }
                .addButton(
                    "Retourner au hub",
                    "Une urgence ? Bloqué dans un lieu inexistant ? Retournez au hub gratuitement !"
                ) { _ ->
                    val p = getAccount(slashCommand)
                    ui.setLongCustomUI(MenuBuilderUI(
                        "Confirmation requise",
                        "Voulez-vous vraiment retourner au hub ?",
                        ui
                    )
                        .addButton("Oui", "Retourner au hub") { _ ->
                            ui.addMessage(Message("✔ Flavinou vient de vous téléporter au hub <https://discord.gg/q4hVQ6gwyx>"))
                            toSpawn(p)
                            null
                        }
                        .addButton("Non", "Annuler") { _ ->
                            ui.addMessage(Message("Annulé"))
                            null
                        }
                    )
                    null
                }
                .addButton(
                    "Cartes",
                    "Les cartes sont disponibles ici ! De nombreuses actions complémentaires sont proposées"
                ) { _ ->
                    ui.setLongCustomUI(
                        MenuBuilderUI(
                            "Cartes 🌌",
                            "Les cartes ... tellement de cartes !",
                            ui
                        )
                            .addButton(
                                "Liste des cartes",
                                "Toutes les cartes permanentes du jeu ... remerciez Darki"
                            ) { _ ->
                                val maps = arrayListOf(*FilesMapEnum.values())
                                ui.setLongCustomUI(
                                    EmbedPages(
                                        null,
                                        null,
                                        "Liste des cartes",
                                        "Toutes les cartes permanentes du jeu ... remerciez Darki",
                                        null,
                                        maps,
                                        { i: Int, i1: Int, filesMapEnums: ArrayList<FilesMapEnum> ->
                                            val pairs = ArrayList<Pair<String, String>>()
                                            for (j in i until i + i1) {
                                                val map = filesMapEnums[j]
                                                pairs.add(
                                                    Pair(
                                                        map.name,
                                                        map.description + "\n" + map.urlOfMap + "\n de : " + map.author
                                                    )
                                                )
                                            }
                                            if (pairs.size == 0) {
                                                return@EmbedPages listOf(Pair("Aucune carte", "Aucune carte"))
                                            }
                                            return@EmbedPages pairs
                                        },
                                        ui
                                    )
                                )
                                null
                            }
                            .addButton(
                                "Ma position",
                                "Toutes les informations sur votre position"
                            ) { _ ->

                                val player = getAccount(slashCommand)
                                val worldStr = player["world"]
                                val world = WorldEnum.valueOf(worldStr)
                                val position = player.positionToString()

                                val x = player["place_${worldStr}_x"]
                                val y = player["place_${worldStr}_y"]
                                val zoom = player["place_${worldStr}_zoom"]
                                val zooms = try {
                                    Zooms.valueOf(zoom)
                                } catch (e: Exception) {
                                    Zooms.ZOOM_OUT
                                }
                                val xInt = x.toInt()
                                val yInt = y.toInt()
                                val biome = if (world.isDirt(xInt, yInt)) "la terre" else "l'eau"

                                val later = buttonClickEvent.buttonInteraction.respondLater()
                                val image = world.zoomWithDecorElements(xInt, yInt, 30, zooms, player = player)

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
                            ) { _ ->

                                Question(
                                    "Trouver un chemin",
                                    QuestionField(
                                        "Le x du point de départ",
                                        shortAnswer = true,
                                        required = true
                                    ),
                                    QuestionField(
                                        "Le y du point de départ",
                                        shortAnswer = true,
                                        required = true
                                    ),
                                    QuestionField(
                                        "Le x du point d'arrivée",
                                        shortAnswer = true,
                                        required = true
                                    ),
                                    QuestionField(
                                        "Le y du point d'arrivée",
                                        shortAnswer = true,
                                        required = true
                                    )
                                ) {
                                    // get optionals text inputs from modal interaction
                                    val opX1 = it.field0.answer
                                    val opY1 = it.field1!!.answer
                                    val opX2 = it.field2!!.answer
                                    val opY2 = it.field3!!.answer

                                    // check if the strings are numbers
                                    val x1Int = try {
                                        opX1.toInt()
                                    } catch (e: NumberFormatException) {
                                        throw IllegalArgumentException("Le x du point de départ n'est pas un nombre")
                                    }
                                    val y1Int = try {
                                        opY1.toInt()
                                    } catch (e: NumberFormatException) {
                                        throw IllegalArgumentException("Le y du point de départ n'est pas un nombre")
                                    }
                                    val x2Int = try {
                                        opX2.toInt()
                                    } catch (e: NumberFormatException) {
                                        throw IllegalArgumentException("Le x du point d'arrivée n'est pas un nombre")
                                    }
                                    val y2Int = try {
                                        opY2.toInt()
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
                                    null
                                }
                            }
                            .addButton(
                                "Zoomer",
                                "Zoomer sur une carte"
                            ) { _ ->
                                Question(
                                    "Informations pour zoomer sur la carte",
                                    QuestionField(
                                        "Le x de la case",
                                        shortAnswer = true,
                                        required = true
                                    ),
                                    QuestionField(
                                        "Le y de la case",
                                        shortAnswer = true,
                                        required = true
                                    ),
                                    QuestionField(
                                        "Zoom (1-60) = hauteur/2",
                                        shortAnswer = true,
                                        required = true
                                    )
                                ) {

                                    // transform optionals to strings
                                    val x = it.field0.answer
                                    val y = it.field1!!.answer
                                    val zoomStr = it.field2!!.answer

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
                                    var zoomInt = try {
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

                                    // check if zoomInt is < 60 and > 0
                                    if (zoomInt < 1 || zoomInt > 60) {
                                        throw IllegalArgumentException("Le zoom doit être compris entre 1 et 60 (et rester dans la carte !)")
                                    }

                                    var zooms = Zooms.ZOOM_OUT
                                    if (zoomInt < 9) {
                                        when (zoomInt) {
                                            2 -> {
                                                zooms = Zooms.ZOOM_ZONES_DETAILS
                                                zoomInt = 60
                                            }

                                            1 -> {
                                                zooms = Zooms.ZOOM_IN
                                                zoomInt = 60
                                            }

                                            else -> {
                                                zooms = Zooms.ZOOM_ZONES
                                                zoomInt *= 7
                                            }
                                        }
                                    }

                                    val (xInt2, yInt2) = Zooms.ZOOM_OUT.zoomInTo(zooms, xInt, yInt)

                                    // send the zoom on the map
                                    val later = modalInteraction.respondLater()

                                    val image = world.zoomWithDecorElements(xInt2, yInt2, zoomInt, zooms)

                                    later.thenAccept {
                                        it.addEmbed(
                                            EmbedBuilder()
                                                .setTitle("Zoom sur la carte")
                                                .setImage(image)
                                        )
                                            .update()
                                    }
                                    null
                                }
                            }
                            .addButton(
                                "Type de case",
                                "Le biome d'une case et les informations"
                            ) { _ ->
                                Question(
                                    "Type de case",
                                    QuestionField(
                                        "Le x de la case",
                                        shortAnswer = true,
                                        required = true
                                    ),
                                    QuestionField(
                                        "Le y de la case",
                                        shortAnswer = true,
                                        required = true
                                    )
                                ) {

                                    // transform optionals to strings
                                    val x = try {
                                        it.field0.answer.toInt()
                                    } catch (e: NumberFormatException) {
                                        throw IllegalArgumentException("Le x de la case n'est pas un nombre")
                                    }
                                    val y = try {
                                        it.field1!!.answer.toInt()
                                    } catch (e: NumberFormatException) {
                                        throw IllegalArgumentException("Le y de la case n'est pas un nombre")
                                    }

                                    val player = ui.getPlayer()
                                    val world = player.world

                                    if (x < 0 || x > world.mapWidth) {
                                        throw IllegalArgumentException("Le x de la case n'est pas dans la carte")
                                    }
                                    if (y < 0 || y > world.mapHeight) {
                                        throw IllegalArgumentException("Le y de la case n'est pas dans la carte")
                                    }

                                    val biome = if (world.isDirt(x, y)) {
                                        "la terre"
                                    } else {
                                        "l'eau"
                                    }

                                    ui.addMessage(
                                        Message(
                                            "🌱 La case est de $biome",
                                            "Type de case de [$x:$y]"
                                        )
                                    )
                                    null
                                }
                            })
                    null
                })
        ui.updateOrSend()
        context.ui(ui)
    }
}