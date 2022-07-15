package io.github.alexiscomete.lapinousecond.commands.withoutslash.classes

import io.github.alexiscomete.lapinousecond.PERMS
import io.github.alexiscomete.lapinousecond.*
import io.github.alexiscomete.lapinousecond.useful.managesave.saveManager
import io.github.alexiscomete.lapinousecond.commands.withoutslash.CommandBot
import io.github.alexiscomete.lapinousecond.useful.managesave.SaveManager
import org.javacord.api.event.message.MessageCreateEvent
import kotlin.Array
import kotlin.String

class PermsManager : CommandBot(
    "Commande permettant de modifier les permissions d'un utilisateur dans le bot",
    "pm",
    "pm [user] [perm name] [true/false]; perms : PLAY, CREATE_SERVER, SET_SERVER_SEC, MANAGE_PERMS",
    "MANAGE_PERMS"
) {
    override fun execute(messageCreateEvent: MessageCreateEvent, content: String, args: Array<String>) {
        if (args.size < 4) {
            messageCreateEvent.message.reply("pm [user] [perm name] [true/false]")
            return
        }
        val value = (args[3]).toBoolean()
        val perm = args[2]
        try {
            val userPerms = UserPerms(args[1].toLong())
            if (userPerms.isDefault) {
                val what = HashMap<String, String>()
                what["ID"] = messageCreateEvent.messageAuthor.id.toString()
                what["PLAY"] = SaveManager.toBooleanString(userPerms.play)
                what["CREATE_SERVER"] = SaveManager.toBooleanString(userPerms.createServer)
                what["MANAGE_PERMS"] = SaveManager.toBooleanString(userPerms.managePerms)
                saveManager.insert("perms", what)
            }
            if (perm.equals("MANAGE_PERMS", ignoreCase = true) && !messageCreateEvent.messageAuthor.isBotOwner) {
                messageCreateEvent.message.reply("Impossible .... vous devez être l'owner du bot pour modifier cette permission")
                return
            }
            saveManager.setValue(
                PERMS,
                messageCreateEvent.messageAuthor.id,
                perm,
                SaveManager.toBooleanString(value)
            )
            messageCreateEvent.message.reply("Fait")
        } catch (e: NumberFormatException) {
            messageCreateEvent.message.reply("Vous devez indiquer un format de nombre valide pour l'argument user")
        }
    }
}