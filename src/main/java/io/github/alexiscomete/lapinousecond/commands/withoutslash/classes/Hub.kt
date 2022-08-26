package io.github.alexiscomete.lapinousecond.commands.withoutslash.classes
import io.github.alexiscomete.lapinousecond.commands.withoutslash.CommandBot
import io.github.alexiscomete.lapinousecond.entity.players
import io.github.alexiscomete.lapinousecond.worlds.ServerBot
import org.javacord.api.event.message.MessageCreateEvent

/**
 * Hub command, used to teleport to the hub the player who sent the command
 */
class Hub : CommandBot(
    "Vous permet de retourner au serveur principal **gratuitement**",
    "hub",
    "Permet de retourner gratuitement au hub si vous êtes bloqué dans un serveur, par exemple si il n'y a pas de salon textuel"
) {
    override fun execute(messageCreateEvent: MessageCreateEvent, content: String, args: Array<String>) {

    }
}