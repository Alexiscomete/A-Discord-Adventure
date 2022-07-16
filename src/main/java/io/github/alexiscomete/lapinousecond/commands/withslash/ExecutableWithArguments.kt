package io.github.alexiscomete.lapinousecond.commands.withslash

import org.javacord.api.interaction.SlashCommandInteractionOption

interface ExecutableWithArguments {
    abstract fun execute(arguments: List<SlashCommandInteractionOption>)
}