package io.github.alexiscomete.lapinousecond.commands.classes

import io.github.alexiscomete.lapinousecond.commands.CommandBot
import org.javacord.api.event.message.MessageCreateEvent

class Give : CommandBot("description", "give", "totalDescription") {
    override fun execute(messageCreateEvent: MessageCreateEvent, content: String, args: Array<String>) {}
}