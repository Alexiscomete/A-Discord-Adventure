package io.github.alexiscomete.lapinousecond.commands.withslash

import org.javacord.api.interaction.SlashCommandOption
import org.javacord.api.interaction.SlashCommandOptionType

open class SubCommandGroup(override val name: String, override val description: String, private val subCommands: Array<SubCommand>) : Sub {
    override fun getS(): SlashCommandOption {
        val options = subCommands.map {
            if (it is ExecutableWithArguments) {
                commands[it.fullName] = it
            }
            it.getS()
        }.toList()
        return SlashCommandOption.createWithOptions(SlashCommandOptionType.SUB_COMMAND_GROUP, name, description, options)
    }
}