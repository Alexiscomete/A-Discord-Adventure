package io.github.alexiscomete.lapinousecond.commands.withoutslash.classes

import io.github.alexiscomete.lapinousecond.commands.withoutslash.CommandWithAccount
import io.github.alexiscomete.lapinousecond.entity.Player
import io.github.alexiscomete.lapinousecond.worlds.WorldEnum
import org.javacord.api.entity.message.embed.EmbedBuilder
import org.javacord.api.event.message.MessageCreateEvent
import java.util.*

/**
 * @author Alexiscomete
 *
 * Command to change the world of the player
 */
class WorldCommand : CommandWithAccount(
    "Commande pour changer de monde",
    "world",
    "Commande pour changer de monde, utiliser world change [nom du monde] pour changer de monde et world list pour voir les mondes disponibles"
) {
    override fun execute(messageCreateEvent: MessageCreateEvent, content: String, args: Array<String>, p: Player) {
        if (args.size > 1) {
            if (args[1] == "change") {
                if (args.size > 2) {
                    if (p["bal"] == "") {
                        p["bal"] = "0.0"
                    }
                    try {
                        val worldEnum = WorldEnum.valueOf(args[2].uppercase(Locale.getDefault()))
                        val world = worldEnum.world
                        // récupération du monde du joueur : si c'est le même monde, on le lui dit
                        if (p.getString("world") == world.progName) {
                            messageCreateEvent.channel.sendMessage("Vous êtes déjà dans le monde " + world.progName)
                            return
                        }
                        // on regarde si il a assez d'argent
                        if (p["bal"].toDouble() < world.travelPrice) {
                            messageCreateEvent.channel.sendMessage("Vous n'avez pas assez d'argent pour changer de monde")
                            return
                        }
                        // on retire l'argent
                        p["bal"] = (p["bal"].toDouble() - world.travelPrice).toString()
                        // on change le monde
                        p["world"] = world.progName
                    } catch (e: IllegalArgumentException) {
                        val eb = EmbedBuilder()
                        eb.setTitle("Monde inconnu")
                        eb.setDescription("Le monde " + args[2] + " n'existe pas")
                        messageCreateEvent.channel.sendMessage(eb)
                    }
                } else {
                    messageCreateEvent.channel.sendMessage("Veuillez entrer le nom du monde")
                }
            }
        }
    }
}