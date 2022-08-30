package io.github.alexiscomete.lapinousecond.commands.withslash.classes

import io.github.alexiscomete.lapinousecond.api
import io.github.alexiscomete.lapinousecond.commands.withslash.Command
import io.github.alexiscomete.lapinousecond.commands.withslash.ExecutableWithArguments
import io.github.alexiscomete.lapinousecond.commands.withslash.SubCommand
import io.github.alexiscomete.lapinousecond.commands.withslash.getAccount
import io.github.alexiscomete.lapinousecond.entity.Player
import io.github.alexiscomete.lapinousecond.entity.players
import io.github.alexiscomete.lapinousecond.resources.Resource
import io.github.alexiscomete.lapinousecond.useful.managesave.saveManager
import org.javacord.api.entity.message.embed.EmbedBuilder
import org.javacord.api.interaction.SlashCommandInteraction
import org.javacord.api.interaction.SlashCommandOption
import org.javacord.api.interaction.SlashCommandOptionType
import java.awt.Color
import java.sql.SQLException

fun who(slashCommand: SlashCommandInteraction): Player {
    val arguments = slashCommand.arguments

    val userArg = arguments.find { it.name == "player" }

    return if (userArg != null) {
        val userOp = userArg.userValue
        if (userOp.isPresent) {
            val user = userOp.get()
            try {
                players[user.id]
                    ?: throw IllegalStateException("Cette personne n'a pas de compte sur le bot")
            } catch (e: Exception) {
                throw IllegalStateException("Cette personne n'a pas de compte sur le bot")
            }
        } else {
            getAccount(slashCommand)
        }
    } else {
        getAccount(slashCommand)
    }
}

class InvCommandBase : Command(
    "inv",
    "Permet de voir l'inventaire du joueur de diverses manières",
    "inv [infos/items/resources/top]",
    subCommands = listOf(
        InvCommandInfos(),
        InvCommandItems(),
        InvCommandResources(),
        InvCommandTop()
    )
)

class InvCommandInfos : SubCommand(
    "infos",
    "Permet d'afficher vos informations générales",
    arrayListOf(
        SlashCommandOption.create(SlashCommandOptionType.USER, "player", "Spécifier un joueur si ce n'est pas vous", false)
    )
), ExecutableWithArguments {
    override val fullName: String
        get() = "inv infos"
    override val botPerms: Array<String>?
        get() = null

    override fun execute(slashCommand: SlashCommandInteraction) {

        val player = who(slashCommand)

        val builder =
            EmbedBuilder()
                .setDescription("Serveur actuel : ${if (player["serv"] == "") "serveur inconnu, utilisez -hub" else player["serv"]}")
                .setTitle("Infos joueur")
                .setAuthor(slashCommand.user)
                .setTimestampToNow()
                .addField(
                    "Pixel", """
     Compte sur l'ORU : ${if (player["has_account"] == "1") "oui" else "non"}
     Vérification : ${if (player["is_verify"] == "1") "oui" else "non"}
     Pixel : ${if (player["x"] == "" || player["x"].toInt() == -1) "pixel inconnu" else "[" + player["x"] + ":" + player["y"] + "]"}
     """.trimIndent(), true
                )
                .addField("Position", player.positionToString())
                .setColor(Color.green)
                .setThumbnail("https://cdn.discordapp.com/attachments/854322477152337920/924612939879702588/unknown.png")

        slashCommand.createImmediateResponder()
            .addEmbed(builder)
            .respond()
    }
}

class InvCommandItems : SubCommand(
    "items",
    "Permet d'afficher vos items",
    arrayListOf(
        SlashCommandOption.create(SlashCommandOptionType.USER, "player", "Spécifier un joueur si ce n'est pas vous", false)
    )
), ExecutableWithArguments {
    override val fullName: String
        get() = "inv items"
    override val botPerms: Array<String>?
        get() = null

    override fun execute(slashCommand: SlashCommandInteraction) {

        //val player = who(slashCommand)

        val embed = EmbedBuilder()
            .setTitle("Items")
            .setTimestampToNow()
            .setFooter("Les items sont utilisables et ne fusionnent pas dans l'inventaire contrairement aux ressources")
            .setColor(Color.BLUE)
            .setThumbnail("https://cdn.discordapp.com/attachments/854322477152337920/924612939879702588/unknown.png")

        embed.setDescription("Le système d'items n'est pas encore implémenté")

        slashCommand.createImmediateResponder()
            .addEmbed(embed)
            .respond()
    }
}

class InvCommandResources : SubCommand(
    "resources",
    "Permet d'afficher vos ressources",
    arrayListOf(
        SlashCommandOption.create(SlashCommandOptionType.USER, "player", "Spécifier un joueur si ce n'est pas vous", false)
    )
), ExecutableWithArguments {
    override val fullName: String
        get() = "inv resources"
    override val botPerms: Array<String>?
        get() = null

    override fun execute(slashCommand: SlashCommandInteraction) {
        val player: Player = who(slashCommand)

        var content = ""

        val tuto = player["tuto"].toInt()
        if (tuto == 1) {
            content += "Bon ... comme vous l'avez vu vous n'avez normalement pas d'argent. Utilisez la commande `work` pour en gagner un peu ...\n"
            player["tuto"] = "3"
        } else if (tuto == 4) {
            content += "Vous remarquerez quelques changements. Utilisez -shop pour échanger ce que vous avez récupéré\n"
            player["tuto"] = "5"
        }

        val embed = EmbedBuilder()

        val re = StringBuilder().append("Cliquez sur une resource pour voir son nom\n")
        for (reM in player.resourceManagers.values) {
            re
                .append(reM.resource.name_)
                .append(" ")
                .append(reM.quantity)
                .append("\n")
        }

        embed
            .setTitle("Inventaire : ressources, items, argent")
            .setColor(Color.ORANGE)
            .addField("Rabbitcoins", player["bal"] + Resource.RABBIT_COIN.name_, true)
            .addField("Ressources", re.toString())
            .setThumbnail("https://cdn.discordapp.com/attachments/854322477152337920/924612939879702588/unknown.png")

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

class InvCommandTop : SubCommand(
    "top",
    "Permet de voir le top des joueurs",
    arrayListOf(
        SlashCommandOption.create(SlashCommandOptionType.USER, "player", "Spécifier un joueur si ce n'est pas vous", false)
    )
), ExecutableWithArguments {
    override val fullName: String
        get() = "inv top"
    override val botPerms: Array<String>?
        get() = null

    override fun execute(slashCommand: SlashCommandInteraction) {
        val pl: Player = who(slashCommand)

        val embed = EmbedBuilder()
            .setTitle("Classement des joueurs en fonction du nombre de ${Resource.RABBIT_COIN.name_}")
            .setColor(Color.ORANGE)
            .setTimestampToNow()

        // top 10
        val resultSet = saveManager.executeQuery("SELECT * FROM players ORDER BY (bal + 0.0) ASC LIMIT 10", true)

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

        // position
        val resultSet2 = saveManager.executeQuery("SELECT count(*) FROM players WHERE bal > ${pl["bal"]}", true)

        var position = 0
        try {
            if (resultSet2 != null) {
                while (resultSet2.next()) {
                    position = resultSet2.getInt(1) + 1
                }
            }
        } catch (e: SQLException) {
            e.printStackTrace()
        }


        var top = ""
        for (player in players) {
            val user = api.getUserById(player.id).join()
            top = "${user.name} ${player["bal"]} ${Resource.RABBIT_COIN.name_}\n${top}"
        }

        embed
            .setDescription(top)
            .setFooter("Vous êtes en position $position")

        slashCommand.createImmediateResponder()
            .addEmbed(embed)
            .respond()
    }
}