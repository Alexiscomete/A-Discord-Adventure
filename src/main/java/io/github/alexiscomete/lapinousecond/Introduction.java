package io.github.alexiscomete.lapinousecond;

import org.javacord.api.event.message.MessageCreateEvent;

public class Introduction extends CommandBot {

    public Introduction() {
        super("Présentation du bot", "intro", "Vous donne une description complète du bot et de son univers");
    }

    @Override
    void execute(MessageCreateEvent messageCreateEvent, String content, String[] args) {

    }
}
