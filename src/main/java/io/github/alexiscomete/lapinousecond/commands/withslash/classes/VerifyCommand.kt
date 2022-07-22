package io.github.alexiscomete.lapinousecond.commands.withslash.classes

import io.github.alexiscomete.lapinousecond.commands.withoutslash.classes.Verify
import io.github.alexiscomete.lapinousecond.commands.withslash.Command
import io.github.alexiscomete.lapinousecond.commands.withslash.ExecutableWithArguments
import io.github.alexiscomete.lapinousecond.config
import io.github.alexiscomete.lapinousecond.entity.players
import org.javacord.api.interaction.SlashCommandInteraction
import org.json.JSONObject
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import java.util.*

fun getUser(id: Long): String? {
    try {
        val connection =
            URL("https://dirtybiology.captaincommand.repl.co/api/?authorization=${config.content[2]}&request=getInfosByDiscordId&datas=%7B%22discordId%22:%22$id%22%7D").openConnection() as HttpURLConnection
        connection.requestMethod = "GET"
        var response = ""
        val scanner = Scanner(connection.inputStream)
        if (scanner.hasNextLine()) {
            response += scanner.nextLine()
        }
        scanner.close()
        return response
    } catch (e: IOException) {
        e.printStackTrace()
    }
    return null
}

fun getUserData(id: Long): UserData {
    val userData = getUser(id)
    println("Data :")
    println(userData)
    if (userData != null) {
        val jsonObject = JSONObject(userData)
        val back = jsonObject.getJSONObject("back")
        if (!back.isEmpty) {
            val member = back.getJSONObject("member")
            val verified = member.getBoolean("verified")
            return if (verified) {
                val jsonArray = member.getJSONArray("coordinatesVerified")
                UserData(jsonArray.getInt(0), jsonArray.getInt(1), isVerify = true, true)
            } else {
                val jsonArray = member.getJSONArray("coordinatesUnverified")
                UserData(jsonArray.getInt(0), jsonArray.getInt(1), isVerify = false, true)
            }
        }
    }
    return UserData(-1, -1, isVerify = false, false)
}

class UserData(val x: Int, val y: Int, val isVerify: Boolean, private val hasAccount: Boolean) {
    fun hasAccount(): Boolean {
        return hasAccount
    }
}

class VerifyCommandBase() : Command(
    "verify",
    "Vérifier vos coordonnées (bot de Sylicium)",
    "verify"
),
    ExecutableWithArguments {

    override val botPerms: Array<String>?
        get() = null

    override fun execute(slashCommand: SlashCommandInteraction) {
        val player = getAccount(slashCommand)
        slashCommand.createImmediateResponder().setContent("Votre vérification est en cours").respond()
        val userData = getUserData(messageCreateEvent.messageAuthor.id)
        if (userData.hasAccount()) {
            val player = players[messageCreateEvent.messageAuthor.id]
            if (player != null) {
                player["x"] = userData.x.toString()
                player["x"] = userData.y.toString()
                player["has_account"] = userData.hasAccount().toString()
                player["is_verify"] = if (userData.isVerify) "1" else "0"
                if (userData.isVerify) {
                    messageCreateEvent.message.reply("Votre compte a été associé à votre pixel. Vous avez la vérification")
                } else {
                    messageCreateEvent.message.reply("Votre compte a été associé à votre pixel. Vous n'avez malheuresement pas la vérification 😕")
                }
            }

        } else {
            messageCreateEvent.message.reply("Vous n'avez pas encore de compte avec l'ORU")
        }
    }

}