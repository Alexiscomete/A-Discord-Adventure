package io.github.alexiscomete.lapinousecond;

import org.javacord.api.event.message.MessageCreateEvent;

public class Give extends CommandBot {
    public Give() {
        super("description", "give", "totalDescription");
    }

    @Override
    void execute(MessageCreateEvent messageCreateEvent, String content, String[] args) {

    }
}
