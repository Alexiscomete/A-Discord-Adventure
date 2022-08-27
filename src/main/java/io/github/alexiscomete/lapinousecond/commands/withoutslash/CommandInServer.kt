package io.github.alexiscomete.lapinousecond.commands.withoutslash

import io.github.alexiscomete.lapinousecond.entity.Player
import org.javacord.api.event.message.MessageCreateEvent

abstract class CommandInServer(description: String, name: String, totalDescription: String, vararg perms: String) :
    CommandWithAccount(description, name, totalDescription, *perms) {
    override fun execute(messageCreateEvent: MessageCreateEvent, content: String, args: Array<String>, p: Player) {

    }

    abstract fun executeC(messageCreateEvent: MessageCreateEvent, content: String, args: Array<String>, p: Player)
}