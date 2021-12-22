package io.github.alexiscomete.lapinousecond.commands.classes;

import io.github.alexiscomete.lapinousecond.commands.CommandBot;
import org.javacord.api.event.message.MessageCreateEvent;

public class PlayerShop extends CommandBot {
    public PlayerShop() {
        super("Marché des joueurs", "playershop", "Vous pouvez vendre ici des items pour le prix que vous voulez (playershop sell). C'est ensuite aux autres joueurs de l'acheter (playershop buy et playershop list)");
    }

    @Override
    public void execute(MessageCreateEvent messageCreateEvent, String content, String[] args) {

    }
}
