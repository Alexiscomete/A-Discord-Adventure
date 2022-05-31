package io.github.alexiscomete.lapinousecond.commands.classes

import io.github.alexiscomete.lapinousecond.commands.CommandBot
import org.javacord.api.event.message.MessageCreateEvent
import org.json.JSONObject
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import java.util.*

class Verify :
    CommandBot("Permet de vérifier votre compte", "verify", "Permet de vérifier votre compte grâce au bot de l'ORU") {
    override fun execute(messageCreateEvent: MessageCreateEvent, content: String, args: Array<String>) {
        if (saveManager.players[messageCreateEvent.messageAuthor.id] != null) {
            messageCreateEvent.message.reply("Votre vérification est en cours")
            val userData = getUserData(messageCreateEvent.messageAuthor.id)
            if (userData.hasAccount()) {
                val player = saveManager.players[messageCreateEvent.messageAuthor.id]
                player!!.x = userData.x
                player.y = userData.y
                player.setHasAccount(userData.hasAccount())
                player.isVerify = userData.isVerify
                if (userData.isVerify) {
                    messageCreateEvent.message.reply("Votre compte a été associé à votre pixel. Vous avez la vérification")
                } else {
                    messageCreateEvent.message.reply("Votre compte a été associé à votre pixel. Vous n'avez malheuresement pas la vérification 😕")
                }
            } else {
                messageCreateEvent.message.reply("Vous n'avez pas encore de compte avec l'ORU")
            }
        } else {
            messageCreateEvent.message.reply("Utilisez -start")
        }
    }

    class UserData(val x: Int, val y: Int, val isVerify: Boolean, private val hasAccount: Boolean) {
        fun hasAccount(): Boolean {
            return hasAccount
        }
    }

    companion object {
        private fun getUser(id: Long): String? {
            try {
                val connection =
                    URL("https://dirtybiology.captaincommand.repl.co/api/?authorization=mXpn9frxWJh0RPjZYSPMilfnK5ooxjhL&request=getInfosByDiscordId&datas=%7B%22discordId%22:%22$id%22%7D").openConnection() as HttpURLConnection
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

        @JvmStatic
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
                        UserData(jsonArray.getInt(0), jsonArray.getInt(1), true, true)
                    } else {
                        val jsonArray = member.getJSONArray("coordinatesUnverified")
                        UserData(jsonArray.getInt(0), jsonArray.getInt(1), false, true)
                    }
                }
            }
            return UserData(-1, -1, false, false)
        }
    }
}