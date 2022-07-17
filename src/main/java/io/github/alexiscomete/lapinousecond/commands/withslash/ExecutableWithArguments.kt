package io.github.alexiscomete.lapinousecond.commands.withslash

import io.github.alexiscomete.lapinousecond.entity.Player
import io.github.alexiscomete.lapinousecond.entity.players
import org.javacord.api.interaction.SlashCommandInteraction

interface ExecutableWithArguments {
    fun execute(slashCommand: SlashCommandInteraction)

    fun getCurrentServerBot(slashCommand: SlashCommandInteraction) {

    }

    fun getAccount(slashCommandInteraction: SlashCommandInteraction): Player {
        try {
            return players[slashCommandInteraction.user.id]
                ?: throw IllegalStateException("Vous devez avoir un compte pour utiliser cette commande. Utilisez -start")
        } catch (e: Exception) {
            throw IllegalStateException("Vous devez avoir un compte pour utiliser cette commande. Utilisez -start")
        }
    }
}