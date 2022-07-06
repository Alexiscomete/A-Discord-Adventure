package io.github.alexiscomete.lapinousecond.commands.classes

import io.github.alexiscomete.lapinousecond.commands.CommandWithAccount
import io.github.alexiscomete.lapinousecond.entity.Player
import io.github.alexiscomete.lapinousecond.worlds.ServerBot
import io.github.alexiscomete.lapinousecond.worlds.WorldEnum
import io.github.alexiscomete.lapinousecond.messagesManager
import io.github.alexiscomete.lapinousecond.worlds.servers
import org.javacord.api.entity.message.embed.EmbedBuilder
import org.javacord.api.event.message.MessageCreateEvent
import java.util.function.Consumer

class ConfigServ : CommandWithAccount(
    "Configuration du serveur, config info pour la configuration actuelle",
    "config",
    "Permet de configurer le serveur actuel si c'est le votre, config info permet sinon de voir les infos du serveur (pour tout le monde)"
) {
    override fun execute(messageCreateEvent: MessageCreateEvent, content: String, args: Array<String>, p: Player) {
        if (messageCreateEvent.isServerMessage && messageCreateEvent.server.isPresent && messageCreateEvent.messageAuthor.asUser().isPresent) {
            var server = servers[messageCreateEvent.server.get().id]
            if (args.size > 1 && args[1].equals("info", ignoreCase = true)) {
                if (server == null) {
                    messageCreateEvent.message.reply("Le serveur n' est pas configuré")
                } else {
                    val name = server.getString("namerp")
                    val descr = server.getString("descr")
                    val id = server.id
                    val welcome = server.getString("welcome")
                    var world = server.getString("world")
                    world = try {
                        val w = WorldEnum.valueOf(world).world
                        w.name
                    } catch (e: IllegalArgumentException) {
                        "Monde invalide"
                    }
                    val embedBuilder = EmbedBuilder()
                        .setDescription(if (descr != "") descr else "Description invalide")
                        .setTitle(if (name == "") "Nom invalide" else name)
                        .setAuthor(id.toString())
                        .addField("Message de bienvenue", if (welcome == "") "Message d' arrivé invalide" else welcome)
                        .addField("Monde", world)
                    messageCreateEvent.message.reply(embedBuilder)
                }
            } else if (messageCreateEvent.messageAuthor.isServerAdmin) {
                if (server == null) {
                    if (content.endsWith("oui")) {
                        messageCreateEvent.message.reply("Création en cours ....")
                        server = ServerBot(messageCreateEvent.server.get().id)
                        servers.hashMap[server.id] = server
                        servers.add(messageCreateEvent.server.get().id)
                        messageCreateEvent.message.reply("Commençons par configurer le nom (entrez un nom) :")
                        val finalServer: ServerBot = server
                        val id = messageCreateEvent.messageAuthor.id
                        val textChannel = messageCreateEvent.channel
                        messagesManager.setValueAndRetry(
                            textChannel, id, "namerp", "Maintenant la description :", 50, finalServer
                        ) {
                            messagesManager.setValueAndRetry(
                                textChannel,
                                id,
                                "descr",
                                "Maintenant le message d' arrivé sur votre serveur : ",
                                500,
                                finalServer
                            ) {
                                val embedBuilder = EmbedBuilder()
                                    .setTitle("Les mondes")
                                    .setDescription("Maintenant configurons le monde de votre serveur ...")
                                for (worldEnum in WorldEnum.values()) {
                                    val w = worldEnum.world
                                    embedBuilder.addField(
                                        w.name, """
     RP : ${w.nameRP}
     Nom à entrer : ${w.progName}
     Description : ${w.desc}
     """.trimIndent()
                                    )
                                }
                                messagesManager
                                    .setValueAndRetry(textChannel, id, "welcome", embedBuilder, 1500, finalServer) {
                                        messagesManager
                                            .addListener(textChannel, id, object : Consumer<MessageCreateEvent> {
                                                override fun accept(messageCreateEvent: MessageCreateEvent) {
                                                    try {
                                                        val world =
                                                            WorldEnum.valueOf(messageCreateEvent.messageContent).world
                                                        finalServer["world"] = world.progName
                                                        messageCreateEvent.message.reply("Configuration terminée !! Enfin ! (et moi j' ai fini de coder ça, maintenant c'est les lieux 😑). Faites -help place pour la suite. Vous pouvez modifier tout cela à n' importe quel moment avec config [what] [value] et voir la configuration avec -config info.")
                                                    } catch (e: IllegalArgumentException) {
                                                        messageCreateEvent.message.reply("Ceci n' est pas un monde valide")
                                                        val embedBuilder = EmbedBuilder()
                                                            .setTitle("Les mondes")
                                                            .setDescription("SVP lisez")
                                                        for (worldEnum in WorldEnum.values()) {
                                                            val w = worldEnum.world
                                                            embedBuilder.addField(
                                                                w.name, """
     RP : ${w.nameRP}
     Nom à entrer : ${w.progName}
     Description : ${w.desc}
     """.trimIndent()
                                                            )
                                                        }
                                                        messageCreateEvent.message.reply(embedBuilder)
                                                        messagesManager
                                                            .addListener(messageCreateEvent.channel, id, this)
                                                    }
                                                }
                                            })
                                    }
                            }
                        }
                    } else {
                        messageCreateEvent.message.reply("En continuant (tapez oui à la fin de la commande), vous vous engagez à fournir aux joueurs un serveur respectueux dans lequel ils peuvent s'intégrer ou continuer leur aventure de de bonnes conditions. Vous acceptez aussi que le bot puisse inviter des personne sur votre serveur")
                    }
                } else {
                    if (args.size > 1) {
                        if (args[1].equals("name", ignoreCase = true) && args.size > 2) {
                            val name = getStr(args)
                            server["namerp"] = name.toString()
                            messageCreateEvent.message.reply("Nom modifié")
                        } else if (args[1].equals("desc", ignoreCase = true) && args.size > 2) {
                            val name = getStr(args)
                            server["descr"] = name.toString()
                            messageCreateEvent.message.reply("Description modifiée")
                        } else if (args[1].equals("in", ignoreCase = true) && args.size > 2) {
                            setValue(messageCreateEvent, "welcome", "Message d' arrivé modifiée", 1500, server, args)
                            val name = getStr(args)
                            server["welcome"] = name.toString()
                            messageCreateEvent.message.reply("Message d' arrivé modifié")
                        } else {
                            messageCreateEvent.message.reply("Utilisez config [what] [value]. Possibilités de what :\n - name, peut être RP\n - desc, description\n - in, message d' arrivé")
                        }
                    }
                }
            } else {
                messageCreateEvent.message.reply("Bien essayé, mais vous ne pouvez pas configurer un serveur qui n'est pas le votre")
            }
        } else {
            messageCreateEvent.message.reply("Comment voulez vous configurer un serveur sans être dans un serveur ?")
        }
    }

    fun setValue(
        messageCreateEvent: MessageCreateEvent,
        prog_name: String?,
        message: String?,
        len: Int,
        serverBot: ServerBot,
        args: Array<String>
    ) {
        val value = getStr(args)
        val v = serverBot.testValueAndSet(len, value.toString(), prog_name)
        if (v == "") {
            messageCreateEvent.message.reply(message)
        } else {
            messageCreateEvent.message.reply(v)
        }
    }

    companion object {
        fun getStr(args: Array<String>): StringBuilder {
            val name = StringBuilder()
            for (i in 2 until args.size) {
                name.append(args[i])
                name.append(" ")
            }
            return name
        }
    }
}