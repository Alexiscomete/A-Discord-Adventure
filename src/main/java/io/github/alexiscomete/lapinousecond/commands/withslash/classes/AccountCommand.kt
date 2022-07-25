package io.github.alexiscomete.lapinousecond.commands.withslash.classes

import io.github.alexiscomete.lapinousecond.commands.withslash.Command
import io.github.alexiscomete.lapinousecond.commands.withslash.ExecutableWithArguments
import io.github.alexiscomete.lapinousecond.commands.withslash.SubCommand
import io.github.alexiscomete.lapinousecond.config
import org.javacord.api.interaction.SlashCommandInteraction
import org.json.JSONObject
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import java.util.*

class AccountCommandBase : Command(
    "account",
    "Toutes les commandes liées à votre compte",
    "/account [verify/start/perms]",
    subCommands = listOf(
        AccountCommandVerify()
    )
)

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

class AccountCommandVerify() : SubCommand(
    "verify",
    "Vérifier vos coordonnées (bot de Sylicium)"
),
    ExecutableWithArguments {
    override val fullName: String
        get() = "account verify"
    override val botPerms: Array<String>?
        get() = null

    override fun execute(slashCommand: SlashCommandInteraction) {
        val player = getAccount(slashCommand)
        val userData = getUserData(slashCommand.user.id)
        if (userData.hasAccount()) {
            player["x"] = userData.x.toString()
            player["x"] = userData.y.toString()
            player["has_account"] = userData.hasAccount().toString()
            player["is_verify"] = if (userData.isVerify) "1" else "0"
            if (userData.isVerify) {
                slashCommand.createImmediateResponder().setContent("Votre compte a été associé à votre pixel. Vous avez la vérification").respond()
            } else {
                slashCommand.createImmediateResponder().setContent("Votre compte a été associé à votre pixel. Vous n'avez malheuresement pas la vérification 😕").respond()
            }
        } else {
            slashCommand.createImmediateResponder().setContent("Vous n'avez pas encore de compte de pixel, utilisez le bot de Sylicium").respond()
        }
    }

}