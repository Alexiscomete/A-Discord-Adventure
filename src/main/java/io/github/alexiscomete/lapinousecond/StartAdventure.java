package io.github.alexiscomete.lapinousecond;

import org.javacord.api.event.message.MessageCreateEvent;

public class StartAdventure extends CommandBot {

    public StartAdventure() {
        super("Permet de commencer l'aventure", "start", "Vous permet de créer votre compte sur le bot et de commencer l'aventure avec un tuto, vous pouvez réexécuter cette commande pour revoir le tuto (par exemple dans le cas d'une mise à jour importante)");
    }

    @Override
    void execute(MessageCreateEvent messageCreateEvent, String content, String[] args) {

    }
}
