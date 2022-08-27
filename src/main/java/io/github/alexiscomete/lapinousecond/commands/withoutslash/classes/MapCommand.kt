package io.github.alexiscomete.lapinousecond.commands.withoutslash.classes

import io.github.alexiscomete.lapinousecond.commands.withoutslash.CommandWithAccount
import io.github.alexiscomete.lapinousecond.entity.Player
import io.github.alexiscomete.lapinousecond.worlds.Place
import io.github.alexiscomete.lapinousecond.worlds.map.Map
import org.javacord.api.entity.message.MessageBuilder
import org.javacord.api.event.message.MessageCreateEvent

class MapCommand : CommandWithAccount("description", "map", "totalDescription") {
    override fun execute(messageCreateEvent: MessageCreateEvent, content: String, args: Array<String>, p: Player) {
        if (args.size == 1) {
            val messageBuilder = MessageBuilder()
            messageBuilder.addAttachment(Map.map, "map.png")
            messageBuilder.send(messageCreateEvent.channel)
            return
        }
        if (args.size > 1) {
            // switch arguments
            when (args[1]) {
                "zoom_p" -> {
                    // check if the player is in the world DIBIMAP
                    val world = p.getString("world")
                    if (world != "DIBIMAP") {
                        messageCreateEvent.message.reply("Vous n'êtes pas dans le monde DIBIMAP")
                        return
                    }
                    // get the player's position (x, y) in the world DIBIMAP (x/y_DIBIMAP), x and y are strings
                    val x = p.getString("x_DIBIMAP")
                    val y = p.getString("y_DIBIMAP")
                    // convert the strings to int
                    val xInt = x.toInt()
                    val yInt = y.toInt()
                    // zoom on the player's position and send the map bigger
                    val messageBuilder2 = MessageBuilder()
                    messageBuilder2.addAttachment(Map.bigger(Map.zoom(xInt, yInt, 30), 10), "map.png")
                    messageBuilder2.send(messageCreateEvent.channel)
                }
                else -> sendImpossible(messageCreateEvent, p)
            }
        }
    }

    private fun checkRangeArgsMap(
        args: Array<String>,
        messageCreateEvent: MessageCreateEvent
    ): Boolean {
        // check if the arguments are in the right range
        if (args[2].toInt() < 0 || args[2].toInt() > Map.MAP_WIDTH) {
            messageCreateEvent.message.reply("The first argument must be between 0 and " + Map.MAP_WIDTH)
            return true
        }
        if (args[3].toInt() < 0 || args[3].toInt() > Map.MAP_HEIGHT) {
            messageCreateEvent.message.reply("The second argument must be between 0 and " + Map.MAP_HEIGHT)
            return true
        }
        return false
    }
}