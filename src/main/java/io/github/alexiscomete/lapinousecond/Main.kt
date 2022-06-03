package io.github.alexiscomete.lapinousecond

import io.github.alexiscomete.lapinousecond.commands.CommandBot
import io.github.alexiscomete.lapinousecond.commands.classes.*
import io.github.alexiscomete.lapinousecond.message_event.ButtonsManager
import io.github.alexiscomete.lapinousecond.message_event.MessagesManager
import io.github.alexiscomete.lapinousecond.message_event.ReactionManager
import io.github.alexiscomete.lapinousecond.save.SaveLocation
import io.github.alexiscomete.lapinousecond.save.SaveManager
import io.github.alexiscomete.lapinousecond.save.Tables
import org.javacord.api.DiscordApi
import org.javacord.api.DiscordApiBuilder
import java.io.IOException

var saveManager: SaveManager? = null
    private set

val reactionManager: ReactionManager = ReactionManager()

val buttonsManager: ButtonsManager = ButtonsManager()

val messagesManager: MessagesManager = MessagesManager()

/**
 * Permet d'ajouter une commande à la liste pour qu'elle puisse être appelée
 * @param commandBot la commande
 */
private fun addCommand(commandBot: CommandBot) {
    ListenerMain.commands[commandBot.name] = commandBot
}

fun main() {

    try {

        /**
         * Configuration du bot
         */
        val config: SaveLocation<String> = SaveLocation(";", "/config.txt") { a: String -> a }

        println("RIP Lapinou premier")
        config.loadAll()
        /**
         * Instance de l'API Javacord
         */
        val api: DiscordApi = DiscordApiBuilder().setToken(config.content[0]).login().join()
        api.updateActivity("Prefix : -")
        api.addListener(ListenerMain())
        api.addListener(reactionManager)
        api.addListener(buttonsManager)
        api.addListener(messagesManager)
        saveManager = SaveManager(config.content[1])
        Tables.testTables()

        // Ajout des commandes
        addCommand(Help())
        addCommand(Work())
        addCommand(Shop())
        addCommand(Give())
        addCommand(PlayerShop())
        addCommand(InventoryC())
        addCommand(ConfigServ())
        addCommand(Travel())
        addCommand(Hub())
        addCommand(StartAdventure())
        addCommand(UseCommand())
        addCommand(PermsManager())
        addCommand(Invite())
        addCommand(Verify())
        addCommand(PlaceCommand())
        addCommand(BuildingCommand())
        addCommand(MapCommand())
        addCommand(WorldCommand())
        addCommand(TestCommand())
    } catch (e: IOException) {
        e.printStackTrace()
    }
}