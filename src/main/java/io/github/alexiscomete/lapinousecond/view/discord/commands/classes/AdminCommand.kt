package io.github.alexiscomete.lapinousecond.view.discord.commands.classes

import io.github.alexiscomete.lapinousecond.data.managesave.saveManager
import io.github.alexiscomete.lapinousecond.data.transactions.auctions
import io.github.alexiscomete.lapinousecond.data.transactions.offers
import io.github.alexiscomete.lapinousecond.data.transactions.researches
import io.github.alexiscomete.lapinousecond.entity.concrete.items.Item
import io.github.alexiscomete.lapinousecond.entity.concrete.items.ItemData
import io.github.alexiscomete.lapinousecond.entity.entities.PlayerManager
import io.github.alexiscomete.lapinousecond.view.discord.commands.Command
import io.github.alexiscomete.lapinousecond.view.discord.commands.ExecutableWithArguments
import io.github.alexiscomete.lapinousecond.view.discord.commands.SubCommand
import io.github.alexiscomete.lapinousecond.worlds.buildings.buildings
import io.github.alexiscomete.lapinousecond.worlds.places
import io.github.alexiscomete.lapinousecond.worlds.servers
import org.javacord.api.interaction.SlashCommandInteraction
import org.javacord.api.interaction.SlashCommandOption

val logs = arrayListOf<String>()

class AdminCommandBase : Command(
    "admin",
    "Commandes réservées aux administrateurs. Pour le moment c'est juste l'owner",
    subCommands = listOf(
        AdminCommandExecuteSQL(),
        AdminCommandQuerySQL(),
        AdminCommandCache(),
        AdminCommandLogs()
    )
)

fun getDateString(): String {
    val date = java.time.LocalDateTime.now()
    return "${date.dayOfMonth}/${date.monthValue}/${date.year} (${date.hour}/${date.minute})"
}

class AdminCommandExecuteSQL : SubCommand(
    "executesql",
    "Tu n'as pas besoin de savoir",
    arrayListOf(
        SlashCommandOption.createStringOption(
            "sql_command",
            "La commande SQL à exécuter",
            true
        )
    )
), ExecutableWithArguments {
    override val fullName: String = "admin executesql"
    override val botPerms: Array<String>? = null

    override fun execute(slashCommand: SlashCommandInteraction) {
        if (slashCommand.user.isBotOwner) {
            val arguments = slashCommand.arguments
            try {
                val sqlCommand = arguments.first { it.name == "sql_command" }
                saveManager.execute(sqlCommand.stringValue.get())
                slashCommand.createImmediateResponder()
                    .setContent("Commande SQL exécutée")
                    .respond()
                logs.add(sqlCommand.stringValue.get() + " - JJ/MM/AA (HH/mm) : " + getDateString())
            } catch (e: NoSuchElementException) {
                slashCommand.createImmediateResponder()
                    .setContent("Tu dois mettre un argument `sql_command`")
                    .respond()
            }
        } else {
            slashCommand.createImmediateResponder()
                .setContent("Tu n'es pas le propriétaire du bot")
                .respond()
        }
    }
}

class AdminCommandQuerySQL : SubCommand(
    "querysql",
    "Tu n'as pas besoin de savoir",
    arrayListOf(
        SlashCommandOption.createStringOption(
            "sql_query",
            "La query SQL à exécuter",
            true
        )
    )
), ExecutableWithArguments {
    override val fullName: String = "admin querysql"
    override val botPerms: Array<String>? = null

    override fun execute(slashCommand: SlashCommandInteraction) {
        if (slashCommand.user.isBotOwner) {
            val arguments = slashCommand.arguments
            try {
                val sqlQuery = arguments.first { it.name == "sql_query" }
                val result = saveManager.executeMultipleQuery(
                    saveManager.preparedStatement(sqlQuery.stringValue.get())
                )
                // change result format
                var resultString = "Résultat de la query : \n"
                if (result.isEmpty()) {
                    resultString += "Aucun résultat"
                } else {
                    // première ligne : les noms des colonnes
                    val firstRow = result.first()
                    val char = '|'
                    resultString += "```\n$char "
                    for (i in firstRow.indices) {
                        resultString += firstRow[i]
                        resultString += (" $char ")
                    }
                    resultString += "\n"
                    // séparation
                    for (i in firstRow.indices) {
                        resultString += "----"
                    }
                    resultString += "\n"
                    // les autres lignes
                    for (i in 1 until result.size) {
                        val row = result[i]
                        resultString += "$char "
                        for (j in row.indices) {
                            resultString += row[j]
                            resultString += " $char "
                        }
                        resultString += ("\n")
                    }
                    resultString += "```"
                }
                slashCommand.createImmediateResponder()
                    .setContent(resultString)
                    .respond()
                logs.add(sqlQuery.stringValue.get() + " - JJ/MM/AA (HH/mm) : " + getDateString())
            } catch (e: NoSuchElementException) {
                slashCommand.createImmediateResponder()
                    .setContent("Tu dois mettre un argument `sql_query`")
                    .respond()
            }
        } else {
            slashCommand.createImmediateResponder()
                .setContent("Tu n'es pas le propriétaire du bot")
                .respond()
        }
    }
}

class AdminCommandCache : SubCommand(
    "cache",
    "Tu n'as pas besoin de savoir"
), ExecutableWithArguments {
    override val fullName: String = "admin cache"
    override val botPerms: Array<String>? = null

    override fun execute(slashCommand: SlashCommandInteraction) {
        if (slashCommand.user.isBotOwner) {
            servers.clearInsideCache()
            researches.clearInsideCache()
            auctions.clearInsideCache()
            offers.clearInsideCache()
            PlayerManager.clearInsideCache()
            buildings.clearInsideCache()
            Item.clearInsideCache()
            ItemData.clearInsideCache()
            places.clearInsideCache()
            slashCommand.createImmediateResponder()
                .setContent("Cache vidé")
                .respond()
            logs.add("Cache vidé - JJ/MM/AA (HH/mm) : " + getDateString())
        } else {
            slashCommand.createImmediateResponder()
                .setContent("Tu n'es pas le propriétaire du bot")
                .respond()
        }
    }
}

const val NUMBER_OF_LOGS = 20

class AdminCommandLogs : SubCommand(
    "logs",
    "Permet à n'importe qui d'afficher les logs des commandes admin"
), ExecutableWithArguments {
    override val fullName: String = "admin logs"
    override val botPerms: Array<String>? = null

    override fun execute(slashCommand: SlashCommandInteraction) {
        slashCommand.createImmediateResponder()
            .setContent("Logs des $NUMBER_OF_LOGS dernières commandes admin (si le bot redémarre, les logs sont ne sont pas sauvegardés) : \n```\n${logs.takeLast(NUMBER_OF_LOGS).joinToString("\n- ", "- ")}\n```")
            .respond()
    }
}
