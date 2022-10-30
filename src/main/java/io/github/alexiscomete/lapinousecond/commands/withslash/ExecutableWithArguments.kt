package io.github.alexiscomete.lapinousecond.commands.withslash

import io.github.alexiscomete.lapinousecond.entity.Player
import io.github.alexiscomete.lapinousecond.entity.PlayerWithAccount
import io.github.alexiscomete.lapinousecond.entity.players
import io.github.alexiscomete.lapinousecond.worlds.ServerBot
import io.github.alexiscomete.lapinousecond.worlds.servers
import org.javacord.api.entity.user.User
import org.javacord.api.interaction.SlashCommandInteraction

fun getAccount(slashCommandInteraction: SlashCommandInteraction): Player {
    try {
        return players[slashCommandInteraction.user.id]
            ?: throw IllegalStateException("Vous devez avoir un compte pour utiliser cette commande. Utilisez /start")
    } catch (e: Exception) {
        throw IllegalStateException("Vous devez avoir un compte pour utiliser cette commande. Utilisez /start")
    }
}

fun getAccount(id: Long): Player {
    try {
        return players[id]
            ?: throw IllegalStateException("Vous devez avoir un compte pour utiliser cette commande. Utilisez /start")
    } catch (e: Exception) {
        throw IllegalStateException("Vous devez avoir un compte pour utiliser cette commande. Utilisez /start")
    }
}

fun getAccount(user: User): PlayerWithAccount {
    try {
        return PlayerWithAccount(user)
            ?: throw IllegalStateException("Vous devez avoir un compte pour utiliser cette commande. Utilisez /start")
    } catch (e: Exception) {
        throw IllegalStateException("Vous devez avoir un compte pour utiliser cette commande. Utilisez /start")
    }
}

interface ExecutableWithArguments {

    val fullName: String
    val botPerms: Array<String>?

    fun execute(slashCommand: SlashCommandInteraction)

    fun getCurrentServerBot(slashCommand: SlashCommandInteraction): ServerBot {
        val p: Player = getAccount(slashCommand)
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
                throw IllegalStateException("Utilisez cette commande dans un salon du serveur actuel : " + p["serv"].toLong())
            }
        } else {
            throw IllegalStateException("Vous devez être dans un serveur discord pour utiliser cette commande. Voici votre serveur actuel : " + p["serv"].toLong())
        }
    }
}