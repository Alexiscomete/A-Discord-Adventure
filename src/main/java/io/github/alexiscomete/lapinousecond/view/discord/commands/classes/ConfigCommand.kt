package io.github.alexiscomete.lapinousecond.view.discord.commands.classes

import io.github.alexiscomete.lapinousecond.data.managesave.generateUniqueID
import io.github.alexiscomete.lapinousecond.data.managesave.saveManager
import io.github.alexiscomete.lapinousecond.view.contextFor
import io.github.alexiscomete.lapinousecond.view.discord.commands.Command
import io.github.alexiscomete.lapinousecond.view.discord.commands.ExecutableWithArguments
import io.github.alexiscomete.lapinousecond.view.discord.commands.getAccount
import io.github.alexiscomete.lapinousecond.view.ui.longuis.EmbedPagesWithInteractions
import io.github.alexiscomete.lapinousecond.view.ui.longuis.MenuBuilderUI
import io.github.alexiscomete.lapinousecond.view.ui.playerui.*
import io.github.alexiscomete.lapinousecond.worlds.*
import io.github.alexiscomete.lapinousecond.worlds.dibimap.checkById
import io.github.alexiscomete.lapinousecond.worlds.dibimap.getValueById
import io.github.alexiscomete.lapinousecond.worlds.dibimap.isDibimap
import org.javacord.api.entity.permission.PermissionType
import org.javacord.api.entity.server.Server
import org.javacord.api.interaction.Interaction
import org.javacord.api.interaction.SlashCommandInteraction
import java.util.*

const val TUTO_CITY_X = "45"
const val TUTO_CITY_Y = "20"

fun configNormalServer(world: WorldEnum, server: Server, placeId: Long) {
    servers.add(server.id)
    val serverC = servers[server.id]
        ?: throw IllegalArgumentException("Un problème de source inconnue est survenue. La création du serveur a échoué.")
    serverC["world"] = world.progName
    serverC["name"] = server.name
    serverC["places"] = placeId.toString()
}

fun getUniquePlace(server: ServerBot): Place {
    val placesL = server.getPlaces()
    if (placesL.size != 1) {
        throw IllegalArgumentException("Le serveur n'a pas un lieu unique. Contactez un administrateur.")
    }
    return places[placesL[0]] ?: throw IllegalArgumentException("Le lieu n'existe pas. Contactez un administrateur.")
}

class ConfigCommand : Command(
    "config",
    "Permet de configurer le serveur discord et les lieux associés à celui-ci",
    inDms = false,
    discordPerms = EnumSet.of(PermissionType.MANAGE_CHANNELS)
), ExecutableWithArguments {
    override val fullName: String
        get() = "config"
    override val botPerms: Array<String>
        get() = arrayOf("CREATE_SERVER")

    private fun createPlace(
        question: Question, world: WorldEnum, serverId: Long, playerUI: PlayerUI, server: Server
    ) {
        val placeId = generateUniqueID()
        places.add(placeId)
        val place = places[placeId]
            ?: throw IllegalArgumentException("Un problème de source inconnue est survenue. La création du serveur a échoué.")

        place["nameRP"] = question.field0.answer
        place["description"] = question.field1!!.answer
        place["welcome"] = question.field2!!.answer

        // génération aléatoire des coordonnées du lieu entre 1 et le maximum du lieu
        var x: Int
        var y: Int
        do {
            x = (1..world.mapWidth).random()
            y = (1..world.mapHeight).random()
        } while (world.isDirt(x, y))

        place["x"] = x.toString()
        place["y"] = y.toString()

        place["type"] = "city" // automatique normalement
        place["world"] = world.progName // automatique normalement
        place["server"] = serverId.toString() // automatique normalement

        configNormalServer(world, server, placeId)

        playerUI.addMessage(
            Message(
                "Le serveur a été configuré avec succès ! Les coordonnées sont [${x}:${y}]"
            )
        )
    }

    private fun addPlace1(serverId: Long, pui: PlayerUI, question: Question) {

        // création d'un bouton pour continuer
        pui.setLongCustomUI(
            MenuBuilderUI(
                "Discord n'autorise pas l'enchainement des entrées de texte", "Donc cliquez sur ce", pui
            ).addButton(
                "Bouton", "pour continuer"
            ) {
                // partie 2 du modal avec x et y, j'utilise à nouveau idNameRP (pour x); idDescription (pour y)
                Question(
                    "Ajout d'une ville (2/2)",
                    QuestionField(
                        "Coordonnée X",
                        shortAnswer = true,
                        required = true
                    ),
                    QuestionField(
                        "Coordonnée Y",
                        shortAnswer = true,
                        required = true
                    )
                ) {
                    addPlace2(serverId, pui, question, it)
                    null
                }
            }
        )
    }

    private fun addPlace2(serverId: Long, pui: PlayerUI, question0: Question, question1: Question) {
        // récupération de tous les éléments : description, nameRP, welcome, x, y
        val opX = question1.field0.answer
        val opY = question1.field1!!.answer

        // on récupère les éléments
        val nameRP = question0.field0.answer
        val description = question0.field1!!.answer
        val welcome = question0.field2!!.answer

        // on vérifie que les coordonnées sont bien des nombres
        try {
            opX.toInt()
            opY.toInt()
        } catch (e: NumberFormatException) {
            throw IllegalArgumentException("Les coordonnées doivent être des nombres !")
        }

        if (!getValueById(serverId).isInZones(opX.toInt(), opY.toInt())) {
            throw IllegalArgumentException("Les coordonnées ne sont pas dans les zones autorisées pour votre entité !")
        }

        // TODO : vérifier l'existence de la ville dans les villes du lore officiel

        if (saveManager.hasResult("SELECT * FROM places WHERE nameRP = '$nameRP' OR x = '$opX' AND y = '$opY'")) {
            throw IllegalArgumentException("Une ville existe déjà à ces coordonnées ou avec ce nom !")
        }

        val id = generateUniqueID()
        // création de la ville, on réutilise encore id
        places.add(id)
        val place = places[id]
            ?: throw IllegalArgumentException("Un problème de source inconnue est survenue. La création de la ville a échoué.")
        place["nameRP"] = nameRP
        place["description"] = description
        place["welcome"] = welcome
        place["x"] = opX
        place["y"] = opY
        place["server"] = serverId.toString()
        place["type"] = "city"
        place["world"] = WorldEnum.DIBIMAP.progName

        servers[serverId]!!.addPlace(id)

        // on envoie un message de succès
        pui.addMessage(
            Message(
                "La ville a été créée avec succès !"
            )
        )
    }

    private fun fieldModificationAnswer(
        question: Question, place: Place, col: String, playerUI: PlayerUI
    ) {
        place[col] = question.field0.answer
        playerUI.addMessage(Message("Modifié avec succès !"))
    }

    override fun execute(slashCommand: SlashCommandInteraction) {
        SERVERS
        PLACES

        val serverId = slashCommand.server.get().id
        val server = servers[serverId]
        val context = contextFor(getAccount(slashCommand.user))
        val serverD = slashCommand.server.get()
        val ui = DiscordPlayerUI(context, slashCommand as Interaction)

        if (server == null) {
            val world =
                if (serverId == MAIN_SERVER_ID) WorldEnum.TUTO else if (isDibimap(serverId)) WorldEnum.DIBIMAP else WorldEnum.NORMAL
            ui.setLongCustomUI(MenuBuilderUI(
                "Votre première configuration",
                "Votre serveur discord a été automatiquement assigné au ${world.nameRP}. Explications :\nLe Dibistan a un drapeau qui est aussi son territoire principal. Si votre serveur discord est un État ou une région qui a un territoire en forme de polygone sur le drapeau, alors son monde est le ${WorldEnum.DIBIMAP.nameRP} sinon c'est le monde ${WorldEnum.NORMAL.nameRP}. Les mécaniques sont différentes dans les 2 mondes. **Le monde détecté est-il correct ?**",
                ui
            ).addButton(
                "Oui", "Le monde est correct et je continue la configuration. **Irréversible**"
            ) { playerUI ->

                when (world) {
                    WorldEnum.NORMAL -> {
                        return@addButton Question(
                            "Configuration de la ville du serveur", QuestionField(
                                "Nom de la ville", shortAnswer = true, required = true
                            ), QuestionField(
                                "Description de la ville", shortAnswer = false, required = true
                            ), QuestionField(
                                "Message de bienvenue", shortAnswer = false, required = true
                            )
                        ) {
                            createPlace(
                                it, world, serverId, playerUI, serverD
                            )
                            null
                        }
                    }

                    WorldEnum.DIBIMAP -> {

                        servers.add(serverId)
                        val serverC = servers[serverId]
                            ?: throw IllegalArgumentException("Un problème de source inconnue est survenue. La création du serveur a échoué.")
                        serverC["world"] = world.progName
                        serverC["name"] = serverD.name

                        val serverForZones = getValueById(serverId)

                        playerUI.addMessage(Message("Le serveur a été configuré avec succès ! Vous devez faire à nouveau la commande pour ajouter des villes. Les $serverForZones ont été ajoutées automatiquement."))
                    }

                    WorldEnum.TUTO -> {
                        val id = generateUniqueID()
                        places.add(id)
                        val place = places[id]
                            ?: throw IllegalArgumentException("Un problème de source inconnue est survenue. La création du serveur a échoué.")
                        place["nameRP"] = "Saint-Lapin-sur-bot"
                        place["description"] = "Ville accueillante du tutoriel"
                        place["welcome"] = "Ne restez pas trop longtemps ici et profitez de l'aventure"
                        place["x"] = TUTO_CITY_X
                        place["y"] = TUTO_CITY_Y
                        place["type"] = "city"
                        place["world"] = world.progName
                        place["server"] = serverId.toString()

                        configNormalServer(world, serverD, id)

                        playerUI.addMessage(Message("Le serveur a été configuré avec succès !"))
                    }
                }
                null
            }.addButton(
                "Non", "Le monde est incorrect ou je veux changer quelque chose. **Réversible**"
            ) { playerUI ->
                playerUI.addMessage(Message("Contactez un administrateur pour changer le monde si c'est le problème"))
                null
            })
        } else {
            when (WorldEnum.valueOf(server["world"])) {
                WorldEnum.NORMAL -> {
                    modifServer(server, serverD, ui)
                }

                WorldEnum.DIBIMAP -> {
                    checkById(serverId)

                    fun fillEmbed(start: Int, num: Int, placesArray: ArrayList<Place>): List<Pair<String, String>> {
                        return (start until start + num).map { i ->
                            val place = placesArray[i]
                            Pair(
                                place["nameRP"], "Coordonnées : ${place["x"]}, ${place["y"]}"
                            )
                        }
                    }

                    ui.setLongCustomUI(
                        MenuBuilderUI(
                            "Configuration du serveur", "Configurer votre serveur discord d'entité territorial", ui
                        ).addButton(
                            "Mise à jour du nom",
                            "Le nom du serveur discord est stocké dans la base de données. Mais si vous changer le nom du serveur discord le bot ne met pas à jour automatiquement de son côté."
                        ) { playerUI ->
                            server["name"] = serverD.name
                            playerUI.addMessage(Message("Le nom du serveur a été mis à jour avec succès !"))
                            null
                        }.addButton("Ajouter une ville", "Permet d'ajouter une ville sur la carte") { playerUI ->
                            // j'ai besoin d'un nom, d'une description, d'un message de bienvenue, et de x et y. Les modals sont limités à 4 champs donc je vais faire 2 modals

                            Question(
                                "Ajout d'une ville (1/2)", QuestionField(
                                    "Nom RP de la ville", shortAnswer = true, required = true
                                ), QuestionField(
                                    "Description de la ville", shortAnswer = false, required = true
                                ), QuestionField(
                                    "Message de bienvenue", shortAnswer = false, required = true
                                )
                            ) {
                                addPlace1(serverId, playerUI, it)
                                null
                            }
                        }.addButton(
                            "Supprimer une ville",
                            "Permet de supprimer une ville sur la carte si elle n'est pas utilisée pour le lore"
                        ) {
                            /**
                             * Etapes :
                             * 1. Récupérer la liste des villes
                             * 2. Afficher la liste des villes au joueur dans un embed avec interactions
                             * 3. Récupérer la ville sélectionnée
                             * 4. Supprimer la ville
                             * 5. Envoyer un message de succès
                             */

                            // 1. Récupérer la liste des villes
                            val placesLong = server.getPlaces()

                            val placesPlace = arrayListOf(*placesLong.map { places[it]!! }.toTypedArray())

                            // 2. Afficher la liste des villes au joueur dans un embed avec interactions
                            ui.setLongCustomUI(
                                EmbedPagesWithInteractions(
                                    placesPlace, ::fillEmbed, { place: Place, playerUI: PlayerUI ->
                                        // 3. Récupérer la ville sélectionnée
                                        // 4. Supprimer la ville
                                        server.removePlace(place.id)
                                        saveManager.execute("DELETE FROM places WHERE id = ${place.id}")
                                        // TODO : supprimer la ville côté joueur et le tp
                                        // 5. Envoyer un message de succès
                                        playerUI.addMessage(Message("La ville a été supprimée avec succès !"))
                                        return@EmbedPagesWithInteractions null
                                    }, null, null, "Liste des villes", "Sélectionnez une ville à supprimer", ui
                                )
                            )
                            null
                        }.addButton("Modifier une ville", "Permet de modifier une ville sur la carte") { playerUI ->
                            /**
                             * Etapes :
                             * 1. Récupérer la liste des villes
                             * 2. Afficher la liste des villes au joueur dans un embed avec interactions
                             * 3. Récupérer la ville sélectionnée
                             * 4. Afficher les options de modification
                             * 5. Modifier la ville
                             */

                            // 1. Récupérer la liste des villes
                            val placesLong = server.getPlaces()

                            val placesPlace = arrayListOf(*placesLong.map { places[it]!! }.toTypedArray())

                            // 2. Afficher la liste des villes au joueur dans un embed avec interactions
                            ui.setLongCustomUI(
                                EmbedPagesWithInteractions(
                                    placesPlace, ::fillEmbed, { place: Place, _: PlayerUI ->
                                        // 3. Récupérer la ville sélectionnée
                                        // 4. Afficher les options de modification

                                        playerUI.setLongCustomUI(
                                            MenuBuilderUI(
                                                "Modification de la ville ${place["nameRP"]}",
                                                "Sélectionnez ce qu'il faut modifier :",
                                                ui
                                            ).addButton(
                                                "Modifier le nom RP du lieu",
                                                "Modifiable à tout moment, le nom de votre ville est personnalisable."
                                            ) { _ ->
                                                Question(
                                                    "Mise à jour du nom RP de la ville", QuestionField(
                                                        "Nom RP de la ville", shortAnswer = true, required = true
                                                    )
                                                ) {
                                                    fieldModificationAnswer(
                                                        it, place, "nameRP", ui
                                                    )
                                                    null
                                                }
                                            }.addButton(
                                                "Modifier la description du lieu",
                                                "Modifiable à tout moment, la description de votre ville est la deuxième chose que voix une personne quand il regarde le lieu."
                                            ) { _ ->
                                                Question(
                                                    "Mise à jour de la description de la ville", QuestionField(
                                                        "Description de la ville", shortAnswer = false, required = true
                                                    )
                                                ) {
                                                    fieldModificationAnswer(
                                                        it, place, "description", ui
                                                    )
                                                    null
                                                }
                                            }.addButton(
                                                "Modifier le message de bienvenue",
                                                "Modifiable à tout moment, le message de bienvenue est nécessaire pour mettre l'ambiance : ville magique ? Tech ? Abandonné ? Repaire de Pirates ?"
                                            ) { _ ->
                                                Question(
                                                    "Mise à jour du message de bienvenue", QuestionField(
                                                        "Message de bienvenue", shortAnswer = false, required = true
                                                    )
                                                ) {
                                                    fieldModificationAnswer(
                                                        it, place, "welcome", ui
                                                    )
                                                    null
                                                }
                                            }
                                        )
                                        null
                                    }, null, null, "Liste des villes", "Sélectionnez une ville à modifier", ui
                                )
                            )
                            null
                        })
                }

                WorldEnum.TUTO -> {
                    modifServer(server, serverD, ui)
                }
            }
        }
        ui.updateOrSend()
        context.ui(ui)
    }

    private fun modifServer(
        server: ServerBot, serverDiscord: Server, ui: PlayerUI
    ) {
        ui.setLongCustomUI(
            MenuBuilderUI(
                "Modification du serveur dans un monde à lieu unique",
                "Vous pouvez modifier la configuration du serveur discord de façon simple dans un monde à serveur unique. Sélectionner ce qu'il faut modifier :",
                ui
            ).addButton(
                "Mise à jour du nom du serveur",
                "Le nom du serveur discord est stocké dans la base de données. Mais si vous changer le nom du serveur discord le bot ne met pas à jour automatiquement de son côté."
            ) { pui ->
                server["name"] = serverDiscord.name
                pui.addMessage(Message("Le nom du serveur a été mis à jour avec succès !"))
                null
            }.addButton(
                "Modifier le nom RP du lieu", "Modifiable à tout moment, le nom de votre ville est personnalisable."
            ) {
                Question(
                    "Mise à jour du nom RP de la ville", QuestionField(
                        "Nom RP de la ville", shortAnswer = true, required = true
                    )
                ) {
                    fieldModificationAnswer(
                        it, getUniquePlace(server), "nameRP", ui
                    )
                    null
                }
            }.addButton(
                "Modifier la description du lieu",
                "Modifiable à tout moment, la description de votre ville est la deuxième chose que voix une personne quand il regarde le lieu."
            ) {
                Question(
                    "Mise à jour de la description de la ville", QuestionField(
                        "Description de la ville", shortAnswer = false, required = true
                    )
                ) {
                    fieldModificationAnswer(
                        it, getUniquePlace(server), "description", ui
                    )
                    null
                }
            }.addButton(
                "Modifier le message de bienvenue",
                "Modifiable à tout moment, le message de bienvenue est nécessaire pour mettre l'ambiance : ville magique ? Tech ? Abandonné ? Repaire de Pirates ?"
            ) {
                Question(
                    "Mise à jour du message de bienvenue", QuestionField(
                        "Message de bienvenue", shortAnswer = false, required = true
                    )
                ) {
                    fieldModificationAnswer(
                        it, getUniquePlace(server), "welcome", ui
                    )
                    null
                }
            }
        )
    }
}