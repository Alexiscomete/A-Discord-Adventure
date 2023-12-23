package io.github.alexiscomete.lapinousecond

import io.github.alexiscomete.lapinousecond.data.managesave.SaveLocation
import io.github.alexiscomete.lapinousecond.data.managesave.SaveManager
import io.github.alexiscomete.lapinousecond.data.managesave.saveManager
import io.github.alexiscomete.lapinousecond.view.discord.commands.ListenerSlashCommands
import io.github.alexiscomete.lapinousecond.view.discord.commands.loadAllS
import io.github.alexiscomete.lapinousecond.view.discord.manager.ButtonsManager
import io.github.alexiscomete.lapinousecond.view.discord.manager.MessagesManager
import io.github.alexiscomete.lapinousecond.view.discord.manager.ModalManager
import io.github.alexiscomete.lapinousecond.view.discord.manager.SelectMenuManager
import org.javacord.api.DiscordApi
import org.javacord.api.DiscordApiBuilder
import org.javacord.api.entity.intent.Intent
import java.io.IOException

/**
 * Configuration du bot. Separated by ";"
 * 0. discord TOKEN
 * 1. BDD
 */
val config: SaveLocation<String> = SaveLocation(";", "/config.txt") { a: String -> a }

val messagesManager: MessagesManager = MessagesManager()

val api: DiscordApi = DiscordApiBuilder()
    .addIntents(Intent.MESSAGE_CONTENT)
    .setToken(run {
        config.loadAll()
        config.content[0]
    })
    .login().join()

fun main() {

    try {
        saveManager = SaveManager(config.content[1])

        println("RIP Lapinou premier")
        /**
         * Instance de l'API Javacord
         */
        api.updateActivity("Utilisez /account start pour commencer votre aventure ! ❤")
        api.addListener(ListenerSlashCommands())
        api.addListener(ButtonsManager())
        api.addListener(messagesManager)
        api.addListener(ModalManager())
        api.addListener(SelectMenuManager())

        // Ajout des commandes
        loadAllS()

        // Ajout des colonnes critiques dans la BDD
        saveManager.addCriticalColumns()
    } catch (e: IOException) {
        e.printStackTrace()
    }
}
