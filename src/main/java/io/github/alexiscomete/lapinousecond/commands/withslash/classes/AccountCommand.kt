package io.github.alexiscomete.lapinousecond.commands.withslash.classes

import io.github.alexiscomete.lapinousecond.PERMS
import io.github.alexiscomete.lapinousecond.UserPerms
import io.github.alexiscomete.lapinousecond.commands.withslash.Command
import io.github.alexiscomete.lapinousecond.commands.withslash.ExecutableWithArguments
import io.github.alexiscomete.lapinousecond.commands.withslash.SubCommand
import io.github.alexiscomete.lapinousecond.config
import io.github.alexiscomete.lapinousecond.entity.players
import io.github.alexiscomete.lapinousecond.useful.managesave.SaveManager
import io.github.alexiscomete.lapinousecond.useful.managesave.saveManager
import io.github.alexiscomete.lapinousecond.worlds.ServerBot
import org.javacord.api.entity.message.embed.EmbedBuilder
import org.javacord.api.interaction.SlashCommandInteraction
import org.javacord.api.interaction.SlashCommandOption
import org.javacord.api.interaction.SlashCommandOptionChoice
import org.javacord.api.interaction.SlashCommandOptionType
import org.json.JSONObject
import java.awt.Color
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import java.util.*

class AccountCommandBase : Command(
    "account",
    "Toutes les commandes liées à votre compte",
    "/account [verify/start/perms]",
    subCommands = listOf(
        AccountCommandVerify(),
        AccountCommandStart(),
        AccountCommandPerms()
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
                slashCommand.createImmediateResponder()
                    .setContent("Votre compte a été associé à votre pixel. Vous avez la vérification").respond()
            } else {
                slashCommand.createImmediateResponder()
                    .setContent("Votre compte a été associé à votre pixel. Vous n'avez malheuresement pas la vérification 😕")
                    .respond()
            }
        } else {
            slashCommand.createImmediateResponder()
                .setContent("Vous n'avez pas encore de compte de pixel, utilisez le bot de Sylicium").respond()
        }
    }
}

class AccountCommandStart() : SubCommand(
    "start",
    "Créer votre compte sur le bot et faire le tuto"
),
    ExecutableWithArguments {
    override val fullName: String
        get() = "account start"
    override val botPerms: Array<String>?
        get() = null

    override fun execute(slashCommand: SlashCommandInteraction) {
        var p = players[slashCommand.user.id]
        var content = ""
        if (p == null) {
            if ((slashCommand.server.isPresent && slashCommand.server.get().id != 854288660147994634L) || !slashCommand.server.isPresent) {
                content += "Le bot utilise un système de voyage entre les serveurs discord\nPour cette raison tout le monde spawn sur le serveur principal : https://discord.gg/q4hVQ6gwyx\nVous devez le rejoindre pour commencer. Vous pouvez le quitter après avoir fini le tuto\n"
            }
            val user = slashCommand.user
            val what = HashMap<String, String>()
            val userData = getUserData(slashCommand.user.id)
            if (userData.hasAccount()) {
                if (userData.isVerify) {
                    content += "Votre compte va être associé à votre pixel. Vous avez la vérification\n"
                } else {
                    content += "Votre compte va être associé à votre pixel. Vous n'avez malheuresement pas la vérification 😕\n"
                }
            } else {
                content += "Aucun compte de pixel trouvé sur le bot de Sylicium\n"
            }
            what["id"] = user.id.toString()
            saveManager.insert("players", what)
            p = players[user.id]
            if (p == null) {
                throw IllegalStateException("Player not found")
            }
            players.hashMap[user.id] = p
            p["x"] = userData.x.toString()
            p["y"] = userData.y.toString()
            p["has_account"] = SaveManager.toBooleanString(userData.hasAccount())
            p["is_verify"] = SaveManager.toBooleanString(userData.isVerify)
            p["bal"] = "0.0"
        }
        p["serv"] = "854288660147994634"
        p["world"] = "NORMAL"
        p["place_NORMAL"] = ServerBot(854288660147994634L).getString("places")
        p["tuto"] = "1"
        val embed = EmbedBuilder()
            .setColor(Color.CYAN)
            .setTitle("Une nouvelle aventure commence")
            .setFooter("Bonne chance !")
            .addField(
                "Un rêve entêtant",
                "*Vous vous réveillez un matin après un rêve sur le Wumpus d'or. Vous décidez de partir à la recherche de cette légende ...*\n"
            )
            .addField(
                "Bienvenue dans A Discord Adventure !\n",
                "Prêt vivre une aventure se déroulant sur plusieurs serveurs ? Le principe est simple : il existe une histoire principale commune à tout les serveurs, mais chaque serveur peut aussi avoir sa propre histoire plus ou moins configurable ! De nombreuses autres fonctionnalités sont disponibles. Les textes RP serons le plus souvent en *italique*. Vous pouvez voyager **de serveur en serveur** quand le bot vous envoie une **invitation**, le plus souvent après avoir **acheté** par exemple un **ticket** pour voyager sur un bateau !\n"
            )
            .addField(
                "Signalement",
                "Les serveurs sont uniquement sur le thème du **Dibistan**. Si vous voyez malgré tout un abus signalez le sur le **serveur principal du bot**."
            )
            .addField("Commençons le tuto", "Tapez la commande `inv`")
        slashCommand.createImmediateResponder()
            .addEmbed(embed)
            .setContent(content)
            .respond()
    }
}

class AccountCommandPerms : SubCommand(
    "perms",
    "Permet de modifier la permission globale d'un utilisateur du bot.",
    arguments = arrayListOf(
        SlashCommandOption.create(
            SlashCommandOptionType.USER,
            "user",
            "L'utilisateur à qui on veut changer la permission",
            true
        ),
        SlashCommandOption.createWithChoices(
            SlashCommandOptionType.STRING, "permission", "La permission à changer", true, listOf(
                SlashCommandOptionChoice.create("PLAY", "play"),
                SlashCommandOptionChoice.create("CREATE_SERVER", "create_server"),
                SlashCommandOptionChoice.create("MANAGE_PERMS", "manage_perms"),
                SlashCommandOptionChoice.create("MANAGE_ROLES", "manage_roles"),
            )
        ),
        SlashCommandOption.create(SlashCommandOptionType.BOOLEAN, "value", "Il a cette permission ?", true)
    )
), ExecutableWithArguments {
    override val fullName: String
        get() = "account perms"
    override val botPerms: Array<String>?
        get() = arrayOf("MANAGE_ROLES")

    override fun execute(slashCommand: SlashCommandInteraction) {
        val arguments = slashCommand.arguments

        // user
        val userArg = arguments.find { it.name == "user" }
            ?: throw IllegalArgumentException("Missing user argument")
        val opUser = userArg.userValue
        if (!opUser.isPresent) {
            throw IllegalArgumentException("User not found")
        }
        val user = opUser.get()

        // permission
        val permissionArg = arguments.find { it.name == "permission" }
            ?: throw IllegalArgumentException("Missing permission argument")
        val opPermission = permissionArg.stringValue
        if (!opPermission.isPresent) {
            throw IllegalArgumentException("Permission not found")
        }
        val permission = opPermission.get()

        // value
        val valueArg = arguments.find { it.name == "value" }
            ?: throw IllegalArgumentException("Missing value argument")
        val opValue = valueArg.booleanValue
        if (!opValue.isPresent) {
            throw IllegalArgumentException("Value not found")
        }
        val value: Boolean = opValue.get()

        val userPerms = UserPerms(user.id)
        if (userPerms.isDefault) {
            val what = HashMap<String, String>()
            what["id"] = user.id.toString()
            what["play"] = SaveManager.toBooleanString(userPerms.play)
            what["create_server"] = SaveManager.toBooleanString(userPerms.createServer)
            what["manager_perms"] = SaveManager.toBooleanString(userPerms.managePerms)
            what["manager_roles"] = SaveManager.toBooleanString(userPerms.manageRoles)
            saveManager.insert("perms", what)
        }
        if (permission.equals("manage_perms", ignoreCase = true) && !slashCommand.user.isBotOwner) {
            throw IllegalStateException("Impossible .... vous devez être l'owner du bot pour modifier cette permission")
        }

        saveManager.setValue(
            PERMS,
            user.id,
            permission,
            SaveManager.toBooleanString(value)
        )

        slashCommand.createImmediateResponder()
            .setContent("Permission changée !")
            .respond()
    }
}