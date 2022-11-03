package io.github.alexiscomete.lapinousecond.commands.withslash.classes

import io.github.alexiscomete.lapinousecond.commands.withslash.Command
import io.github.alexiscomete.lapinousecond.commands.withslash.ExecutableWithArguments
import io.github.alexiscomete.lapinousecond.commands.withslash.getAccount
import io.github.alexiscomete.lapinousecond.view.ui.EmbedPagesWithInteractions
import io.github.alexiscomete.lapinousecond.view.ui.MenuBuilder
import io.github.alexiscomete.lapinousecond.useful.managesave.generateUniqueID
import io.github.alexiscomete.lapinousecond.useful.managesave.saveManager
import io.github.alexiscomete.lapinousecond.view.Context
import io.github.alexiscomete.lapinousecond.view.contextFor
import io.github.alexiscomete.lapinousecond.view.contextmanager.ModalContextManager
import io.github.alexiscomete.lapinousecond.worlds.*
import io.github.alexiscomete.lapinousecond.worlds.dibimap.getValueById
import io.github.alexiscomete.lapinousecond.worlds.dibimap.isDibimap
import org.javacord.api.entity.message.component.ActionRow
import org.javacord.api.entity.message.component.TextInput
import org.javacord.api.entity.message.component.TextInputStyle
import org.javacord.api.entity.message.embed.EmbedBuilder
import org.javacord.api.entity.permission.PermissionType
import org.javacord.api.entity.server.Server
import org.javacord.api.event.interaction.ButtonClickEvent
import org.javacord.api.event.interaction.ModalSubmitEvent
import org.javacord.api.interaction.SlashCommandInteraction
import java.awt.Color
import java.util.*
import kotlin.collections.ArrayList

fun configNormalServer(world: WorldEnum, server: Server, placeId: Long) {
    servers.add(server.id)
    val serverC = servers[server.id]
        ?: throw IllegalArgumentException("Un problème de source inconnue est survenue. La création du serveur a échoué.")
    serverC["world"] = world.progName
    serverC["name"] = server.name
    serverC["places"] = placeId.toString()
}

class ConfigCommand : Command(
    "config",
    "Permet de configurer le serveur discord et les lieux associés à celui-ci",
    "config",
    inDms = false,
    discordPerms = EnumSet.of(PermissionType.MANAGE_CHANNELS)
), ExecutableWithArguments {
    override val fullName: String
        get() = "config"
    override val botPerms: Array<String>
        get() = arrayOf("CREATE_SERVER")

    class M1(
        name: String,
        val idNameRP: String,
        val idDescription: String,
        val idWelcome: String,
        val world: WorldEnum,
        val serverId: Long
    ) : ModalContextManager(name) {
        override fun ex(smce: ModalSubmitEvent, c: Context) {
            val opNameRp = smce.modalInteraction.getTextInputValueByCustomId(idNameRP)
            val opDescription =
                smce.modalInteraction.getTextInputValueByCustomId(idDescription)
            val opWelcome = smce.modalInteraction.getTextInputValueByCustomId(idWelcome)

            if (!opNameRp.isPresent || !opDescription.isPresent || !opWelcome.isPresent) {
                throw IllegalArgumentException("Un des champs n'a pas été rempli")
            }

            val placeId = generateUniqueID()
            places.add(placeId)
            val place = places[placeId]
                ?: throw IllegalArgumentException("Un problème de source inconnue est survenue. La création du serveur a échoué.")

            place["nameRP"] = opNameRp.get()
            place["description"] = opDescription.get()
            place["welcome"] = opWelcome.get()

            // génération aléatoire des coordonnées du lieu entre 1 et le maximum du lieu
            var x: Int
            var y: Int
            do {
                y = (1..world.mapWidth).random()
                x = (1..world.mapHeight).random()
            } while (world.isDirt(x, y))

            place["x"] = x.toString()
            place["y"] = y.toString()

            place["type"] = "city" // automatique normalement
            place["world"] = world.progName // automatique normalement
            place["server"] = serverId.toString() // automatique normalement

            configNormalServer(world, smce.modalInteraction.server.get(), placeId)

            smce.modalInteraction.createImmediateResponder()
                .setContent("Le serveur a été configuré avec succès ! Les coordonnées sont [${x}:${y}]")
                .respond()
        }
    }

    class M2(name: String, private val idNameRP: String, private val idDescription: String) : ModalContextManager(name) {
        override fun ex(smce: ModalSubmitEvent, c: Context) {

            // création d'un bouton pour continuer
            MenuBuilder(
                "Discord n'autorise pas l'enchainement des entrées de texte",
                "Donc cliquez sur ce",
                Color.GREEN,
                c
            )
                .addButton(
                    "Bouton",
                    "pour continuer"
                ) { button, c2, b2 ->
                    // partie 2 du modal avec x et y, j'utilise à nouveau idNameRP (pour x); idDescription (pour y)
                    val id2 = generateUniqueID()

                    button.buttonInteraction.respondWithModal(
                        id2.toString(),
                        "Ajout d'une ville (2/2)",
                        ActionRow.of(
                            TextInput.create(
                                TextInputStyle.SHORT,
                                idNameRP,
                                "Coordonnée X",
                                true
                            )
                        ),
                        ActionRow.of(
                            TextInput.create(
                                TextInputStyle.SHORT,
                                idDescription,
                                "Coordonnée Y",
                                true
                            )
                        )
                    )

                    c.modal(M3(id2.toString()))
                }
                .responder(smce.modalInteraction)
        }
    }

    class M3(name: String) : ModalContextManager(name) {
        override fun ex(smce: ModalSubmitEvent, c: Context) {
            // récupération de tous les éléments : description, nameRP, welcome, x, y
            val opNameRP =
                modalPart1.modalInteraction.getTextInputValueByCustomId(idNameRP.toString())
            val opDescription =
                modalPart1.modalInteraction.getTextInputValueByCustomId(idDescription.toString())
            val opWelcome =
                modalPart1.modalInteraction.getTextInputValueByCustomId(idWelcome.toString())
            val opX =
                it.modalInteraction.getTextInputValueByCustomId(idNameRP.toString())
            val opY =
                it.modalInteraction.getTextInputValueByCustomId(idDescription.toString())

            // s'il manque un élément, on annule
            if (!opNameRP.isPresent || !opDescription.isPresent || !opWelcome.isPresent || !opX.isPresent || !opY.isPresent) {
                throw IllegalArgumentException("Vous avez oublié un élément !")
            }

            // on récupère les éléments
            val nameRP = opNameRP.get()
            val description = opDescription.get()
            val welcome = opWelcome.get()
            val x = opX.get()
            val y = opY.get()

            // on vérifie que les coordonnées sont bien des nombres
            try {
                x.toInt()
                y.toInt()
            } catch (e: NumberFormatException) {
                throw IllegalArgumentException("Les coordonnées doivent être des nombres !")
            }

            if (!serverForZones.isInZones(x.toInt(), y.toInt())) {
                throw IllegalArgumentException("Les coordonnées ne sont pas dans les zones autorisées pour votre entité !")
            }

            // TODO : vérifier l'existence de la ville dans les villes du lore officiel

            if (saveManager.hasResult("SELECT * FROM places WHERE nameRP = '$nameRP' OR x = '$x' AND y = '$y'")) {
                throw IllegalArgumentException("Une ville existe déjà à ces coordonnées ou avec ce nom !")
            }

            // création de la ville, on réutilise encore id
            places.add(id)
            val place = places[id]
                ?: throw IllegalArgumentException("Un problème de source inconnue est survenue. La création de la ville a échoué.")
            place["nameRP"] = nameRP
            place["description"] = description
            place["welcome"] = welcome
            place["x"] = x
            place["y"] = y
            place["server"] = serverId.toString()
            place["type"] = "city"
            place["world"] = WorldEnum.DIBIMAP.progName

            server.addPlace(id)

            // on envoie un message de succès
            it.modalInteraction.createImmediateResponder()
                .setContent("La ville a été créée avec succès !")
                .respond()
        }
    }

    override fun execute(slashCommand: SlashCommandInteraction) {
        SERVERS
        PLACES

        val serverId = slashCommand.server.get().id
        val server = servers[serverId]
        val context = contextFor(getAccount(slashCommand.user))

        if (server == null) {
            val world =
                if (serverId == 854288660147994634) WorldEnum.TUTO else if (isDibimap(serverId)) WorldEnum.DIBIMAP else WorldEnum.NORMAL
            MenuBuilder(
                "Votre première configuration",
                "Votre serveur discord a été automatiquement assigné au ${world.nameRP}. Explications :\nLe Dibistan a un drapeau qui est aussi son territoire principal. Si votre serveur discord est un État ou une région qui a un territoire en forme de polygone sur le drapeau, alors son monde est le ${WorldEnum.DIBIMAP.nameRP} sinon c'est le monde ${WorldEnum.NORMAL.nameRP}. Les mécaniques sont différentes dans les 2 mondes. **Le monde détecté est-il correct ?**",
                Color.BLUE,
                context
            )
                .addButton(
                    "Oui",
                    "Le monde est correcte et je continue la configuration. **Irréversible**"
                ) { yes, c1, _ ->

                    when (world) {
                        WorldEnum.NORMAL -> {
                            println("Normal")
                            val id = generateUniqueID()
                            val idNameRP = generateUniqueID()
                            val idDescription = generateUniqueID()
                            val idWelcome = generateUniqueID()

                            yes.buttonInteraction.respondWithModal(
                                id.toString(),
                                "Configuration de la ville du serveur",
                                ActionRow.of(
                                    TextInput.create(
                                        TextInputStyle.SHORT,
                                        idNameRP.toString(),
                                        "Nom de la ville",
                                        true
                                    )
                                ),
                                ActionRow.of(
                                    TextInput.create(
                                        TextInputStyle.PARAGRAPH,
                                        idDescription.toString(),
                                        "Description de la ville",
                                        true
                                    )
                                ),
                                ActionRow.of(
                                    TextInput.create(
                                        TextInputStyle.PARAGRAPH,
                                        idWelcome.toString(),
                                        "Message de bienvenue",
                                        true
                                    )
                                )
                            )

                            c1.modal(
                                M1(
                                    id.toString(),
                                    idNameRP.toString(),
                                    idDescription.toString(),
                                    idWelcome.toString(),
                                    world,
                                    serverId
                                )
                            )
                        }

                        WorldEnum.DIBIMAP -> {

                            servers.add(serverId)
                            val serverC = servers[serverId]
                                ?: throw IllegalArgumentException("Un problème de source inconnue est survenue. La création du serveur a échoué.")
                            serverC["world"] = world.progName
                            serverC["name"] = slashCommand.server.get().name

                            val serverForZones = getValueById(serverId)

                            yes.buttonInteraction.createImmediateResponder()
                                .setContent("Le serveur a été configuré avec succès ! Vous devez faire à nouveau la commande pour ajouter des villes. Les $serverForZones ont été ajoutées automatiquement.")
                                .respond()
                        }

                        WorldEnum.TUTO -> {
                            val id = generateUniqueID()
                            places.add(id)
                            val place = places[id]
                                ?: throw IllegalArgumentException("Un problème de source inconnue est survenue. La création du serveur a échoué.")
                            place["nameRP"] = "Saint-Lapin-sur-bot" // à Demander
                            place["description"] = "Ville accueillante du tutoriel" // à Demander
                            place["welcome"] =
                                "Ne restez pas trop longtemps ici et profitez de l'aventure" // à Demander
                            place["x"] = 45.toString() // automatique normalement : aléatoire
                            place["y"] = 20.toString() // automatique normalement : aléatoire
                            place["type"] = "city" // automatique normalement
                            place["world"] = world.progName // automatique normalement
                            place["server"] = serverId.toString() // automatique normalement

                            configNormalServer(world, slashCommand.server.get(), id)

                            yes.buttonInteraction.createImmediateResponder()
                                .setContent("Le serveur a été configuré avec succès !")
                                .respond()
                        }
                    }

                }
                .addButton(
                    "Non",
                    "Le monde est incorrecte ou je veux changer quelque chose. **Réversible**"
                ) { it, _, _ ->
                    it.buttonInteraction.createOriginalMessageUpdater()
                        .setContent("Contactez un administrateur pour changer le monde si c'est le problème")
                        .removeAllEmbeds()
                        .removeAllComponents()
                        .update()
                }
                .responder(slashCommand)
        } else {
            when (WorldEnum.valueOf(server["world"])) {
                WorldEnum.NORMAL -> {
                    modifServer(server, context, slashCommand.server.get())
                }

                WorldEnum.DIBIMAP -> {
                    val serverForZones = getValueById(serverId)

                    fun fillEmbed(builder: EmbedBuilder, start: Int, num: Int, placesArray: ArrayList<Place>) {
                        for (i in start until start + num) {
                            val place = placesArray[i]
                            builder.addField(
                                place["nameRP"],
                                "Coordonnées : ${place["x"]}, ${place["y"]}",
                                true
                            )
                        }
                    }

                    MenuBuilder(
                        "Configuration du serveur",
                        "Configurer votre serveur discord d'entité territorial",
                        Color.BLUE,
                        context
                    )
                        .addButton(
                            "Mise à jour du nom",
                            "Le nom du serveur discord est stocké dans la base de données. Mais si vous changer le nom du serveur discord le bot ne met pas à jour automatiquement de son côté."
                        ) { name, _, _ ->
                            val serverDiscord = slashCommand.server.get()
                            server["name"] = serverDiscord.name
                            name.buttonInteraction.createImmediateResponder()
                                .setContent("Le nom du serveur a été mis à jour avec succès !")
                                .respond()
                        }
                        .addButton("Ajouter une ville", "Permet d'ajouter une ville sur la carte") { addCity, c1, b1 ->
                            // j'ai besoin d'un nom, d'une description, d'un message de bienvenue, et de x et y. Les modals sont limités à 4 champs donc je vais faire 2 modals
                            val id = generateUniqueID()
                            val idNameRP = generateUniqueID()
                            val idDescription = generateUniqueID()
                            val idWelcome = generateUniqueID()

                            addCity.buttonInteraction.respondWithModal(
                                id.toString(),
                                "Ajout d'une ville (1/2)",
                                ActionRow.of(
                                    TextInput.create(
                                        TextInputStyle.SHORT,
                                        idNameRP.toString(),
                                        "Nom RP de la ville",
                                        true
                                    )
                                ),
                                ActionRow.of(
                                    TextInput.create(
                                        TextInputStyle.PARAGRAPH,
                                        idDescription.toString(),
                                        "Description de la ville",
                                        true
                                    )
                                ),
                                ActionRow.of(
                                    TextInput.create(
                                        TextInputStyle.PARAGRAPH,
                                        idWelcome.toString(),
                                        "Message de bienvenue",
                                        true
                                    )
                                )
                            )

                            c1.modal(
                                M2(
                                    id.toString(),
                                    idNameRP.toString(),
                                    idDescription.toString(),
                                )
                            )
                        }
                        .addButton(
                            "Supprimer une ville",
                            "Permet de supprimer une ville sur la carte si elle n'est pas utilisée pour le lore"
                        ) { remove, c1, _ ->
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
                            val embedBuilder = EmbedBuilder()
                                .setTitle("Liste des villes")
                                .setDescription("Sélectionnez une ville à supprimer")
                                .setColor(Color.GREEN)

                            val em = EmbedPagesWithInteractions(
                                embedBuilder,
                                placesPlace,
                                ::fillEmbed,
                                c1
                            ) { place: Place, buttonClickEvent: ButtonClickEvent, c2 ->
                                // 3. Récupérer la ville sélectionnée
                                // 4. Supprimer la ville
                                server.removePlace(place.id)
                                saveManager.execute("DELETE FROM places WHERE id = ${place.id}")
                                // TODO : supprimer la ville côté joueur et le tp
                                // 5. Envoyer un message de succès
                                buttonClickEvent.buttonInteraction.createOriginalMessageUpdater()
                                    .setContent("La ville a été supprimée avec succès !")
                                    .removeAllEmbeds()
                                    .removeAllComponents()
                                    .update()
                            }

                            em.register()
                            remove.buttonInteraction.createOriginalMessageUpdater()
                                .removeAllEmbeds()
                                .removeAllComponents()
                                .addComponents(em.components, ActionRow.of(em.buttons))
                                .addEmbed(embedBuilder)
                                .update()
                        }
                        .addButton("Modifier une ville", "Permet de modifier une ville sur la carte") { city, c1, _ ->
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
                            val embedBuilder = EmbedBuilder()
                                .setTitle("Liste des villes")
                                .setDescription("Sélectionnez une ville à modifier")
                                .setColor(Color.GREEN)

                            val em = EmbedPagesWithInteractions(
                                embedBuilder,
                                placesPlace,
                                ::fillEmbed,
                                c1
                            ) { place: Place, buttonClickEvent: ButtonClickEvent, c2 ->
                                // 3. Récupérer la ville sélectionnée
                                // 4. Afficher les options de modification

                                MenuBuilder(
                                    "Modification de la ville ${place["nameRP"]}",
                                    "Sélectionner ce qu'il faut modifier :",
                                    Color.YELLOW,
                                    c2
                                )
                                    .addButton(
                                        "Modifier le nom RP du lieu",
                                        "Modifiable à tout moment, le nom de votre ville est personnalisable."
                                    ) { name, c3, b3 ->
                                        val id = generateUniqueID()
                                        val idName = generateUniqueID()

                                        modalManager.add(id) {
                                            val opName =
                                                it.modalInteraction.getTextInputValueByCustomId(idName.toString())

                                            if (!opName.isPresent) {
                                                throw IllegalArgumentException("Le nom n'a pas été rempli")
                                            }

                                            place["nameRP"] = opName.get()

                                            it.modalInteraction.createImmediateResponder()
                                                .setContent("Le nom RP de la ville a été modifié avec succès !")
                                                .respond()
                                        }

                                        name.buttonInteraction.respondWithModal(
                                            id.toString(),
                                            "Mise à jour du nom RP de la ville",
                                            ActionRow.of(
                                                TextInput.create(
                                                    TextInputStyle.SHORT,
                                                    idName.toString(),
                                                    "Nom de la ville",
                                                    true
                                                )
                                            )
                                        )
                                    }
                                    .addButton(
                                        "Modifier la description du lieu",
                                        "Modifiable à tout moment, la description de votre ville est la deuxième chose que voix une personne quand il regarde le lieu."
                                    ) { description, c2, b2 ->
                                        val id = generateUniqueID()
                                        val idDescription = generateUniqueID()

                                        modalManager.add(id) {
                                            val opDescription =
                                                it.modalInteraction.getTextInputValueByCustomId(idDescription.toString())

                                            if (!opDescription.isPresent) {
                                                throw IllegalArgumentException("La description n'a pas été remplie")
                                            }

                                            place["description"] = opDescription.get()

                                            it.modalInteraction.createImmediateResponder()
                                                .setContent("La description de la ville a été modifiée avec succès !")
                                                .respond()
                                        }

                                        description.buttonInteraction.respondWithModal(
                                            id.toString(),
                                            "Mise à jour de la description de la ville",
                                            ActionRow.of(
                                                TextInput.create(
                                                    TextInputStyle.PARAGRAPH,
                                                    idDescription.toString(),
                                                    "Description de la ville",
                                                    true
                                                )
                                            )
                                        )
                                    }
                                    .addButton(
                                        "Modifier le message de bienvenue",
                                        "Modifiable à tout moment, le message de bienvenue est nécessaire pour mettre l'ambiance : ville magique ? Tech ? Abandonné ? Repaire de Pirates ?"
                                    ) { welcome, c2, b2 ->
                                        val id = generateUniqueID()
                                        val idWelcome = generateUniqueID()

                                        modalManager.add(id) {
                                            val opWelcome =
                                                it.modalInteraction.getTextInputValueByCustomId(idWelcome.toString())

                                            if (!opWelcome.isPresent) {
                                                throw IllegalArgumentException("Le message de bienvenue n'a pas été rempli")
                                            }

                                            place["welcome"] = opWelcome.get()

                                            it.modalInteraction.createImmediateResponder()
                                                .setContent("Le message de bienvenue a été modifié avec succès !")
                                                .respond()
                                        }

                                        welcome.buttonInteraction.respondWithModal(
                                            id.toString(),
                                            "Mise à jour du message de bienvenue",
                                            ActionRow.of(
                                                TextInput.create(
                                                    TextInputStyle.PARAGRAPH,
                                                    idWelcome.toString(),
                                                    "Message de bienvenue",
                                                    true
                                                )
                                            )
                                        )
                                    }
                                    .modif(buttonClickEvent)
                            }

                            em.register()
                            city.buttonInteraction.createOriginalMessageUpdater()
                                .removeAllEmbeds()
                                .removeAllComponents()
                                .addComponents(em.components, ActionRow.of(em.buttons))
                                .addEmbed(embedBuilder)
                                .update()
                        }
                        .responder(slashCommand)

                }

                WorldEnum.TUTO -> {
                    modifServer(slashCommand, server)
                }
            }
        }
    }

    private fun modifServer(server: ServerBot, context: Context, serverDiscord: Server) {
        MenuBuilder(
            "Modification du serveur dans un monde à lieu unique",
            "Vous pouvez modifier la configuration du serveur discord de façon simple dans un monde à serveur unique. Sélectionner ce qu'il faut modifier :",
            Color.YELLOW,
            context
        )
            .addButton(
                "Mise à jour du nom du serveur",
                "Le nom du serveur discord est stocké dans la base de données. Mais si vous changer le nom du serveur discord le bot ne met pas à jour automatiquement de son côté."
            ) { name, _, _ ->
                server["name"] = serverDiscord.name
                name.buttonInteraction.createImmediateResponder()
                    .setContent("Le nom du serveur a été mis à jour avec succès !")
                    .respond()
            }
            .addButton(
                "Modifier le nom RP du lieu",
                "Modifiable à tout moment, le nom de votre ville est personnalisable."
            ) { name, c1, b1 ->
                val id = generateUniqueID()
                val idName = generateUniqueID()

                modalManager.add(id) {
                    val opName = it.modalInteraction.getTextInputValueByCustomId(idName.toString())

                    if (!opName.isPresent) {
                        throw IllegalArgumentException("Le nom n'a pas été rempli")
                    }

                    getUniquePlace(server)["name"] = opName.get()

                    it.modalInteraction.createImmediateResponder()
                        .setContent("Le nom RP de la ville a été modifié avec succès !")
                        .respond()
                }

                name.buttonInteraction.respondWithModal(
                    id.toString(),
                    "Mise à jour du nom RP de la ville",
                    ActionRow.of(
                        TextInput.create(TextInputStyle.SHORT, idName.toString(), "Nom de la ville", true)
                    )
                )
            }
            .addButton(
                "Modifier la description du lieu",
                "Modifiable à tout moment, la description de votre ville est la deuxième chose que voix une personne quand il regarde le lieu."
            ) { description, c1, b1 ->
                val id = generateUniqueID()
                val idDescription = generateUniqueID()

                modalManager.add(id) {
                    val opDescription = it.modalInteraction.getTextInputValueByCustomId(idDescription.toString())

                    if (!opDescription.isPresent) {
                        throw IllegalArgumentException("La description n'a pas été remplie")
                    }

                    getUniquePlace(server)["description"] = opDescription.get()

                    it.modalInteraction.createImmediateResponder()
                        .setContent("La description de la ville a été modifiée avec succès !")
                        .respond()
                }

                description.buttonInteraction.respondWithModal(
                    id.toString(),
                    "Mise à jour de la description de la ville",
                    ActionRow.of(
                        TextInput.create(
                            TextInputStyle.PARAGRAPH,
                            idDescription.toString(),
                            "Description de la ville",
                            true
                        )
                    )
                )
            }
            .addButton(
                "Modifier le message de bienvenue",
                "Modifiable à tout moment, le message de bienvenue est nécessaire pour mettre l'ambiance : ville magique ? Tech ? Abandonné ? Repaire de Pirates ?"
            ) { welcome, c1, b1 ->
                val id = generateUniqueID()
                val idWelcome = generateUniqueID()

                modalManager.add(id) {
                    val opWelcome = it.modalInteraction.getTextInputValueByCustomId(idWelcome.toString())

                    if (!opWelcome.isPresent) {
                        throw IllegalArgumentException("Le message de bienvenue n'a pas été rempli")
                    }

                    getUniquePlace(server)["welcome"] = opWelcome.get()

                    it.modalInteraction.createImmediateResponder()
                        .setContent("Le message de bienvenue a été modifié avec succès !")
                        .respond()
                }

                welcome.buttonInteraction.respondWithModal(
                    id.toString(),
                    "Mise à jour du message de bienvenue",
                    ActionRow.of(
                        TextInput.create(TextInputStyle.PARAGRAPH, idWelcome.toString(), "Message de bienvenue", true)
                    )
                )
            }
            .responder(slashCommand)
    }

    private fun getUniquePlace(server: ServerBot): Place {
        val placesL = server.getPlaces()
        if (placesL.size != 1) {
            throw IllegalArgumentException("Le serveur n'a pas un lieu unique. Contactez un administrateur.")
        }
        return places[placesL[0]]
            ?: throw IllegalArgumentException("Le lieu n'existe pas. Contactez un administrateur.")
    }
}