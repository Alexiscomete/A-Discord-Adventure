package io.github.alexiscomete.lapinousecond.commands.withoutslash

import io.github.alexiscomete.lapinousecond.entity.Player
import io.github.alexiscomete.lapinousecond.entity.players
import org.javacord.api.event.message.MessageCreateEvent

abstract class CommandWithAccount(
    description: String,
    name: String,
    totalDescription: String,
    vararg perms: String
) : CommandBot(
    description, name, totalDescription, *perms
) {
    override fun execute(messageCreateEvent: MessageCreateEvent, content: String, args: Array<String>) {

    }

    abstract fun execute(messageCreateEvent: MessageCreateEvent, content: String, args: Array<String>, p: Player)
}