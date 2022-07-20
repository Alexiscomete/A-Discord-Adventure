package io.github.alexiscomete.lapinousecond.commands.withslash

import org.javacord.api.interaction.SlashCommandOption

interface Sub {
    val name: String
    val description: String

    fun getS(): SlashCommandOption
}