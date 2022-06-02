package io.github.alexiscomete.lapinousecond.commands.classes

import io.github.alexiscomete.lapinousecond.Main
import io.github.alexiscomete.lapinousecond.commands.CommandBot
import io.github.alexiscomete.lapinousecond.save.SaveManager
import io.github.alexiscomete.lapinousecond.save.Tables
import org.javacord.api.event.message.MessageCreateEvent
import java.lang.Boolean
import kotlin.Array
import kotlin.NumberFormatException
import kotlin.String

class PermsManager : CommandBot(
    "Commande permettant de modifier les permissions d'un utilisateur dans le bot",
    "pm",
    "pm [user] [perm name] [true/false]; perms : PLAY, CREATE_SERVER, SET_SERVER_SEC, MANAGE_PERMS",
    "MANAGE_PERMS"
) {
    override fun execute(messageCreateEvent: MessageCreateEvent, content: String, args: Array<String>) {
        val saveManager = Main.getSaveManager()
        if (args.size < 4) {
            messageCreateEvent.message.reply("pm [user] [perm name] [true/false]")
            return
        }
        val value = Boolean.parseBoolean(args[3])
        val perm = args[2]
        try {
            val userPerms = saveManager.getPlayerPerms(args[1].toLong())
            if (userPerms.isDefault) {
                val what = HashMap<String, String>()
                what["ID"] = messageCreateEvent.messageAuthor.id.toString()
                what["PLAY"] = SaveManager.toBooleanString(userPerms.PLAY)
                what["CREATE_SERVER"] = SaveManager.toBooleanString(userPerms.CREATE_SERVER)
                what["MANAGE_PERMS"] = SaveManager.toBooleanString(userPerms.MANAGE_PERMS)
                saveManager.insert("perms", what)
            }
            if (perm.equals("MANAGE_PERMS", ignoreCase = true) && !messageCreateEvent.messageAuthor.isBotOwner) {
                messageCreateEvent.message.reply("Impossible .... vous devez être l'owner du bot pour modifier cette permission")
                return
            }
            saveManager.setValue(
                Tables.PERMS.table,
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