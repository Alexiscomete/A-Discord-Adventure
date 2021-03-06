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
                "dirt" -> {
                    // check if enough arguments
                    if (args.size < 4) {
                        sendArgs(messageCreateEvent, p)
                        return
                    }
                    // check if the arguments are numbers
                    if (isNotNumeric(args[2])) {
                        sendNumberEx(messageCreateEvent, p, 2)
                        return
                    }
                    if (isNotNumeric(args[3])) {
                        sendNumberEx(messageCreateEvent, p, 3)
                        return
                    }
                    if (checkRangeArgsMap(args, messageCreateEvent)) return
                    // check if the pixel is dirt
                    if (Map.isDirt(args[2].toInt(), args[3].toInt())) {
                        messageCreateEvent.message.reply("The pixel is dirt")
                    } else {
                        messageCreateEvent.message.reply("The pixel is not dirt")
                    }
                }
                "zoom_p" -> {
                    // check if the player is in the world DIBIMAP
                    val world = p.getString("world")
                    if (world != "DIBIMAP") {
                        messageCreateEvent.message.reply("Vous n'??tes pas dans le monde DIBIMAP")
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
                "zoom" -> {
                    // check if enough arguments
                    if (args.size < 5) {
                        sendArgs(messageCreateEvent, p)
                        return
                    }
                    // check if the arguments are numbers
                    if (isNotNumeric(args[2])) {
                        sendNumberEx(messageCreateEvent, p, 2)
                        return
                    }
                    if (isNotNumeric(args[3])) {
                        sendNumberEx(messageCreateEvent, p, 3)
                        return
                    }
                    if (isNotNumeric(args[4])) {
                        sendNumberEx(messageCreateEvent, p, 4)
                        return
                    }
                    if (checkRangeArgsMap(args, messageCreateEvent)) return
                    // check if arg 4 is < 60
                    if (args[4].toInt() > 60) {
                        messageCreateEvent.message.reply("The fourth argument must be between 0 and 60")
                        return
                    }
                    // send the zoom on the map
                    val messageBuilder = MessageBuilder()
                    messageCreateEvent.message.reply("Cr??ation de la carte en cours et ajout des villes proches ...")
                    val image = Map.bigger(
                        Map.zoom(
                            args[2].toInt(), args[3].toInt(), args[4].toInt()
                        ), 10
                    )
                    val places = Place.getPlacesWithWorld("DIBIMAP")
                    places.removeIf { place: Place -> !place.getX().isPresent || !place.getY().isPresent || place.getX().get() < args[2].toInt() - args[4].toInt() * 2 || place.getX().get() > args[2].toInt() + args[4].toInt() * 2 || place.getY().get() < args[3].toInt() - args[4].toInt() || place.getY().get() > args[3].toInt() + args[4].toInt() }
                    Map.getMapWithNames(
                        places,
                        args[2].toInt() - args[4].toInt() * 2,
                        args[3].toInt() - args[4].toInt(),
                        args[4].toInt() * 4,
                        args[4].toInt() * 2,
                        image
                    )
                    messageBuilder.addAttachment(image, "zoommap.png")
                    messageBuilder.send(messageCreateEvent.channel)
                }
                "findpath" -> {
                    // check if enough arguments
                    if (args.size < 6) {
                        sendArgs(messageCreateEvent, p)
                        return
                    }
                    // check if the arguments are numbers
                    var i = 2
                    while (i < 6) {
                        if (isNotNumeric(args[i])) {
                            sendNumberEx(messageCreateEvent, p, i)
                            return
                        }
                        i++
                    }
                    // check if the arguments are in the right range
                    if (args[2].toInt() < 0 || args[2].toInt() > Map.MAP_WIDTH) {
                        messageCreateEvent.message.reply("The first argument must be between 0 and " + Map.MAP_WIDTH)
                        return
                    }
                    if (args[3].toInt() < 0 || args[3].toInt() > Map.MAP_HEIGHT) {
                        messageCreateEvent.message.reply("The second argument must be between 0 and " + Map.MAP_HEIGHT)
                        return
                    }
                    if (args[4].toInt() < 0 || args[4].toInt() > Map.MAP_WIDTH) {
                        messageCreateEvent.message.reply("The third argument must be between 0 and " + Map.MAP_WIDTH)
                        return
                    }
                    if (args[5].toInt() < 0 || args[5].toInt() > Map.MAP_HEIGHT) {
                        messageCreateEvent.message.reply("The fourth argument must be between 0 and " + Map.MAP_HEIGHT)
                        return
                    }
                    // send the path
                    val path = Map.findPath(
                        Map.getNode(
                            args[2].toInt(), args[3].toInt(), ArrayList()
                        ), Map.getNode(
                            args[4].toInt(), args[5].toInt(), ArrayList()
                        ), messageCreateEvent.channel
                    )
                    messageCreateEvent.message.reply("Path found : " + path.size + " steps")
                    sendPath(messageCreateEvent, path)
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