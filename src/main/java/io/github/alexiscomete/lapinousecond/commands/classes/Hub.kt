package io.github.alexiscomete.lapinousecond.commands.classes
import io.github.alexiscomete.lapinousecond.*
import io.github.alexiscomete.lapinousecond.commands.CommandBot
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
        val p = saveManager.players[messageCreateEvent.messageAuthor.id]
        if (p == null) {
            messageCreateEvent.message.reply("Voici le serveur principal : <https://discord.gg/q4hVQ6gwyx>")
            return
        }
        if (content.endsWith("yes do it")) {
            messageCreateEvent.message.reply("✔ Flavinou vient de vous téléporter au hub <https://discord.gg/q4hVQ6gwyx>")
            p.setServer(854288660147994634L)
            p["world"] = "NORMAL"
            p["place_NORMAL"] = ServerBot(854288660147994634L).getString("places")
        } else {
            messageCreateEvent.message.reply("Etes-vous sûr de vouloir retourner au serveur principal ? Cette action ne peut pas être annulée. Tapez **hub yes do it** pour y retourner.")
        }
    }
}