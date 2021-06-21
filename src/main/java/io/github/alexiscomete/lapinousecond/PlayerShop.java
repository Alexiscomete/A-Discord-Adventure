package io.github.alexiscomete.lapinousecond;

import org.javacord.api.event.message.MessageCreateEvent;

public class PlayerShop extends CommandBot {
    public PlayerShop() {
        super("description", "name", "totalDescription");
    }

    @Override
    void execute(MessageCreateEvent messageCreateEvent, String content, String[] args) {

    }
}
