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
                if (args.size < 3 || args[2] == "bal") {
                    val resultSet = saveManager.executeQuery("SELECT * FROM players ORDER BY bal LIMIT 10", true)
                    val players = ArrayList<Player>()
                    try {
                        if (resultSet != null) {
                            while (resultSet.next()) {
                                players.add(
                                    Player(
                                        resultSet.getLong("id"),
                                    )
                                )
                            }
                        }
                    } catch (e: SQLException) {
                        e.printStackTrace()
                    }
                    val embedBuilder = EmbedBuilder().setTitle("Classement des joueurs par bal").setColor(Color.CYAN)
                    val top = arrayOf("")
                    val ints = intArrayOf(players.size)
                    for (player in players) {
                        api.getUserById(player.id).thenAccept { user: User ->
                            println("...")
                            ints[0]--
                            top[0] = """${user.name} -> ${player["bal"]}
${top[0]}"""
                            if (ints[0] == 0) {
                                embedBuilder.setDescription(top[0])
                                messageCreateEvent.message.reply(embedBuilder)
                            }
                        }
                    }
                } else {
                    messageCreateEvent.message.reply("Seul le classement par bal est disponible pour le moment")
                }
            } else {
                if (args[1].startsWith("<@")) {
                    args[1] = args[1].substring(2, args[1].length - 1)
                }
                try {
                    val p = players[args[1].toLong()]
                    p?.let { invOf(it, messageCreateEvent) }
                        ?: messageCreateEvent.message.reply("Cette personne n'a pas encore de compte")
                } catch (e: NumberFormatException) {
                    messageCreateEvent.message.reply("Pour voir l'inventiare d'une personne, vous devez indiquer son id ou la mentionner")
                }
            }
        } else {
            val p = players[messageCreateEvent.messageAuthor.id]
            if (p == null) {
                messageCreateEvent.message.reply("Vous devez d'abord faire la commande start avant de continuer")
            } else {
                invOf(p, messageCreateEvent)
                val tuto = p["tuto"].toInt()
                if (tuto == 1) {
                    messageCreateEvent.message.reply("Bon ... comme vous l'avez vu vous n'avez normalement pas d'argent. Utilisez la commande `work` pour en gagner un peu ...")
                    p["tuto"] = "3"
                } else if (tuto == 4) {
                    messageCreateEvent.message.reply("Vous remarquerez quelques changements. Utilisez -shop pour échanger ce que vous avez récupéré")
                    p["tuto"] = "5"
                }
            }
        }
    }

    private fun invOf(p: Player, messageCreateEvent: MessageCreateEvent) {
        val builder =
            EmbedBuilder()
                .setDescription("Serveur actuel : ${if (p["serv"] == "") "serveur inconnu, utilisez -hub" else p["serv"]}")
                .setTitle("Infos joueur")
                .setAuthor(messageCreateEvent.messageAuthor)
                .setTimestampToNow()
                .addField(
                    "Pixel", """
     Compte sur l'ORU : ${if (p["has_account"] == "1") "oui" else "non"}
     Vérification : ${if (p["is_verify"] == "1") "oui" else "non"}
     Pixel : ${if (p["x"] == "" || p["x"].toInt() == -1) "pixel inconnu" else "[" + p["x"] + ":" + p["y"] + "]"}
     """.trimIndent(), true
                )
                .addField("Position", p.positionToString())
                .setColor(Color.green)
                .setThumbnail("https://cdn.discordapp.com/attachments/854322477152337920/924612939879702588/unknown.png")
        messageCreateEvent.message.reply(builder)
        val re = StringBuilder().append("Nom -> quantité\n")
        for (reM in p.resourceManagers.values) {
            re.append(reM.resource.name_).append(" -> ").append(reM.quantity).append("\n")
        }
        val builder2 = EmbedBuilder().setTitle("Inventaire : ressources, items, argent").setColor(Color.ORANGE)
            .addField("Rabbitcoins", p["bal"], true).addField("Ressources", re.toString())
        messageCreateEvent.message.reply(builder2)
    }
}