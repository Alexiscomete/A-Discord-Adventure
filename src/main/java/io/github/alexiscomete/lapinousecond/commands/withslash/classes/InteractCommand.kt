package io.github.alexiscomete.lapinousecond.commands.withslash.classes

import io.github.alexiscomete.lapinousecond.commands.withslash.Command
import io.github.alexiscomete.lapinousecond.commands.withslash.ExecutableWithArguments
import io.github.alexiscomete.lapinousecond.commands.withslash.getAccount
import io.github.alexiscomete.lapinousecond.message_event.MenuBuilder
import io.github.alexiscomete.lapinousecond.useful.managesave.saveManager
import io.github.alexiscomete.lapinousecond.worlds.WorldEnum
import io.github.alexiscomete.lapinousecond.worlds.places
import org.javacord.api.entity.message.MessageFlag
import org.javacord.api.interaction.SlashCommandInteraction
import java.awt.Color

class InteractCommandBase : Command(
    "interact",
    "Interact with your environment",
    "interact"
), ExecutableWithArguments {

    override val fullName: String
        get() = "interact"
    override val botPerms: Array<String>
        get() = arrayOf("PLAY")

    override fun execute(slashCommand: SlashCommandInteraction) {
        val player = getAccount(slashCommand)
        val world = WorldEnum.valueOf(player["world"])
        when (player["place_${world.progName}_type"]) {
            "coos" -> {
                // on regarde si il existe une ville à l'endroit où le joueur est
                val x = player["place_${world.progName}_x"].toInt()
                val y = player["place_${world.progName}_y"].toInt()
                val resultSet = saveManager.executeQuery(
                    "SELECT * FROM places WHERE x = $x AND y = $y AND world = '${world.progName}'",
                    true
                )
                    ?: throw IllegalArgumentException("Error while executing query")
                if (resultSet.next()) {
                    val place = places[resultSet.getString("id").toLong()]
                        ?: throw IllegalArgumentException("Place not found")
                    MenuBuilder(
                        "Interactions sur la case $x $y du monde ${world.nameRP}",
                        "Liste de toutes vos possibilités dans la version actuelle du bot. Les sorts ne sont pas compris.",
                        Color.BLUE
                    )
                        .addButton("Entrer dans la ville", "La ville ${place["nameRP"]} est ici ! Vous pouvez y entrer en cliquant sur ce bouton. Description : ${place["description"]}") {
                            player["place_${world.progName}_type"] = "city"
                            player["place_${world.progName}_id"] = place["id"]
                            it.buttonInteraction.createImmediateResponder()
                                .setContent("Vous êtes maintenant dans la ville ${place["nameRP"]} !")
                                .setFlags(MessageFlag.EPHEMERAL)
                                .respond()
                        }
                } else {
                    slashCommand.createImmediateResponder()
                        .setContent("Il n'y a rien ici. De nouvelles interactions arriveront à l'avenir, pour le moment allez vers une ville")
                        .setFlags(MessageFlag.EPHEMERAL)
                        .respond()
                }
            }
            "building" -> {

            }
            "city" -> {

            }
            else -> {
                slashCommand.createImmediateResponder()
                    .setContent("Aucune interaction possible ici")
                    .setFlags(MessageFlag.EPHEMERAL)
                    .respond()
            }
        }
    }

}