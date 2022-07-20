package io.github.alexiscomete.lapinousecond.commands.withslash

import org.javacord.api.interaction.SlashCommandOption

class SubCommand(override val name: String, override val description: String) : Sub {
    override fun getS(): SlashCommandOption {
        TODO("Not yet implemented")
    }
}