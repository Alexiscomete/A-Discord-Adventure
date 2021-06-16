package io.github.alexiscomete.lapinousecond;

import org.javacord.api.event.message.MessageCreateEvent;

public class InventoryC extends CommandBot {

    public InventoryC() {
        super("Ouvre l'inventaire", "inv", "Vous permet d'ouvrir votre inventaire et de voir votre avancement dans l'aventure !");
    }

    @Override
    void execute(MessageCreateEvent messageCreateEvent, String content, String[] args) {
        messageCreateEvent.getMessage().reply("L'inventaire est bientôt disponible");
    }

}
