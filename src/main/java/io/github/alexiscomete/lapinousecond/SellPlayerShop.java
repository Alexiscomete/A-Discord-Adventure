package io.github.alexiscomete.lapinousecond;

import org.javacord.api.event.message.MessageCreateEvent;

public class SellPlayerShop extends CommandBot{
    public SellPlayerShop() {
        super("description", "name", "totalDescription");
    }

    @Override
    void execute(MessageCreateEvent messageCreateEvent, String content, String[] args) {

    }
}
