package io.github.alexiscomete.lapinousecond.view.contextmanager

import io.github.alexiscomete.lapinousecond.view.Context
import org.javacord.api.entity.channel.TextChannel
import org.javacord.api.event.message.MessageCreateEvent

abstract class MessagesContextManager(private val textChannel: TextChannel): ContextManager {
    override fun canApply(string: String): Boolean {
        return string == textChannel.idAsString
    }

    abstract fun ex(s: MessageCreateEvent, c: Context)

    override fun toString(): String {
        return "MessagesContextManager{${textChannel.idAsString}}"
    }
}