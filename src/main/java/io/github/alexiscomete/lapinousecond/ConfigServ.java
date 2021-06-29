package io.github.alexiscomete.lapinousecond;

import org.javacord.api.event.message.MessageCreateEvent;

public class ConfigServ extends CommandBot {

    public ConfigServ() {
        super("Configuration de votre serveur", "config", "Permet de configurer le serveru actuel si c'est le votre");
    }

    @Override
    void execute(MessageCreateEvent messageCreateEvent, String content, String[] args) {
        if (messageCreateEvent.isServerMessage()) {
            if (messageCreateEvent.getMessageAuthor().isServerAdmin()) {
                System.out.println("Coming soon");
            } else {
                System.out.println("Bien essayé, mais vous ne pouvez pas configurer un serveur qui n'ets pas le votre");
            }
        } else {
            System.out.println("Comment voulez vous configurer un serveur sans être dans un serveur ?");
        }
    }
}
