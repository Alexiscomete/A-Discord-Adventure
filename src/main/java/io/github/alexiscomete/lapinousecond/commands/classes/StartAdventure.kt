package io.github.alexiscomete.lapinousecond.commands.classes

import io.github.alexiscomete.lapinousecond.Main
import io.github.alexiscomete.lapinousecond.commands.CommandBot
import io.github.alexiscomete.lapinousecond.commands.classes.Verify.Companion.getUserData
import io.github.alexiscomete.lapinousecond.entity.Player
import io.github.alexiscomete.lapinousecond.save.SaveManager
import org.javacord.api.event.message.MessageCreateEvent

class StartAdventure : CommandBot(
    "Permet de commencer l'aventure",
    "start",
    "Vous permet de créer votre compte sur le bot et de commencer l'aventure avec un tuto, vous pouvez réexécuter cette commande pour revoir le tuto (par exemple dans le cas d'une mise à jour importante)",
    "PLAY"
) {
    override fun execute(messageCreateEvent: MessageCreateEvent, content: String, args: Array<String>) {
        val saveManager = Main.getSaveManager()
        var p = saveManager.players[messageCreateEvent.messageAuthor.id]
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
                    what["x"] = userData.x.toString()
                    what["y"] = userData.y.toString()
                    what["has_account"] = SaveManager.toBooleanString(userData.hasAccount())
                    what["is_verify"] = SaveManager.toBooleanString(userData.isVerify)
                    val user = msga.get()
                    what["id"] = user.id.toString()
                    what["bal"] = 0.toString()
                    what["serv"] = 854288660147994634L.toString()
                    what["tuto"] = 1.toString()
                    saveManager.insert("players", what)
                    p = Player(user.id, 0.0, 854288660147994634L, 1.toShort(), userData.hasAccount(), "", "")
                    saveManager.players.hashMap[user.id] = p
                    //TODO modifier lore
                    messageCreateEvent.message.reply("*Vous vous réveillez un matin après un rêve sur le Wumpus d'or. Vous décidez de partir à la recherche de cette légende ...*\nBienvenue dans A Discord Adventure !\nPrêt vivre une aventure se déroulant sur plusieurs serveurs ? Le principe est simple : il existe une histoire principale commune à tout les serveurs, mais chaque serveur peut aussi avoir sa propre histoire plus ou moins configurable ! Les textes RP serons le plus souvent en *italique*. Vous pouvez voyager **de serveur en serveur** quand le bot vous envoie une **invitation**, le plus souvent après avoir **acheté** par exemple un **ticket** pour voyager sur un bateau !\nLes serveurs sont uniquement sur le thème de la **RPDB**, et le bot ne peut être configuré que par des **personnes autorisées**. Si vous voyez malgré tout un abus signalez le sur le **serveur principal du bot**. Commençont le tuto ... tapez la commande `ìnv`")
                }
            }
        } else {
            messageCreateEvent.message.reply("La reprise du tutoriel après le début de la partie n'est pas encore disponible !")
        }
    }
}