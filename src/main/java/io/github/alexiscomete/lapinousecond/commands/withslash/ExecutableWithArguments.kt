package io.github.alexiscomete.lapinousecond.commands.withslash

import org.javacord.api.interaction.SlashCommandInteraction
import org.javacord.api.interaction.SlashCommandInteractionOption

interface ExecutableWithArguments {
    fun execute(slashCommand: SlashCommandInteraction)

    fun getServer(slashCommand: SlashCommandInteraction) {

    }
    fun getServerBot(slashCommand: SlashCommandInteraction) {

    }

    fun getAccount(slashCommandInteraction: SlashCommandInteraction) {

    }
}