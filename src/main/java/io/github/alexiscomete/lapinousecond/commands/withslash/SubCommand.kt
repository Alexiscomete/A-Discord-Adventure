package io.github.alexiscomete.lapinousecond.commands.withslash

import org.javacord.api.interaction.SlashCommandOption
import org.javacord.api.interaction.SlashCommandOptionType

class SubCommand(
    override val name: String,
    override val description: String,
    private val arguments: ArrayList<SlashCommandOption> = arrayListOf()
) : Sub {
    override fun getS(): SlashCommandOption {
        return SlashCommandOption.createWithOptions(SlashCommandOptionType.SUB_COMMAND, name, description, arguments)
    }
}