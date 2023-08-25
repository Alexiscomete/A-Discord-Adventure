package io.github.alexiscomete.lapinousecond.view.discord.commands.classes

import io.github.alexiscomete.lapinousecond.data.managesave.saveManager
import io.github.alexiscomete.lapinousecond.view.discord.commands.Command
import io.github.alexiscomete.lapinousecond.view.discord.commands.ExecutableWithArguments
import io.github.alexiscomete.lapinousecond.view.discord.commands.SubCommand
import org.javacord.api.interaction.SlashCommandInteraction
import org.javacord.api.interaction.SlashCommandOption

class AdminCommandBase : Command(
    "admin",
    "Commandes réservées aux administrateurs. Pour le moment c'est juste l'owner",
    subCommands = listOf(
        AdminCommandExecuteSQL(),
        AdminCommandQuerySQL(),
        AdminCommandCache()
    )
)

class AdminCommandExecuteSQL : SubCommand(
    "executeSQL",
    "Tu n'as pas besoin de savoir",
    arrayListOf(
        SlashCommandOption.createStringOption(
            "sql_command",
            "La commande SQL à exécuter",
            true
        )
    )
), ExecutableWithArguments {
    override val fullName: String = "admin executeSQL"
    override val botPerms: Array<String>? = null

    override fun execute(slashCommand: SlashCommandInteraction) {
        if (slashCommand.user.isBotOwner) {
            val arguments = slashCommand.arguments
            try {
                val sqlCommand = arguments.first { it.name == "sql_command" }
                saveManager.execute(sqlCommand.stringValue.get())
            } catch (e: NoSuchElementException) {
                slashCommand.createImmediateResponder()
                    .setContent("Tu dois mettre un argument `sql_command`")
                    .respond()
            }
        } else {
            slashCommand.createImmediateResponder()
                .setContent("Tu n'es pas le propriétaire du bot")
                .respond()
        }
    }
}

class AdminCommandQuerySQL : SubCommand(
    "querySQL",
    "Tu n'as pas besoin de savoir",
    arrayListOf(
        SlashCommandOption.createStringOption(
            "sql_query",
            "La query SQL à exécuter",
            true
        )
    )
), ExecutableWithArguments {
    override val fullName: String = "admin querySQL"
    override val botPerms: Array<String>? = null

    override fun execute(slashCommand: SlashCommandInteraction) {
        if (slashCommand.user.isBotOwner) {
            val arguments = slashCommand.arguments
            try {
                val sqlQuery = arguments.first { it.name == "sql_query" }
                val result = saveManager.executeMultipleQuery(
                    saveManager.preparedStatement(sqlQuery.stringValue.get())
                )
                slashCommand.createImmediateResponder()
                    .setContent(result.toString())
                    .respond()
            } catch (e: NoSuchElementException) {
                slashCommand.createImmediateResponder()
                    .setContent("Tu dois mettre un argument `sql_query`")
                    .respond()
            }
        } else {
            slashCommand.createImmediateResponder()
                .setContent("Tu n'es pas le propriétaire du bot")
                .respond()
        }
    }
}

class AdminCommandCache : SubCommand(
    "cache",
    "Tu n'as pas besoin de savoir"
), ExecutableWithArguments {
    override val fullName: String = "admin cache"
    override val botPerms: Array<String>? = null

    override fun execute(slashCommand: SlashCommandInteraction) {
        if (slashCommand.user.isBotOwner) {
            slashCommand.createImmediateResponder()
                .setContent("Commande en cours de création")
                .respond()
        } else {
            slashCommand.createImmediateResponder()
                .setContent("Tu n'es pas le propriétaire du bot")
                .respond()
        }
    }
}
