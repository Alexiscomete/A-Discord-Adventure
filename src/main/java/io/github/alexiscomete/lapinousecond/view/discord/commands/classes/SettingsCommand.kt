package io.github.alexiscomete.lapinousecond.view.discord.commands.classes

import io.github.alexiscomete.lapinousecond.view.discord.commands.Command
import io.github.alexiscomete.lapinousecond.view.discord.commands.ExecutableWithArguments
import org.javacord.api.interaction.SlashCommandInteraction

class SettingsCommandBase : Command(
    "settings",
    "Permet de changer les paramètres du bot, et par exemple d'enlever les notifications",
), ExecutableWithArguments {
    override val fullName: String
        get() = TODO("Not yet implemented")
    override val botPerms: Array<String>?
        get() = TODO("Not yet implemented")

    override fun execute(slashCommand: SlashCommandInteraction) {
        TODO("Not yet implemented")
    }

}
