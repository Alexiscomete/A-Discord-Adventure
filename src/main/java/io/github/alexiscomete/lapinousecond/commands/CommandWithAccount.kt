package io.github.alexiscomete.lapinousecond.commands

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
        val p = players[messageCreateEvent.messageAuthor.id]
        p?.let { execute(messageCreateEvent, content, args, it) }
            ?: messageCreateEvent.message.reply("Vous devez d'abord vous créer un compte avec -start")
    }

    abstract fun execute(messageCreateEvent: MessageCreateEvent, content: String, args: Array<String>, p: Player)
}