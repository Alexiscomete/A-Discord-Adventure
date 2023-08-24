package io.github.alexiscomete.lapinousecond.view.discord.commands.classes

import io.github.alexiscomete.lapinousecond.view.discord.commands.Command
import io.github.alexiscomete.lapinousecond.view.discord.commands.ExecutableWithArguments
import io.github.alexiscomete.lapinousecond.view.discord.commands.SubCommand
import org.javacord.api.interaction.SlashCommandInteraction

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
    "Tu n'as pas besoin de savoir"
), ExecutableWithArguments {
    override val fullName: String = "admin executeSQL"
    override val botPerms: Array<String>? = null // TODO : add perms

    override fun execute(slashCommand: SlashCommandInteraction) {

    }
}

class AdminCommandQuerySQL : SubCommand(
    "querySQL",
    "Tu n'as pas besoin de savoir"
), ExecutableWithArguments {
    override val fullName: String = "admin querySQL"
    override val botPerms: Array<String>? = null // TODO : add perms

    override fun execute(slashCommand: SlashCommandInteraction) {

    }
}

class AdminCommandCache : SubCommand(
    "cache",
    "Tu n'as pas besoin de savoir"
), ExecutableWithArguments {
    override val fullName: String = "admin cache"
    override val botPerms: Array<String>? = null // TODO : add perms

    override fun execute(slashCommand: SlashCommandInteraction) {

    }
}