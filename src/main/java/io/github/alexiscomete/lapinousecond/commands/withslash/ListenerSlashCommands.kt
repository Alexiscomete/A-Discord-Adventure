package io.github.alexiscomete.lapinousecond.commands.withslash

import org.javacord.api.event.interaction.SlashCommandCreateEvent
import org.javacord.api.listener.interaction.SlashCommandCreateListener

val commands = LinkedHashMap<String, Command>()

class ListenerSlashCommands : SlashCommandCreateListener {
    override fun onSlashCommandCreate(event: SlashCommandCreateEvent) {
        val slashCommand = event.slashCommandInteraction

    }
}