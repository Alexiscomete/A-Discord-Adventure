package io.github.alexiscomete.lapinousecond.commands.classes;

import io.github.alexiscomete.lapinousecond.commands.CommandBot;
import org.javacord.api.event.message.MessageCreateEvent;

public class Buy extends CommandBot {
    public Buy() {
        super("Acheter un item : list / item [name]", "buy", "Vous permet d'acheter des items / ressources classiques : 'list' pour voir la liste des items et 'item [name] <quantité>' pour acheter un item.");
    }

    @Override
    public void execute(MessageCreateEvent messageCreateEvent, String content, String[] args) {

    }
}