package io.github.alexiscomete.lapinousecond.view.discord.commands

import io.github.alexiscomete.lapinousecond.entity.entities.PlayerData
import io.github.alexiscomete.lapinousecond.entity.entities.PlayerManager
import io.github.alexiscomete.lapinousecond.entity.entities.PlayerWithAccount
import io.github.alexiscomete.lapinousecond.worlds.ServerBot
import io.github.alexiscomete.lapinousecond.worlds.servers
import org.javacord.api.entity.user.User
import org.javacord.api.interaction.SlashCommandInteraction

const val START_COMMAND = "/account start"

fun getAccount(slashCommandInteraction: SlashCommandInteraction): PlayerManager {
    try {
        return PlayerManager[slashCommandInteraction.user.id]
    } catch (e: Exception) {
        throw IllegalStateException("Vous devez avoir un compte pour utiliser cette commande. Utilisez $START_COMMAND")
    }
}

fun getAccount(id: Long): PlayerManager {
    try {
        return PlayerManager[id]
    } catch (e: Exception) {
        throw IllegalStateException("Vous devez avoir un compte pour utiliser cette commande. Utilisez $START_COMMAND")
    }
}

fun getAccount(user: User): PlayerWithAccount {
    try {
        return PlayerWithAccount(user)
    } catch (e: Exception) {
        throw IllegalStateException("Vous devez avoir un compte pour utiliser cette commande. Utilisez $START_COMMAND")
    }
}

interface ExecutableWithArguments {

    val fullName: String
    val botPerms: Array<String>?

    fun execute(slashCommand: SlashCommandInteraction)

    fun getCurrentServerBot(slashCommand: SlashCommandInteraction): ServerBot {
        val p: PlayerData = getAccount(slashCommand).playerData
        if (p["serv"] == "") {
            throw IllegalStateException("Impossible de trouver votre serveur actuel : utilisez /hub")
        }
        val servOp = slashCommand.server
        if (servOp.isPresent) {
            val serv = servOp.get()
            if (serv.id == p["serv"].toLong()) {
                return servers[serv.id]
                    ?: throw IllegalStateException("L'owner du server ou un admin doit utiliser le /config pour configurer le serveur")
            } else {
                throw WrongServerException("Utilisez cette commande dans un salon du serveur actuel : " + p["serv"].toLong() + ". Astuce : pour changer de serveur déplacez vous dans une ville située dans le serveur voulu avec /map. Si vous êtes actuellement dans le tuto, relisez les instructions.")
            }
        } else {
            throw IllegalStateException("Vous devez être dans un serveur discord pour utiliser cette commande. Voici votre serveur actuel : " + p["serv"].toLong())
        }
    }
}

class WrongServerException(message: String) : Exception(message)