package io.github.alexiscomete.lapinousecond

import io.github.alexiscomete.lapinousecond.commands.withslash.ListenerSlashCommands
import io.github.alexiscomete.lapinousecond.commands.withslash.loadAllS
import io.github.alexiscomete.lapinousecond.message_event.ButtonsManager
import io.github.alexiscomete.lapinousecond.message_event.MessagesManager
import io.github.alexiscomete.lapinousecond.message_event.ModalManager
import io.github.alexiscomete.lapinousecond.message_event.SelectMenuManager
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

val buttonsManager: ButtonsManager = ButtonsManager()
val messagesManager: MessagesManager = MessagesManager()
val modalManager: ModalManager = ModalManager()
val selectMenuManager: SelectMenuManager = SelectMenuManager()

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
        api.updateActivity("Prefix : -")
        api.addListener(ListenerSlashCommands())
        api.addListener(buttonsManager)
        api.addListener(messagesManager)
        api.addListener(modalManager)
        api.addListener(selectMenuManager)

        // Ajout des commandes
        loadAllS()
    } catch (e: IOException) {
        e.printStackTrace()
    }
}
