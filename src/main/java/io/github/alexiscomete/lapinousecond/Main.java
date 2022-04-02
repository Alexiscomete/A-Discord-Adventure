package io.github.alexiscomete.lapinousecond;


import io.github.alexiscomete.lapinousecond.commands.CommandBot;
import io.github.alexiscomete.lapinousecond.commands.classes.*;
import io.github.alexiscomete.lapinousecond.message_event.ButtonsManager;
import io.github.alexiscomete.lapinousecond.message_event.MessagesManager;
import io.github.alexiscomete.lapinousecond.message_event.ReactionManager;
import io.github.alexiscomete.lapinousecond.save.SaveLocation;
import io.github.alexiscomete.lapinousecond.save.SaveManager;
import io.github.alexiscomete.lapinousecond.save.Tables;
import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;

import java.io.IOException;

public class Main {

    /**
     * Instance de l'API Javacord
     */
    public static DiscordApi api;
    /**
     * Configuration du bot
     */
    public static SaveLocation<String> config;

    /**
     *
     * @return le gestionnaire de la base de données
     */
    public static SaveManager getSaveManager() {
        return saveManager;
    }

    /**
     *
     * @return le gestionnaire des actions par réaction à un message
     */
    public static ReactionManager getReactionManager() {
        return reactionManager;
    }

    /**
     *
     * @return le gestionnaire des actions par utilisation d'un bouton sur un message
     */
    public static ButtonsManager getButtonsManager() {
        return buttonsManager;
    }

    /**
     *
     * @return le gestionnaire qui attend qu'une personne précise envoie un message dans un salon donné pour exécuter une action
     */
    public static MessagesManager getMessagesManager() {
        return messagesManager;
    }

    private static SaveManager saveManager;
    private static ReactionManager reactionManager;
    private static ButtonsManager buttonsManager;
    private static MessagesManager messagesManager;

    //Ouverture du fichier de configuration
    static {
        try {
            config = new SaveLocation<>(";", "/config.txt", a -> a);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Initialisation du bot discord, des gestionnaires et des commandes
     * @param args habituel
     */
    public static void main(String[] args) {
        System.out.println("RIP Lapinou premier");
        config.loadAll();
        api = new DiscordApiBuilder().setToken(config.getContent().get(0)).login().join();
        api.updateActivity("Prefix : -");
        api.addListener(new ListenerMain());
        reactionManager = new ReactionManager();
        buttonsManager = new ButtonsManager();
        messagesManager = new MessagesManager();
        api.addListener(reactionManager);
        api.addListener(buttonsManager);
        api.addListener(messagesManager);

        saveManager = new SaveManager(config.getContent().get(1));
        Tables.testTables();

        // Ajout des commandes
        addCommand(new Help());
        addCommand(new Hello());
        addCommand(new Work());
        addCommand(new Shop());
        addCommand(new Give());
        addCommand(new PlayerShop());
        addCommand(new InventoryC());
        addCommand(new ConfigServ());
        addCommand(new Travel());
        addCommand(new Hub());
        addCommand(new StartAdventure());
        addCommand(new UseCommand());
        addCommand(new PermsManager());
        addCommand(new Invite());
        addCommand(new Verify());
        addCommand(new RoleCommand());
        addCommand(new PlaceCommand());
        addCommand(new BuildingCommand());
        addCommand(new MapCommand());
    }

    /**
     * Permet d'ajouter une commande à la liste pour qu'elle puisse être appelée
     * @param commandBot la commande
     */
    public static void addCommand(CommandBot commandBot) {
        ListenerMain.commands.put(commandBot.getName(), commandBot);
    }
}
