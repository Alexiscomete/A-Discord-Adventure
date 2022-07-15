package io.github.alexiscomete.lapinousecond.commands.withoutslash.classes

import io.github.alexiscomete.lapinousecond.commands.withoutslash.CommandWithAccount
import io.github.alexiscomete.lapinousecond.entity.Player
import org.javacord.api.event.message.MessageCreateEvent

class TestCommand : CommandWithAccount("test", "test", "test") {
    override fun execute(messageCreateEvent: MessageCreateEvent, content: String, args: Array<String>, p: Player) {
        messageCreateEvent.message.reply("Aucun test en cours, revenez plus tard.")
    }
}