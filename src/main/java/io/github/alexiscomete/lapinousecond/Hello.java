package io.github.alexiscomete.lapinousecond;

import org.javacord.api.event.message.MessageCreateEvent;

public class Hello extends CommandBot {

    public Hello() {
        super("Hello", "hello", "Say Hello World!");
    }

    @Override
    void execute(MessageCreateEvent messageCreateEvent, String content, String[] args) {
        messageCreateEvent.getMessage().reply("Hello World!");
    }
}
