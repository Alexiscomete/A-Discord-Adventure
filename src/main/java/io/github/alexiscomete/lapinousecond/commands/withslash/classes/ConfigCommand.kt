package io.github.alexiscomete.lapinousecond.commands.withslash.classes

import io.github.alexiscomete.lapinousecond.commands.withslash.Command
import io.github.alexiscomete.lapinousecond.commands.withslash.ExecutableWithArguments
import io.github.alexiscomete.lapinousecond.message_event.MenuBuilder
import io.github.alexiscomete.lapinousecond.useful.managesave.generateUniqueID
import io.github.alexiscomete.lapinousecond.worlds.WorldEnum
import io.github.alexiscomete.lapinousecond.worlds.dibimap.isDibimap
import io.github.alexiscomete.lapinousecond.worlds.places
import io.github.alexiscomete.lapinousecond.worlds.servers
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
                .addButton("Oui", "Le monde est correcte et je continue la configuration. **Irréversible**") {

                    when (world) {
                        WorldEnum.NORMAL -> {
                            val id = generateUniqueID()
                            val idNameRP = generateUniqueID()
                            val idDescription = generateUniqueID()
                            val idWelcome = generateUniqueID()
                        }
                        WorldEnum.DIBIMAP -> {
                            servers.add(serverId)
                            val serverC = servers[serverId]
                                ?: throw IllegalArgumentException("Un problème de source inconnue est survenue. La création du serveur a échoué.")
                            serverC["world"] = world.progName
                            serverC["name"] = slashCommand.server.get().name
                        }
                        WorldEnum.TUTO -> {
                            servers.add(serverId)
                            val serverC = servers[serverId]
                                ?: throw IllegalArgumentException("Un problème de source inconnue est survenue. La création du serveur a échoué.")
                            serverC["world"] = world.progName
                            serverC["name"] = slashCommand.server.get().name
                            val nameRP = "Saint-Lapin-sur-bot" // TODO
                            val description = "Ville accueillante du tutoriel"
                            val welcome = "Ne restez pas trop longtemps ici ! Profitez de l'aventure"
                            val x = 45
                            val y = 20

                            val id = generateUniqueID()
                            places.add(id)
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

}