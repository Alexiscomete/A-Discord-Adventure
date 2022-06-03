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
import java.util.function.Function

object Main {
    /**
     * Instance de l'API Javacord
     */
    var api: DiscordApi? = null

    /**
     * Configuration du bot
     */
    var config: SaveLocation<String>? = null

    /**
     *
     * @return le gestionnaire de la base de données
     */
    @JvmStatic
    var saveManager: SaveManager? = null
        private set

    /**
     *
     * @return le gestionnaire des actions par réaction à un message
     */
    var reactionManager: ReactionManager? = null
        private set

    /**
     *
     * @return le gestionnaire des actions par utilisation d'un bouton sur un message
     */
    @JvmStatic
    var buttonsManager: ButtonsManager? = null
        private set

    /**
     *
     * @return le gestionnaire qui attend qu'une personne précise envoie un message dans un salon donné pour exécuter une action
     */
    var messagesManager: MessagesManager? = null
        private set

    //Ouverture du fichier de configuration
    init {
        try {
            config = SaveLocation(";", "/config.txt", Function { a: String? -> io.github.alexiscomete.lapinousecond.a })
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    /**
     * Initialisation du bot discord, des gestionnaires et des commandes
     * @param args habituel
     */
    @JvmStatic
    fun main(args: Array<String>) {
        println("RIP Lapinou premier")
        config!!.loadAll()
        api = DiscordApiBuilder().setToken(config!!.content[0]).login().join()
        api.updateActivity("Prefix : -")
        api.addListener(ListenerMain())
        reactionManager = ReactionManager()
        buttonsManager = ButtonsManager()
        messagesManager = MessagesManager()
        api.addListener(reactionManager)
        api.addListener(buttonsManager)
        api.addListener(messagesManager)
        saveManager = SaveManager(config!!.content[1])
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
    }

    /**
     * Permet d'ajouter une commande à la liste pour qu'elle puisse être appelée
     * @param commandBot la commande
     */
    fun addCommand(commandBot: CommandBot) {
        ListenerMain.commands[commandBot.name] = commandBot
    }
}