package io.github.alexiscomete.lapinousecond

import io.github.alexiscomete.lapinousecond.commands.withoutslash.CommandBot
import io.github.alexiscomete.lapinousecond.commands.withoutslash.ListenerMain
import io.github.alexiscomete.lapinousecond.commands.withoutslash.loadAll
import io.github.alexiscomete.lapinousecond.commands.withslash.ListenerSlashCommands
import io.github.alexiscomete.lapinousecond.commands.withslash.loadAllS
import io.github.alexiscomete.lapinousecond.message_event.ButtonsManager
import io.github.alexiscomete.lapinousecond.message_event.MessagesManager
import io.github.alexiscomete.lapinousecond.message_event.ReactionManager
import io.github.alexiscomete.lapinousecond.useful.managesave.SaveLocation
import io.github.alexiscomete.lapinousecond.useful.managesave.SaveManager
import io.github.alexiscomete.lapinousecond.useful.managesave.saveManager
import org.javacord.api.DiscordApi
import org.javacord.api.DiscordApiBuilder
import java.io.IOException

/**
 * Configuration du bot
 */
val config: SaveLocation<String> = SaveLocation(";", "/config.txt") { a: String -> a }

val reactionManager: ReactionManager = ReactionManager()

val buttonsManager: ButtonsManager = ButtonsManager()

val messagesManager: MessagesManager = MessagesManager()

val api: DiscordApi = DiscordApiBuilder().setToken(run {
    config.loadAll()
    config.content[0]
}).login().join()

/**
 * Permet d'ajouter une commande à la liste pour qu'elle puisse être appelée
 * @param commandBot la commande
 */
private fun addCommand(commandBot: CommandBot) {
    ListenerMain.commands[commandBot.name] = commandBot
}

fun main() {

    try {
        saveManager = SaveManager(config.content[1])

        println("RIP Lapinou premier")
        /**
         * Instance de l'API Javacord
         */
        api.updateActivity("Prefix : -")
        api.addListener(ListenerMain())
        api.addListener(ListenerSlashCommands())
        api.addListener(reactionManager)
        api.addListener(buttonsManager)
        api.addListener(messagesManager)

        // Ajout des commandes
        loadAll()
        loadAllS()
    } catch (e: IOException) {
        e.printStackTrace()
    }
}