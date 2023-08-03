package io.github.alexiscomete.lapinousecond.view.discord.commands.classes

import io.github.alexiscomete.lapinousecond.data.PERMS
import io.github.alexiscomete.lapinousecond.data.TutoSteps
import io.github.alexiscomete.lapinousecond.data.UserPerms
import io.github.alexiscomete.lapinousecond.data.managesave.fromBooleanToString
import io.github.alexiscomete.lapinousecond.data.managesave.saveManager
import io.github.alexiscomete.lapinousecond.entity.entities.PlayerData
import io.github.alexiscomete.lapinousecond.entity.entities.PlayerManager
import io.github.alexiscomete.lapinousecond.view.discord.commands.Command
import io.github.alexiscomete.lapinousecond.view.discord.commands.ExecutableWithArguments
import io.github.alexiscomete.lapinousecond.view.discord.commands.SubCommand
import io.github.alexiscomete.lapinousecond.worlds.WorldEnum
import org.javacord.api.entity.message.embed.EmbedBuilder
import org.javacord.api.interaction.SlashCommandInteraction
import org.javacord.api.interaction.SlashCommandOption
import org.javacord.api.interaction.SlashCommandOptionChoice
import org.javacord.api.interaction.SlashCommandOptionType
import java.awt.Color

const val MAIN_SERVER_ID = 854288660147994634

fun toSpawn(p: PlayerData) {
    p["serv"] = MAIN_SERVER_ID.toString()
    p["world"] = "TUTO"
    p["place_TUTO_x"] = WorldEnum.TUTO.defaultX.toString()
    p["place_TUTO_y"] = WorldEnum.TUTO.defaultY.toString()
    p["place_TUTO_type"] = "coos"
    p["place_TUTO_zoom"] = "ZOOM_OUT"
}

class AccountCommandBase : Command(
    "account",
    "Toutes les commandes liées à votre compte",
    subCommands = listOf(
        AccountCommandVerify(),
        AccountCommandStart(),
        AccountCommandPerms()
    )
)

class AccountCommandVerify : SubCommand(
    "verify",
    "Vérifier vos coordonnées (bot de Sylicium)"
),
    ExecutableWithArguments {
    override val fullName: String
        get() = "account verify"
    override val botPerms: Array<String>?
        get() = null

    override fun execute(slashCommand: SlashCommandInteraction) {
        slashCommand.createImmediateResponder()
            .setContent("Le bot de Sylicium n'est plus disponible pour une durée indéterminée. ||(Peut-être pour toujours)||")
            .respond()
    }
}

class AccountCommandStart : SubCommand(
    "start",
    "Créer votre compte sur le bot et faire le tuto"
),
    ExecutableWithArguments {
    override val fullName: String
        get() = "account start"
    override val botPerms: Array<String>?
        get() = null

    override fun execute(slashCommand: SlashCommandInteraction) {
        var p = PlayerManager.getOrNull(slashCommand.user.id)
        var content = ""
        if (p == null) {
            if ((slashCommand.server.isPresent && slashCommand.server.get().id != MAIN_SERVER_ID) || !slashCommand.server.isPresent) {
                content += "Vous devrez rejoindre le serveur discord principal à un moment ou à un autre pour le tuto.\n"
            }
            val user = slashCommand.user
            content += "Le bot pour vérifier votre pixel n'est plus disponible. Si vous souhaitez associez votre pixel, contacter le dev.\n"
            p = PlayerManager.createAccount(user.id)
            p.playerData["bal"] = "0.0"
        }
        val data = p.playerData
        toSpawn(data)
        if (slashCommand.server.isPresent) {
            data["serv"] = slashCommand.server.get().id.toString()
        }
        data["tuto"] = TutoSteps.STEP_INVENTORY_EMPTY.number
        val embed = EmbedBuilder()
            .setColor(Color.CYAN)
            .setTitle("Une nouvelle aventure commence")
            .setFooter("Bonne chance !")
            .addField(
                "Un rêve entêtant",
                "*Vous vous réveillez un matin après un rêve sur le Wumpus d'or. Vous décidez de partir à la recherche de cette légende ...*\n"
            )
            .addField(
                "Bienvenue dans A Discord Adventure !",
                "Une aventure ... mais sur plusieurs serveurs ? Prêt.e ? Il existe une histoire principale commune à tout les serveurs, mais chaque serveur peut aussi avoir sa propre histoire ! *Ceci est de la narration*. Vous pouvez voyager **de serveur en serveur** quand le bot vous envoie une **invitation** !"
            )
            .addField(
                "Signalement",
                "Les serveurs sont uniquement sur le thème du **Dibistan**. Si vous voyez malgré tout un abus signalez le sur le **serveur principal du bot**."
            )
            .addField(
                "Commençons le tuto",
                "> (???) : Bonjour ! Je suis **Aurimezi**, je suis ton nouveau inventaire. Ce n'est pas tout les jours qu'on achète un inventaire. Voyons voir ce qu'on a là ...\n\nUtilisez `/inv resource`"
            )
        if (content == "") {
            slashCommand.createImmediateResponder()
                .addEmbed(embed)
                .respond()
        } else {
            slashCommand.createImmediateResponder()
                .addEmbed(embed)
                .setContent(content)
                .respond()
        }
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
    override val botPerms: Array<String>
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
            what["play"] = fromBooleanToString(userPerms.play)
            what["create_server"] = fromBooleanToString(userPerms.createServer)
            what["manager_perms"] = fromBooleanToString(userPerms.managePerms)
            what["manager_roles"] = fromBooleanToString(userPerms.manageRoles)
            saveManager.insert("perms", what)
        }
        if (permission.equals("manage_perms", ignoreCase = true) && !slashCommand.user.isBotOwner) {
            throw IllegalStateException("Impossible .... vous devez être l'owner du bot pour modifier cette permission")
        }

        saveManager.setValue(
            PERMS,
            user.id,
            permission,
            fromBooleanToString(value)
        )

        slashCommand.createImmediateResponder()
            .setContent("Permission changée !")
            .respond()
    }
}