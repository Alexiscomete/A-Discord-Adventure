package io.github.alexiscomete.lapinousecond.commands.withoutslash.classes

import io.github.alexiscomete.lapinousecond.commands.withoutslash.CommandBot
import io.github.alexiscomete.lapinousecond.commands.withslash.classes.getUserData
import io.github.alexiscomete.lapinousecond.entity.players
import io.github.alexiscomete.lapinousecond.useful.managesave.SaveManager
import io.github.alexiscomete.lapinousecond.useful.managesave.saveManager
import org.javacord.api.entity.message.embed.EmbedBuilder
import org.javacord.api.event.message.MessageCreateEvent
import java.awt.Color

class StartAdventure : CommandBot(
    "Permet de commencer l'aventure",
    "start",
    "Vous permet de créer votre compte sur le bot et de commencer l'aventure avec un tuto, vous pouvez réexécuter cette commande pour revoir le tuto (par exemple dans le cas d'une mise à jour importante)",
    "PLAY"
) {
    override fun execute(messageCreateEvent: MessageCreateEvent, content: String, args: Array<String>) {
        var p = players[messageCreateEvent.messageAuthor.id]
        if (p == null) {
            if (messageCreateEvent.isPrivateMessage || messageCreateEvent.server.get().id != 854288660147994634L) {
                messageCreateEvent.message.reply("🙄 Ce bot propose une aventure se déroulant sur de nombreux serveurs, mais elle commence toujours sur le serveur du bot  (vous pourrez le quitter après) : <https://discord.gg/q4hVQ6gwyx>")
            } else {
                val msga = messageCreateEvent.messageAuthor.asUser()
                if (msga.isPresent) {
                    val what = HashMap<String, String>()
                    val userData = getUserData(messageCreateEvent.messageAuthor.id)
                    if (userData.hasAccount()) {
                        if (userData.isVerify) {
                            messageCreateEvent.message.reply("Votre compte va être associé à votre pixel. Vous avez la vérification")
                        } else {
                            messageCreateEvent.message.reply("Votre compte va être associé à votre pixel. Vous n'avez malheuresement pas la vérification 😕")
                        }
                    } else {
                        messageCreateEvent.message.reply("Aucun compte de pixel trouvé")
                    }
                    val user = msga.get()
                    what["id"] = user.id.toString()
                    saveManager.insert("players", what)
                    p = players[user.id]
                    if (p == null) {
                        throw IllegalStateException("Player not found")
                    }
                    p["x"] = userData.x.toString()
                    p["y"] = userData.y.toString()
                    p["has_account"] = SaveManager.toBooleanString(userData.hasAccount())
                    p["is_verify"] = SaveManager.toBooleanString(userData.isVerify)
                    p["bal"] = "0.0"
                    p["serv"] = "854288660147994634"
                    p["tuto"] = "1"
                    players.hashMap[user.id] = p
                    val embed = EmbedBuilder()
                        .setColor(Color.CYAN)
                        .setTitle("Une nouvelle aventure commence")
                        .setFooter("Bonne chance !")
                        .addField("Un rêve entêtant",
                            "*Vous vous réveillez un matin après un rêve sur le Wumpus d'or. Vous décidez de partir à la recherche de cette légende ...*\n")
                        .addField(
                        "Bienvenue dans A Discord Adventure !\n",
                                "Prêt vivre une aventure se déroulant sur plusieurs serveurs ? Le principe est simple : il existe une histoire principale commune à tout les serveurs, mais chaque serveur peut aussi avoir sa propre histoire plus ou moins configurable ! De nombreuses autres fonctionnalités sont disponibles. Les textes RP serons le plus souvent en *italique*. Vous pouvez voyager **de serveur en serveur** quand le bot vous envoie une **invitation**, le plus souvent après avoir **acheté** par exemple un **ticket** pour voyager sur un bateau !\n")
                        .addField("Signalement", "Les serveurs sont uniquement sur le thème du **Dibistan**. Si vous voyez malgré tout un abus signalez le sur le **serveur principal du bot**.")
                        .addField("Commençons le tuto", "Tapez la commande `inv`")
                    messageCreateEvent.message.reply(embed)
                }
            }
        } else {
            messageCreateEvent.message.reply("La reprise du tutoriel après le début de la partie n'est pas encore disponible !")
        }
    }
}