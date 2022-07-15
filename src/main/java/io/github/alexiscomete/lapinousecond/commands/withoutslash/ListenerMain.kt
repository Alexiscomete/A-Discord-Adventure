package io.github.alexiscomete.lapinousecond.commands.withoutslash

import org.javacord.api.event.message.MessageCreateEvent
import org.javacord.api.listener.message.MessageCreateListener
import java.util.*

class ListenerMain : MessageCreateListener {
    /**
     * Quand une personne envoie un message sur un salon visible par le bot
     * @param messageCreateEvent le message
     */
    override fun onMessageCreate(messageCreateEvent: MessageCreateEvent) {
        if (messageCreateEvent.messageContent.startsWith("-")) {
            println("bug")
            val content = messageCreateEvent.messageContent.lowercase(Locale.getDefault()).substring(1)
            val args = content.split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            val commandBot = commands[args[0]]
            println("null?")
            if (commandBot != null && !messageCreateEvent.messageAuthor.isBotUser) {
                println("no")
                commandBot.checkAndExecute(messageCreateEvent, content, args)
            }
        }
    }

    companion object {
        /**
         * Dictionnaire de toutes le commandes du bot
         */
        @JvmField
        val commands = LinkedHashMap<String, CommandBot>()
    }
}