package commands.classes

import commands.CommandWithAccount
import entity.Player
import org.javacord.api.event.message.MessageCreateEvent

class TestCommand : CommandWithAccount("test", "test", "test") {
    override fun execute(messageCreateEvent: MessageCreateEvent, content: String, args: Array<String>, p: Player) {
        messageCreateEvent.message.reply("Aucun test en cours, revenez plus tard.")
    }
}