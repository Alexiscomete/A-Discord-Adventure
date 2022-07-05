package io.github.alexiscomete.lapinousecond.commands

import io.github.alexiscomete.lapinousecond.entity.Player
import org.javacord.api.event.message.MessageCreateEvent

abstract class CommandInServer(description: String, name: String, totalDescription: String, vararg perms: String) :
    CommandWithAccount(description, name, totalDescription, *perms) {
    override fun execute(messageCreateEvent: MessageCreateEvent, content: String, args: Array<String>, p: Player) {
        val servOp = messageCreateEvent.server
        if (servOp.isPresent) {
            val serv = servOp.get()
            if (serv.id == p["serv"].toLong()) {
                executeC(messageCreateEvent, content, args, p)
            } else {
                messageCreateEvent.message.reply("Utilisez cette commande dans un salon du serveur actuel : " + p["serv"].toLong())
            }
        } else {
            messageCreateEvent.message.reply("Utilisez cette commande dans un salon du serveur actuel : " + p["serv"].toLong())
        }
    }

    abstract fun executeC(messageCreateEvent: MessageCreateEvent, content: String, args: Array<String>, p: Player)
}