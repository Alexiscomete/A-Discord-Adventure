package io.github.alexiscomete.lapinousecond.view.discord.commands.classes

import io.github.alexiscomete.lapinousecond.api
import io.github.alexiscomete.lapinousecond.entity.concrete.resources.Resource
import io.github.alexiscomete.lapinousecond.entity.entities.Player
import io.github.alexiscomete.lapinousecond.entity.entities.PlayerWithAccount
import io.github.alexiscomete.lapinousecond.entity.entities.players
import io.github.alexiscomete.lapinousecond.useful.managesave.saveManager
import io.github.alexiscomete.lapinousecond.view.contextFor
import io.github.alexiscomete.lapinousecond.view.discord.commands.Command
import io.github.alexiscomete.lapinousecond.view.discord.commands.ExecutableWithArguments
import io.github.alexiscomete.lapinousecond.view.discord.commands.SubCommand
import io.github.alexiscomete.lapinousecond.view.discord.commands.getAccount
import io.github.alexiscomete.lapinousecond.view.ui.longuis.inv.InvInfosUI
import io.github.alexiscomete.lapinousecond.view.ui.longuis.inv.InvItemsUI
import io.github.alexiscomete.lapinousecond.view.ui.longuis.inv.InvResourcesUI
import io.github.alexiscomete.lapinousecond.view.ui.playerui.DiscordPlayerUI
import org.javacord.api.entity.message.embed.EmbedBuilder
import org.javacord.api.interaction.Interaction
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
        SlashCommandOption.create(
            SlashCommandOptionType.USER,
            "player",
            "Spécifier un joueur si ce n'est pas vous",
            false
        )
    )
), ExecutableWithArguments {
    override val fullName: String
        get() = "inv infos"
    override val botPerms: Array<String>?
        get() = null

    override fun execute(slashCommand: SlashCommandInteraction) {
        val context = contextFor(PlayerWithAccount(slashCommand.user))
        val ui = DiscordPlayerUI(context, slashCommand as Interaction)
        ui.setLongCustomUI(
            InvInfosUI(
                ui
            )
        )
        ui.updateOrSend()
        context.ui(ui)
    }
}

class InvCommandItems : SubCommand(
    "items",
    "Permet d'afficher vos items",
    arrayListOf(
        SlashCommandOption.create(
            SlashCommandOptionType.USER,
            "player",
            "Spécifier un joueur si ce n'est pas vous",
            false
        )
    )
), ExecutableWithArguments {
    override val fullName: String
        get() = "inv items"
    override val botPerms: Array<String>?
        get() = null

    override fun execute(slashCommand: SlashCommandInteraction) {
        val context = contextFor(PlayerWithAccount(slashCommand.user))
        val ui = DiscordPlayerUI(context, slashCommand as Interaction)
        ui.setLongCustomUI(
            InvItemsUI(
                ui
            )
        )
        ui.updateOrSend()
        context.ui(ui)
    }
}

class InvCommandResources : SubCommand(
    "resources",
    "Permet d'afficher vos ressources",
    arrayListOf(
        SlashCommandOption.create(
            SlashCommandOptionType.USER,
            "player",
            "Spécifier un joueur si ce n'est pas vous",
            false
        )
    )
), ExecutableWithArguments {
    override val fullName: String
        get() = "inv resources"
    override val botPerms: Array<String>?
        get() = null

    override fun execute(slashCommand: SlashCommandInteraction) {
        val context = contextFor(PlayerWithAccount(slashCommand.user))
        val ui = DiscordPlayerUI(context, slashCommand as Interaction)
        ui.setLongCustomUI(
            InvResourcesUI(
                ui
            )
        )
        ui.updateOrSend()
        context.ui(ui)
    }
}

class InvCommandTop : SubCommand(
    "top",
    "Permet de voir le top des joueurs",
    arrayListOf(
        SlashCommandOption.create(
            SlashCommandOptionType.USER,
            "player",
            "Spécifier un joueur si ce n'est pas vous",
            false
        )
    )
), ExecutableWithArguments {
    override val fullName: String
        get() = "inv top"
    override val botPerms: Array<String>?
        get() = null

    override fun execute(slashCommand: SlashCommandInteraction) {
        val pl: Player = who(slashCommand)

        val embed = EmbedBuilder()
            .setTitle("Classement des joueurs en fonction du nombre de ${Resource.RABBIT_COIN.show}")
            .setColor(Color.ORANGE)
            .setTimestampToNow()

        // top 10
        val preparedStatement =
            saveManager.preparedStatement("SELECT * FROM players ORDER BY CAST(bal AS INTEGER) ASC LIMIT 10")
        val results = saveManager.executeMultipleQueryKey(preparedStatement, true)

        val playerArrayList = ArrayList<Player>()
        try {
            for (result in results) {
                val player = players[result]
                if (player != null) {
                    playerArrayList.add(player)
                }

            }
        } catch (e: SQLException) {
            e.printStackTrace()
        }

        // position
        val resultSet2 =
            saveManager.executeQuery("SELECT count(*) FROM players WHERE CAST(bal AS INTEGER) > ${pl["bal"]}", true)

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
        for (player in playerArrayList) {
            val user = api.getUserById(player.id).join()
            top = "${user.name} ${player["bal"]} ${Resource.RABBIT_COIN.show}\n${top}"
        }

        embed
            .setDescription(top)
            .setFooter("Vous êtes en position $position")

        slashCommand.createImmediateResponder()
            .addEmbed(embed)
            .respond()
    }
}