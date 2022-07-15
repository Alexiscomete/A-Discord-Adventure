package io.github.alexiscomete.lapinousecond.commands.withoutslash.classes

import io.github.alexiscomete.lapinousecond.commands.withoutslash.CommandInServer
import io.github.alexiscomete.lapinousecond.entity.Player
import io.github.alexiscomete.lapinousecond.message_event.ListButtons
import io.github.alexiscomete.lapinousecond.view.AnswerEnum
import io.github.alexiscomete.lapinousecond.worlds.buildings.Building
import io.github.alexiscomete.lapinousecond.worlds.buildings.Buildings
import org.javacord.api.entity.message.MessageBuilder
import org.javacord.api.entity.message.embed.EmbedBuilder
import org.javacord.api.event.message.MessageCreateEvent
import java.util.*

/**
 * Command to build a building, manage buildings, and use them
 */
class BuildingCommand : CommandInServer(
    "Toutes les actions relatives aux bâtiments",
    "build",
    "Utilisation :\n- enter [id] pour entrer dans un bâtiment\n- exit pour sortir\n- la commande seule permet de voir la liste des bâtiments ou de voir les informations rapides\n- infos pour voir les infos détaillées"
) {
    override fun executeC(messageCreateEvent: MessageCreateEvent, content: String, args: Array<String>, p: Player) {
        val building = p.getString("building")
        val building1: Building? = if (building == "" || building == "exit") {
            null
        } else {
            println("ll")
            Buildings.load(building)
        }
        if (args.size > 1) {
            // all sub commands
            when (args[1]) {
                "enter" -> if (building1 == null) {
                    if (args.size > 2) {
                        try {
                            val i = args[2].toLong()
                            val place = p.place
                            val buildingsString = place!!.getString("buildings")
                            val buildings = Buildings.loadBuildings(buildingsString)
                            println(buildingsString)
                            println(buildings.size)
                            var b: Building? = null
                            for (bu in buildings) {
                                if (bu.id == i) {
                                    b = bu
                                }
                            }
                            if (b == null) {
                                sendImpossible(messageCreateEvent, p)
                                return
                            } else {
                                p["building"] = args[2]
                                println(args[2])
                                messageCreateEvent.message.reply(p.getAnswer(AnswerEnum.ENTREE_BUILD, true))
                            }
                        } catch (e: IllegalArgumentException) {
                            sendNumberEx(messageCreateEvent, p, 2)
                        }
                    } else {
                        sendArgs(messageCreateEvent, p)
                    }
                } else {
                    sendImpossible(messageCreateEvent, p)
                }
                "exit" -> if (building1 == null) {
                    sendImpossible(messageCreateEvent, p)
                } else {
                    p["building"] = "exit"
                    sendList(messageCreateEvent, p)
                }
                "infos" -> if (building1 == null) {
                    sendImpossible(messageCreateEvent, p)
                } else {
                    building1.completeInfos(p)?.send(messageCreateEvent.channel)
                }
                "build" -> if (building1 == null) {
                    if (args.size > 2) {
                        try {
                            val b = Buildings.valueOf(args[2].uppercase(Locale.getDefault()))
                            if (b.isBuild && b.buildingAutorisations?.isAutorise(p) == true) {
                                val place = p.place
                                val building2 = place?.let { Building(b, p, it) }
                                val embedBuilder = building2?.infos(p)
                                messageCreateEvent.message.reply(embedBuilder)
                            } else {
                                b.buildingAutorisations?.let { println(it.isAutorise(p)) }
                                println(b.isBuild)
                                sendImpossible(messageCreateEvent, p)
                            }
                        } catch (e: IllegalArgumentException) {
                            sendImpossible(messageCreateEvent, p)
                        }
                    } else {
                        sendBuildTypeList(messageCreateEvent)
                    }
                } else {
                    sendImpossible(messageCreateEvent, p)
                }
                "usage" -> if (building1 == null) {
                    sendImpossible(messageCreateEvent, p)
                } else {
                    messageCreateEvent.message.reply(building1.usage)
                }
                "help" -> {
                    if (building1 == null) {
                        sendImpossible(messageCreateEvent, p)
                    } else {
                        messageCreateEvent.message.reply(building1.help)
                    }
                    if (building1 == null) {
                        sendImpossible(messageCreateEvent, p)
                    } else {
                        building1.interpret(args)
                    }
                }
                else -> if (building1 == null) {
                    sendImpossible(messageCreateEvent, p)
                } else {
                    building1.interpret(args)
                }
            }
        } else {
            if (building1 == null) {
                sendList(messageCreateEvent, p)
            } else {
                messageCreateEvent.message.reply(building1.infos(p))
            }
        }
    }

    private fun sendBuildTypeList(messageCreateEvent: MessageCreateEvent) {
        val messageBuilder = MessageBuilder()
        val embedBuilder = EmbedBuilder()
        messageBuilder.setEmbed(embedBuilder)
        val buildings = ArrayList(listOf(*Buildings.values()))
        val buildingListButtons = ListButtons(
            embedBuilder,
            buildings
        ) { embedBuilder: EmbedBuilder, min: Int, num: Int, list: ArrayList<Buildings> ->
            addListTypeBuild(
                embedBuilder,
                min,
                num,
                list
            )
        }
        buildingListButtons.register()
        messageBuilder.send(messageCreateEvent.channel)
    }

    private fun addListTypeBuild(embedBuilder: EmbedBuilder, min: Int, num: Int, list: ArrayList<Buildings>) {
        for (i in min until num) {
            val b = list[i]
            embedBuilder.addField(
                b.name_, """
     Peut être construit : ${b.isBuild}
     Prix : ${b.basePrice}
     """.trimIndent(), false
            )
        }
    }

    private fun sendList(messageCreateEvent: MessageCreateEvent, p: Player) {
        val place = p.place
        val buildingsString = place!!.getString("buildings")
        val buildings = Buildings.loadBuildings(buildingsString)
        val messageBuilder = MessageBuilder()
        val embedBuilder = EmbedBuilder()
        messageBuilder.setEmbed(embedBuilder)
        val buildingListButtons = ListButtons(
            embedBuilder,
            buildings
        ) { embedBuilder: EmbedBuilder, min: Int, num: Int, uArrayList: ArrayList<Building> ->
            addListBuild(
                embedBuilder,
                min,
                num,
                uArrayList
            )
        }
        buildingListButtons.register()
        messageBuilder.send(messageCreateEvent.channel)
    }

    private fun addListBuild(embedBuilder: EmbedBuilder, min: Int, num: Int, uArrayList: ArrayList<Building>) {
        for (i in min until min + num) {
            val u = uArrayList[i]
            embedBuilder.addField(
                if (u.getString("name") == "") "???" else u.getString("name"),
                u.id.toString() + " -> (" + u.getString("type") + " : " + u.getString("build_status") + ") " + u.getString(
                    "descr"
                )
            )
        }
    }
}