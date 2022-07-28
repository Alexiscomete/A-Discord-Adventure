package io.github.alexiscomete.lapinousecond.commands.withoutslash.classes

import io.github.alexiscomete.lapinousecond.useful.managesave.saveManager
import io.github.alexiscomete.lapinousecond.api
import io.github.alexiscomete.lapinousecond.commands.withoutslash.CommandBot
import io.github.alexiscomete.lapinousecond.entity.Player
import io.github.alexiscomete.lapinousecond.entity.players
import org.javacord.api.entity.message.embed.EmbedBuilder
import org.javacord.api.entity.user.User
import org.javacord.api.event.message.MessageCreateEvent
import java.awt.Color
import java.sql.SQLException

class InventoryC : CommandBot(
    "Ouvre l'inventaire",
    "inv",
    "Vous permet d'ouvrir votre inventaire et de voir votre avancement dans l'aventure ! Utiliser inv top bal pour connaître le classement des joueurs"
) {
    override fun execute(messageCreateEvent: MessageCreateEvent, content: String, args: Array<String>) {
        if (args.size > 1) {
            if (args[1] == "top") {

            } else {
                if (args[1].startsWith("<@")) {
                    args[1] = args[1].substring(2, args[1].length - 1)
                }
                try {
                    val p = players[args[1].toLong()]
                    /*
                    p?.let { invOf(it, messageCreateEvent) }
                        ?: messageCreateEvent.message.reply("Cette personne n'a pas encore de compte")*/
                } catch (e: NumberFormatException) {
                    messageCreateEvent.message.reply("Pour voir l'inventiare d'une personne, vous devez indiquer son id ou la mentionner")
                }
            }
        } else {
            val p = players[messageCreateEvent.messageAuthor.id]
        }
    }
}