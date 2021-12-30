package io.github.alexiscomete.lapinousecond.commands.classes;

import io.github.alexiscomete.lapinousecond.commands.CommandBot;
import org.javacord.api.event.message.MessageCreateEvent;

public class Shop extends CommandBot {
    public Shop() {
        super("Acheter / vendre un item", "shop", "Vous permet d'acheter ou de vendre des items / ressources classiques :\n- 'list' pour voir la liste des items\n- 'buy [name] <quantité>' pour acheter et 'sell [name] <quantité>' pour vendre\n- 'info [name]' pour avoir les informations sur un item\n");
    }

    @Override
    public void execute(MessageCreateEvent messageCreateEvent, String content, String[] args) {

    }
}
