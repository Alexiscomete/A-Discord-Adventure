package io.github.alexiscomete.lapinousecond.commands.withslash.classes

import io.github.alexiscomete.lapinousecond.commands.withslash.Command
import io.github.alexiscomete.lapinousecond.commands.withslash.ExecutableWithArguments
import io.github.alexiscomete.lapinousecond.commands.withslash.getAccount
import io.github.alexiscomete.lapinousecond.useful.managesave.saveManager
import io.github.alexiscomete.lapinousecond.worlds.WorldEnum
import io.github.alexiscomete.lapinousecond.worlds.places
import org.javacord.api.entity.message.MessageFlag
import org.javacord.api.interaction.SlashCommandInteraction

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
                val resultSet = saveManager.executeQuery("SELECT * FROM places WHERE x = $x AND y = $y AND world = '${world.progName}'", true)
                    ?: throw IllegalArgumentException("Error while executing query")
                if (resultSet.next()) {
                    val place = places[resultSet.getString("id").toLong()]
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