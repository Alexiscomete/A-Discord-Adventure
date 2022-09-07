package io.github.alexiscomete.lapinousecond.commands.withslash.classes

import io.github.alexiscomete.lapinousecond.commands.withslash.Command
import io.github.alexiscomete.lapinousecond.commands.withslash.ExecutableWithArguments
import io.github.alexiscomete.lapinousecond.message_event.MenuBuilder
import io.github.alexiscomete.lapinousecond.modalManager
import io.github.alexiscomete.lapinousecond.useful.managesave.generateUniqueID
import io.github.alexiscomete.lapinousecond.worlds.WorldEnum
import io.github.alexiscomete.lapinousecond.worlds.dibimap.DibimapServer
import io.github.alexiscomete.lapinousecond.worlds.dibimap.isDibimap
import io.github.alexiscomete.lapinousecond.worlds.places
import io.github.alexiscomete.lapinousecond.worlds.servers
import org.javacord.api.entity.message.component.ActionRow
import org.javacord.api.entity.message.component.TextInput
import org.javacord.api.entity.message.component.TextInputStyle
import org.javacord.api.entity.permission.PermissionType
import org.javacord.api.interaction.SlashCommandInteraction
import java.awt.Color
import java.util.*

//TODO
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

    override fun execute(slashCommand: SlashCommandInteraction) {
        val serverId = slashCommand.server.get().id
        val server = servers[serverId]
        if (server == null) {
            val world =
                if (serverId == 854288660147994634) WorldEnum.TUTO else if (isDibimap(serverId)) WorldEnum.DIBIMAP else WorldEnum.NORMAL
            MenuBuilder(
                "Votre première configuration",
                "Votre serveur discord a été automatiquement assigné au ${world.nameRP}. Explications :\nLe Dibistan a un drapeau qui est aussi son territoire principal. Si votre serveur discord est un état ou une région qui a un territoire en forme de polygone sur le drapeau, alors sont monde est le ${WorldEnum.DIBIMAP.nameRP} sinon c'est le monde ${WorldEnum.NORMAL.nameRP}. Les mécaniques sont différentes dans les 2 mondes. **Le monde détecté est-il correcte ?**",
                Color.BLUE
            )
                .addButton("Oui", "Le monde est correcte et je continue la configuration. **Irréversible**") { yes ->

                    when (world) {
                        WorldEnum.NORMAL -> {
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
                                        "Nom de la ville (rp)",
                                        true
                                    ),
                                    TextInput.create(
                                        TextInputStyle.PARAGRAPH,
                                        idDescription.toString(),
                                        "Description de la ville (rp)",
                                        true
                                    ),
                                    TextInput.create(
                                        TextInputStyle.PARAGRAPH,
                                        idWelcome.toString(),
                                        "Message de bienvenue",
                                        true
                                    )
                                )
                            )

                            modalManager.add(id) {
                                val opNameRp = it.modalInteraction.getTextInputValueByCustomId(idNameRP.toString())
                                val opDescription = it.modalInteraction.getTextInputValueByCustomId(idDescription.toString())
                                val opWelcome = it.modalInteraction.getTextInputValueByCustomId(idWelcome.toString())

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

                                configNormalServer(world, slashCommand, placeId)

                                it.modalInteraction.createImmediateResponder()
                                    .setContent("Le serveur a été configuré avec succès ! Les coordonnées sont [${x}:${y}]")
                                    .respond()
                            }
                        }
                        WorldEnum.DIBIMAP -> {

                            servers.add(serverId)
                            val serverC = servers[serverId]
                                ?: throw IllegalArgumentException("Un problème de source inconnue est survenue. La création du serveur a échoué.")
                            serverC["world"] = world.progName
                            serverC["name"] = slashCommand.server.get().name

                            val serverForZones = DibimapServer.valueOf(serverId.toString())

                            yes.buttonInteraction.createImmediateResponder()
                                .setContent("Le serveur a été configuré avec succès ! Vous devez faire à nouveau la commande pour ajouter des villes. ")
                                .respond()
                        }
                        WorldEnum.TUTO -> {
                            val id = generateUniqueID()
                            places.add(id)
                            val place = places[id]
                                ?: throw IllegalArgumentException("Un problème de source inconnue est survenue. La création du serveur a échoué.")
                            place["nameRP"] = "Saint-Lapin-sur-bot" // à Demander
                            place["description"] = "Ville accueillante du tutoriel" // à Demander
                            place["welcome"] = "Ne restez pas trop longtemps ici ! Profitez de l'aventure" // à Demander
                            place["x"] = 45.toString() // automatique normalement : aléatoire
                            place["y"] = 20.toString() // automatique normalement : aléatoire
                            place["type"] = "city" // automatique normalement
                            place["world"] = world.progName // automatique normalement
                            place["server"] = serverId.toString() // automatique normalement

                            configNormalServer(world, slashCommand, id)

                            yes.buttonInteraction.createImmediateResponder()
                                .setContent("Le serveur a été configuré avec succès !")
                                .respond()
                        }
                    }

                }
                .addButton("Non", "Le monde est incorrecte ou je veux changer quelque chose. **Réversible**") {
                    it.buttonInteraction.createOriginalMessageUpdater()
                        .setContent("Contactez un administrateur pour changer le monde si c'est le problème")
                        .removeAllEmbeds()
                        .removeAllComponents()
                        .update()
                }
                .responder(slashCommand)
        } else {
            val world = WorldEnum.valueOf(server["world"])
        }
    }

    private fun configNormalServer(world: WorldEnum, slashCommand: SlashCommandInteraction, placeId: Long) {
        val server = slashCommand.server.get()
        servers.add(server.id)
        val serverC = servers[server.id]
            ?: throw IllegalArgumentException("Un problème de source inconnue est survenue. La création du serveur a échoué.")
        serverC["world"] = world.progName
        serverC["name"] = server.name
        serverC["places"] = placeId.toString()
    }
}