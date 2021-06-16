package io.github.alexiscomete.lapinousecond;

import org.javacord.api.event.message.MessageCreateEvent;

public class Hello extends CommandBot{

    @Override
    void execute(MessageCreateEvent messageCreateEvent, String content, String[] args) {
        messageCreateEvent.getMessage().reply("Hello World!");
    }

    @Override
    public String getDescription() {
        return "Hello";
    }

    @Override
    public String getName() {
        return "hello";
    }

    @Override
    public String getTotalDescription() {
        return "Say Hello World!";
    }
}
