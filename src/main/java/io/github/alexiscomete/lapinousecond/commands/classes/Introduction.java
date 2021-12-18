package io.github.alexiscomete.lapinousecond.commands.classes;

import io.github.alexiscomete.lapinousecond.commands.CommandBot;
import org.javacord.api.event.message.MessageCreateEvent;

public class Introduction extends CommandBot {

    public Introduction() {
        super("Présentation du bot", "intro", "Vous donne une description complète du bot et de son univers");
    }

    @Override
    public void execute(MessageCreateEvent messageCreateEvent, String content, String[] args) {

    }
}
