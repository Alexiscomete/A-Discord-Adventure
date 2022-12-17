package io.github.alexiscomete.lapinousecond

import io.github.alexiscomete.lapinousecond.view.discord.commands.ListenerSlashCommands
import io.github.alexiscomete.lapinousecond.view.discord.commands.loadAllS
import io.github.alexiscomete.lapinousecond.view.manager.ButtonsManager
import io.github.alexiscomete.lapinousecond.view.manager.MessagesManager
import io.github.alexiscomete.lapinousecond.view.manager.ModalManager
import io.github.alexiscomete.lapinousecond.view.manager.SelectMenuManager
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

val messagesManager: MessagesManager = MessagesManager()

val api: DiscordApi = DiscordApiBuilder().setToken(run {
    config.loadAll()
    config.content[0]
}).login().join()

// démarrage du bot
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
    } catch (e: IOException) {
        e.printStackTrace()
    }
}
