package io.github.alexiscomete.lapinousecond.commands.withslash

import org.javacord.api.event.interaction.SlashCommandCreateEvent
import org.javacord.api.listener.interaction.SlashCommandCreateListener

val commands = LinkedHashMap<String, ExecutableWithArguments>()

class ListenerSlashCommands : SlashCommandCreateListener {
    override fun onSlashCommandCreate(event: SlashCommandCreateEvent) {
        val slashCommand = event.slashCommandInteraction
        try {
            commands[slashCommand.commandName]?.execute(slashCommand.arguments)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}